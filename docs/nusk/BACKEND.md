# Nusk Chat Service — Rust Implementation Spec

- **Status:** Draft v1 (Agent-Ready)
- **Service Name:** `nusk`
- **Owner:** Neoverse backend
- **Client:** Neoverse Android app
- **Target Stack:** Rust (2024 edition), Axum, Tokio, SQLx (PostgreSQL)

## 1. Scope

Nusk is a lightweight, standalone 1:1 plain-text chat service optimized for low RAM and CPU usage on a shared VPS.

### In Scope (v1)

- Exact-username user lookup via the auth service.
- 1:1 conversations (unique per user pair).
- Plain-text messages with client-side idempotency.
- REST conversation list, message history/backfill, and send.
- WebSocket live delivery with heartbeats and token-expiry enforcement.
- Per-user conversation hiding (unhides on new message).
- Account-deletion cleanup (internal server-side cascade + client fallback).

### Out of Scope (v1)

- Friend graph, friend requests, or contact lists.
- Group conversations.
- Push notifications.
- Avatars, image messages, or file uploads (`avatarUrl` is always `null`).
- Read receipts (keep `last_read_message_id` column nullable for future use).
- Message editing, reactions, or deletion for the other participant.
- End-to-end encryption.
- Multi-instance horizontal scaling / Redis pub-sub (single instance with in-memory registry for v1).

## 2. Service Boundaries & Routing

| Concern | Service | Public URL |
|---|---|---|
| Authentication | Existing auth service | `{AUTH_SERVICE_URL}` |
| Chat REST | Nusk | `{NUSK_BASE_URL}` |
| Chat WebSocket | Nusk | `{NUSK_WS_URL}` |

URLs are environment-specific placeholders:

- `{AUTH_SERVICE_URL}` — auth service base URL.
- `{NUSK_BASE_URL}` — public Nusk base including `/nusk/v1/`.
- `{NUSK_WS_URL}` — public Nusk WebSocket URL.

Endpoint sections below use service-root paths (`/v1/...`). The public client
base `{NUSK_BASE_URL}` already includes `/nusk/v1/`, so the Android client calls
`/health`, `/conversations`, etc.

Nusk is a resource server. It does not issue JWTs and does not share a database with the auth service.

```text
Android app
  ├── POST/GET auth ──────────────► {AUTH_SERVICE_URL}
  └── REST + WebSocket ───────────► {NUSK_BASE_URL} (proxy strips /nusk)
                                         │
                                         ├── validates auth access JWT
                                         ├── Postgres: chat_users, conversations, direct_conversations,
                                         │             conversation_members, messages
                                         └── auth3 lookup ──► {AUTH_SERVICE_URL}
```

**Reverse Proxy Convention:** The reverse proxy terminates TLS, upgrades WebSockets, forwards `/nusk/*` to Nusk with the `/nusk` prefix stripped, and **blocks all public access to `/internal/*`**. Therefore, Nusk binds routes at `/v1/*` and `/internal/*`.

## 3. Tech Stack & Engineering Guardrails

### Required Crates

- **Web & WS:** `axum` (0.8+, with `ws` feature), `tower`, `tower-http` (tracing, timeout, request body limit)
- **Async Runtime:** `tokio` (`macros`, `rt-multi-thread`, `signal`, `time`, `sync`)
- **Database:** `sqlx` (Postgres, `runtime-tokio`, `tls-rustls`, `uuid`, `chrono`, `migrate`) — replaces Flyway via embedded `sqlx::migrate!("./migrations")`
- **Serialization:** `serde`, `serde_json` (all API structs use `#[serde(rename_all = "camelCase")]`; timestamps serialized as Unix epoch **milliseconds** `i64`)
- **Auth:** `jsonwebtoken` (supports HMAC via `JWT_SECRET` or JWKS/public key if configured later)
- **HTTP Client:** `reqwest` (with `rustls-tls`, `json`, `default-features = false` — **no OpenSSL/native-tls**)
- **IDs & Time:** `uuid` (`v4`, `v7`, `serde`), `chrono` (`serde`)
- **Concurrency & Rate Limiting:** `dashmap` (connection hub), `moka` (`future` feature) or `governor` with bounded/TTL eviction
- **Errors & Logging:** `thiserror`, `tracing`, `tracing-subscriber` (`env-filter`, `json` optional)

### Strict Performance & Safety Rules

1. **No blocking calls on Tokio threads:** Never use `std::thread::sleep` or synchronous I/O in async handlers.
2. **Never hold locks across `.await`:** When accessing the WebSocket registry (`DashMap` or `RwLock`), clone the `mpsc::Sender` handles, drop the guard immediately, and then send.
3. **Bounded memory everywhere:**
   - Every WebSocket connection uses a bounded outbound channel (`tokio::sync::mpsc::channel(64)`). If a slow consumer's channel is full (`try_send` returns `Full`), drop/close that socket rather than buffering unbounded memory.
   - Rate-limit state MUST evict inactive users automatically (TTL + max capacity) so memory does not leak over time.
   - Default Postgres pool size is small (`DATABASE_MAX_CONNECTIONS`, default `8`).
4. **No OpenSSL dependency:** Use `rustls` everywhere so Linux production builds compile cleanly without system `libssl`.
5. **No `unwrap()` or `expect()` in request/WebSocket paths:** Convert all runtime failures into typed `AppError` responses or clean socket closes.

## 4. Recommended Project Structure

```text
nusk/
├── Cargo.toml
├── Dockerfile
├── migrations/
│   └── 0001_init.sql
├── src/
│   ├── main.rs                 # Entry point, tracing, pool, migrations, graceful shutdown
│   ├── lib.rs                  # App builder (for integration tests)
│   ├── config.rs               # Typed env var loader
│   ├── error.rs                # AppError enum + Axum IntoResponse
│   ├── models.rs               # API DTOs (camelCase, epoch millis)
│   ├── state.rs                # Shared AppState (PgPool, Hub, AuthClient, RateLimiter, Config)
│   ├── auth/
│   │   ├── mod.rs
│   │   ├── jwt.rs              # AuthUser extractor + JWT verification
│   │   ├── service_token.rs    # ServiceAuth extractor for /internal/* (constant-time compare)
│   │   └── client.rs           # Reqwest client for auth3 checks and username lookups
│   ├── db/
│   │   ├── mod.rs
│   │   ├── conversations.rs    # Inbox, create/get 1:1, hide, membership check
│   │   ├── messages.rs         # Send (idempotent), cursor pagination, backfill
│   │   └── users.rs            # chat_users upsert & account deletion cascade
│   ├── rate_limit.rs           # Bounded per-user sliding/token-bucket limiter
│   ├── routes/
│   │   ├── mod.rs              # Router assembly
│   │   ├── health.rs           # GET /v1/health
│   │   ├── conversations.rs    # GET/POST /v1/conversations, DELETE /v1/conversations/{id}
│   │   ├── messages.rs         # GET/POST /v1/conversations/{id}/messages
│   │   ├── me.rs               # DELETE /v1/me
│   │   └── internal.rs         # DELETE /internal/users/{id}
│   └── ws/
│       ├── mod.rs
│       ├── hub.rs              # In-memory user -> connections registry + broadcast
│       └── handler.rs          # WS upgrade, 30s ping, JWT exp timer (4401), shutdown (4410)
└── tests/
    └── integration.rs          # End-to-end acceptance tests (mock auth server + Postgres)
```

## 5. Authentication & Internal Routes

### Client JWT Validation

Every `/v1/*` route (except `/v1/health`) and `/v1/ws` requires:

```http
Authorization: Bearer <accessToken>
```

Nusk validates:

- Signature & allowed algorithm (explicitly configured via `JWT_ALGORITHM`, default `HS256`, using `JWT_SECRET`).
- `exp` (not expired).
- `iss` (validated if `JWT_ISSUER` is non-empty).
- Identity:
  - `id` claim (or `sub` fallback): valid UUID string (caller's `user_id`).
  - `username`: optional. If missing, Nusk resolves the caller through auth3 and caches the result.

Expose `AuthUser { user_id: Uuid, exp: i64, ... }` as an Axum `FromRequestParts` extractor.

### Auth3 Lookups (Outbound from Nusk)

Nusk uses auth3 instead of dedicated internal auth routes:

```http
GET {AUTH_SERVICE_URL}/auth/check
Username: <username>
GET {AUTH_SERVICE_URL}/auth/user/username
```

- `/auth/check` resolves an exact target username.
- `/auth/user/username` resolves the caller when the JWT has no username claim.
- Cache lookup results to respect auth3 rate limits.
- `404` from `/auth/check` returns `404 USER_NOT_FOUND` to the caller.
- `5xx`/timeout returns `500 INTERNAL_ERROR`.
- No `/internal/auth/*` routes are required.

### Internal Cleanup Route (Inbound to Nusk)

```http
DELETE /internal/users/{id}
X-Service-Token: <SERVICE_TOKEN>
```

- Protected by constant-time comparison against `SERVICE_TOKEN`; return `401 UNAUTHORIZED` if missing/invalid.
- Runs the exact same user deletion logic as `DELETE /v1/me`.
- Returns `204 No Content` (idempotent even if user has no chat data).

## 6. Database Schema (PostgreSQL + SQLx Migrations)

Place in `migrations/0001_init.sql`. Run automatically on startup when `RUN_MIGRATIONS=true` (default `true`).

```sql
create table chat_users (
    user_id    uuid primary key,
    username   text not null,
    updated_at timestamptz not null default now()
);

create table conversations (
    id                   uuid primary key,
    last_message_preview text,
    created_at           timestamptz not null default now(),
    updated_at           timestamptz not null default now()
);

-- Enforces exactly one 1:1 conversation per unordered user pair at the DB level
create table direct_conversations (
    conversation_id uuid primary key references conversations(id) on delete cascade,
    user_low        uuid not null,
    user_high       uuid not null,
    constraint direct_conversations_ordered_pair check (user_low < user_high),
    constraint direct_conversations_unique_pair unique (user_low, user_high)
);

create table conversation_members (
    conversation_id      uuid not null references conversations(id) on delete cascade,
    user_id              uuid not null,
    hidden_at            timestamptz,
    last_read_message_id uuid,
    primary key (conversation_id, user_id)
);

-- Fast inbox lookup for visible conversations
create index conversation_members_user_visible_idx
    on conversation_members (user_id)
    where hidden_at is null;

-- Fast lookup for account deletion and all-membership checks
create index conversation_members_user_idx
    on conversation_members (user_id);

create table messages (
    id                uuid primary key, -- Server-generated UUIDv7 (time-ordered)
    conversation_id   uuid not null references conversations(id) on delete cascade,
    sender_id         uuid not null,
    client_message_id uuid not null,
    body              text not null,
    created_at        timestamptz not null default now(),
    unique (conversation_id, client_message_id)
);

-- UUIDv7 ids are monotonically time-ordered; supports fast before/after range scans
create index messages_conversation_id_idx
    on messages (conversation_id, id desc);
```

### Schema Implementation Notes

- **Message IDs:** Generate `Uuid::now_v7()` on the server. Use `id` directly for `before` and `after` cursor range queries.
- **`last_message_preview`:** Denormalized preview (first 200 Unicode scalar values of `body`) updated inside the same transaction as `conversations.updated_at` when a message is inserted. Eliminates `LATERAL` joins on `GET /v1/conversations`.
- **1:1 Pair Ordering:** Always compute `(user_low, user_high) = if a < b { (a, b) } else { (b, a) }` in application code before inserting/querying `direct_conversations`.
- **Deleted Peer Fallback:** When a peer deletes their account, their `chat_users` row is removed, but the surviving member still owns the conversation and messages. Derive the peer's UUID from `direct_conversations` (`user_low` or `user_high`) and `LEFT JOIN chat_users`; if no `chat_users` row exists, return `username: "deleted_user"` (or empty fallback) rather than failing the query.

## 7. REST API Contract

All JSON timestamps (`createdAt`, `updatedAt`) are Unix epoch **milliseconds** (`i64`).

### `GET /v1/health`

Public. Verifies Postgres connectivity (`SELECT 1`).

- `200 OK`: `{ "status": "ok" }`
- `503 Service Unavailable`: `{ "code": "INTERNAL_ERROR", "message": "Database unavailable" }`

### `GET /v1/conversations`

Requires `AuthUser`. Returns visible (`hidden_at IS NULL`) conversations for the caller, ordered by `conversations.updated_at DESC, conversations.id DESC`.

```json
{
  "data": [
    {
      "id": "7d2e4af0-9f43-4c7d-b2c8-6a5f0f2f1c11",
      "peer": {
        "id": "e7e4b3bd-4f5f-4a9d-8df2-7ee5c3e9b3e1",
        "username": "jdoe",
        "avatarUrl": null
      },
      "lastMessage": "see you tomorrow",
      "createdAt": 1780000000000,
      "updatedAt": 1780000005000
    }
  ]
}
```

### `POST /v1/conversations`

Requires `AuthUser`. Rate limit: **10 req / min / user**.

Request:

```json
{ "username": "jdoe" }
```

Behavior:

1. Trim username; if empty, return `400 INVALID_REQUEST`.
2. Lookup target user via auth service internal API (`GET /internal/auth/users/by-username/{username}`).
3. If `404`, return `404 USER_NOT_FOUND`.
4. If target `id == caller.user_id`, return `400 SELF_CONVERSATION`.
5. In a DB transaction:
   - Compute `(user_low, user_high)`.
   - Upsert both caller and target into `chat_users`.
   - Check if `direct_conversations` already exists for `(user_low, user_high)`.
   - If it exists, ensure caller is in `conversation_members` and set caller's `hidden_at = NULL` (unhide if previously hidden), then return it.
   - If not, create `conversations`, `direct_conversations`, and both `conversation_members` rows. Handle concurrent insert unique violation on `(user_low, user_high)` by re-selecting the winning conversation rather than failing with 500.
6. Return `200 OK` with the single conversation object (same shape as an item in `GET /v1/conversations`).

### `GET /v1/conversations/{conversationId}/messages`

Requires `AuthUser`. Verify caller is in `conversation_members` (if conversation does not exist, `404 CONVERSATION_NOT_FOUND`; if exists and caller is not a member, `403 NOT_A_MEMBER`).

Query parameters:

- `before`: optional message UUID (returns messages with `id < before`, ordered `id DESC`).
- `after`: optional message UUID (returns messages with `id > after`, ordered `id ASC`, used for reconnect backfill).
- `limit`: optional integer, default `50`, clamped/validated to `1..=100` (return `400 INVALID_REQUEST` if `0` or `> 100`).
- Reject if **both** `before` and `after` are provided (`400 INVALID_REQUEST`).

Fetch `limit + 1` rows to determine whether more rows exist:

- If `limit + 1` rows were returned, trim to `limit` and set `nextCursor` to the last returned message's `id`.
- Otherwise, `nextCursor` is `null`.

Response (`200 OK`):

```json
{
  "data": [
    {
      "id": "01950f8d-8f3c-764b-9fc1-7b2f4f1a1c22",
      "conversationId": "7d2e4af0-9f43-4c7d-b2c8-6a5f0f2f1c11",
      "senderId": "e7e4b3bd-4f5f-4a9d-8df2-7ee5c3e9b3e1",
      "body": "see you tomorrow",
      "createdAt": 1780000005000
    }
  ],
  "nextCursor": "01950f8d-8f3c-764b-9fc1-7b2f4f1a1c22"
}
```

### `POST /v1/conversations/{conversationId}/messages`

Requires `AuthUser`. Rate limit: **30 req / min / user**.

Request:

```json
{
  "clientMessageId": "1cbfcd09-8c4f-4a3e-b98d-5e4a9e43a001",
  "body": "see you tomorrow"
}
```

Rules:

1. Trim `body`. Must be `1..=4000` characters (`.chars().count()`). Empty -> `400 EMPTY_MESSAGE`; over 4000 -> `400 INVALID_REQUEST`.
2. Verify conversation exists (`404 CONVERSATION_NOT_FOUND`) and caller is a member (`403 NOT_A_MEMBER`).
3. Inside a DB transaction:
   - Generate server `id = Uuid::now_v7()`, `created_at = Utc::now()`, and use `sender_id = caller.user_id`.
   - Upsert caller's `chat_users` row (keeps username fresh).
   - Insert into `messages` with `ON CONFLICT (conversation_id, client_message_id) DO NOTHING RETURNING ...`.
   - **If conflict (duplicate `clientMessageId`):** fetch and return the existing message with **`200 OK`**. Do **not** re-broadcast WebSocket events.
   - **If newly inserted:**
     - Update `conversations` setting `updated_at = created_at` and `last_message_preview = <first 200 chars of body>`.
     - Unhide conversation for all members (`UPDATE conversation_members SET hidden_at = NULL WHERE conversation_id = $1 AND hidden_at IS NOT NULL`).
     - Commit transaction **before** broadcasting.
4. After commit, fetch current member user IDs for the conversation and broadcast to each member's connected WebSocket sessions:
   - `message.created`
   - `conversation.updated` (tailored per recipient so each user sees the *other* participant in `peer`)
5. Return `201 Created` (or `200 OK` on duplicate) with the message object.

### `DELETE /v1/conversations/{conversationId}`

Requires `AuthUser`. Caller must be a member (`404`/`403` as above).

- Sets `hidden_at = now()` on `conversation_members` for `(conversationId, caller.user_id)` only.
- Returns `204 No Content`.

### `DELETE /v1/me`

Requires `AuthUser`.

Runs shared `delete_user_chat_data(user_id)`:

1. In a DB transaction:
   - `DELETE FROM conversation_members WHERE user_id = $1`
   - `DELETE FROM conversations c WHERE NOT EXISTS (SELECT 1 FROM conversation_members m WHERE m.conversation_id = c.id)` (cascades to `messages` and `direct_conversations`)
   - `DELETE FROM chat_users WHERE user_id = $1`
2. Close all active WebSocket connections for `user_id` with close code `4403`.
3. Return `204 No Content`.

## 8. WebSocket Contract

### Endpoint & Handshake

```text
{NUSK_WS_URL}
Authorization: Bearer <accessToken>
```

- Validate JWT during HTTP upgrade handshake before upgrading.
  - Missing/invalid/expired token -> HTTP `401 UNAUTHORIZED` (do not upgrade).
- Enforce `MAX_CONNECTIONS_PER_USER` (default `5`):
  - If the user already has `MAX_CONNECTIONS_PER_USER` open sockets, reject the upgrade with HTTP `429 RATE_LIMITED` (with `Retry-After: 5`).
- No per-conversation subscription protocol: once connected, the server pushes events for every conversation the user belongs to.

### Outbound Server Events (JSON Text Frames)

`message.created`:

```json
{
  "type": "message.created",
  "conversationId": "7d2e4af0-9f43-4c7d-b2c8-6a5f0f2f1c11",
  "message": {
    "id": "01950f8d-8f3c-764b-9fc1-7b2f4f1a1c22",
    "conversationId": "7d2e4af0-9f43-4c7d-b2c8-6a5f0f2f1c11",
    "senderId": "e7e4b3bd-4f5f-4a9d-8df2-7ee5c3e9b3e1",
    "body": "see you tomorrow",
    "createdAt": 1780000005000
  }
}
```

`conversation.updated`:

```json
{
  "type": "conversation.updated",
  "conversation": {
    "id": "7d2e4af0-9f43-4c7d-b2c8-6a5f0f2f1c11",
    "peer": {
      "id": "e7e4b3bd-4f5f-4a9d-8df2-7ee5c3e9b3e1",
      "username": "jdoe",
      "avatarUrl": null
    },
    "lastMessage": "see you tomorrow",
    "createdAt": 1780000000000,
    "updatedAt": 1780000005000
  }
}
```

### Lifecycle, Heartbeats & Close Codes

- **Heartbeat:** Send a protocol-level WebSocket `Ping` frame every **30 seconds**. If no `Pong` (or any client frame) is received within **60 seconds**, close and clean up the dead socket.
- **Active Token Expiry:** Schedule a Tokio timer for the JWT's `exp` timestamp. When it fires during an active session, send a WebSocket `Close` frame with code `4401` and close the connection.
- **Graceful Shutdown:** Listen for `SIGINT`/`SIGTERM`. Broadcast shutdown to the hub, send `Close(4410)` to all active sockets, allow up to 5 seconds for clean drains, then exit.
- **Cleanup Guarantee:** Always remove the connection handle from the hub on every exit path (normal close, error, slow consumer, timeout, panic).

| Close Code | Meaning |
|---|---|
| `1000` | Normal close |
| `4401` | Token expired / invalid |
| `4403` | User deleted or no longer allowed |
| `4410` | Server shutting down; client reconnects with jittered backoff |

## 9. Security, Rate Limiting & Error Format

### Rate Limits

Return `429 Too Many Requests` with header `Retry-After: <seconds>` and JSON error code `RATE_LIMITED`:

- `POST /v1/conversations`: **10 / min / user**
- `POST /v1/conversations/{id}/messages`: **30 / min / user**
- `GET /v1/ws` (concurrent): **`MAX_CONNECTIONS_PER_USER`** (default `5`)

### Request Size Limits

- Apply a strict global HTTP body limit (e.g., `64 KiB`) via `tower_http::limit::RequestBodyLimitLayer` to prevent memory abuse.

### Standard JSON Error Format

```json
{
  "code": "CONVERSATION_NOT_FOUND",
  "message": "Conversation not found"
}
```

| HTTP Status | Code Examples |
|---|---|
| `400` | `INVALID_REQUEST`, `SELF_CONVERSATION`, `EMPTY_MESSAGE` |
| `401` | `UNAUTHORIZED` |
| `403` | `NOT_A_MEMBER` |
| `404` | `USER_NOT_FOUND`, `CONVERSATION_NOT_FOUND` |
| `409` | `CONVERSATION_CONFLICT` |
| `429` | `RATE_LIMITED` |
| `500` | `INTERNAL_ERROR` |

## 10. Configuration (Environment Variables)

| Variable | Required | Default | Purpose |
|---|---|---|---|
| `HOST` | No | `0.0.0.0` | Bind address |
| `PORT` | No | `8078` | HTTP port |
| `DATABASE_URL` | **Yes** | — | Postgres URL (`postgres://user:pass@host:5432/nusk`) |
| `DATABASE_MAX_CONNECTIONS` | No | `8` | SQLx pool max connections |
| `RUN_MIGRATIONS` | No | `true` | Run embedded SQLx migrations on boot |
| `JWT_SECRET` | **Yes** | — | Shared HMAC secret for access tokens |
| `JWT_ALGORITHM` | No | `HS256` | JWT signing algorithm (`HS256`, `HS384`, `HS512`) |
| `JWT_ISSUER` | No | `""` | Expected `iss` claim if non-empty |
| `AUTH_SERVICE_URL` | **Yes** | — | auth3 base URL |
| `SERVICE_TOKEN` | **Yes** | — | Shared secret for `X-Service-Token` |
| `MAX_CONNECTIONS_PER_USER` | No | `5` | Max concurrent WebSockets per user |
| `RUST_LOG` | No | `nusk=info,tower_http=info` | Tracing filter |

## 11. Dockerfile & Deployment Requirements

Provide a multi-stage `Dockerfile`:

- **Builder:** `rust:1-slim` (release build with `strip = true`, `lto = "thin"` in `[profile.release]`).
- **Runtime:** `debian:bookworm-slim` or `gcr.io/distroless/cc-debian12` with `ca-certificates`, running as a non-root user.
- Target runtime RAM under 25 MB at idle.

## 12. Agent Execution & Verification Checklist

Implement in this order, compiling and checking at each step:

1. **Bootstrap:** `Cargo.toml`, `config.rs`, `error.rs`, `migrations/0001_init.sql`, `main.rs`, `GET /v1/health`.
2. **Auth:** `auth/jwt.rs` (`AuthUser`), `auth/service_token.rs`, `auth/client.rs`.
3. **REST Core:** `db/` queries + `conversations`, `messages`, `me`, and `internal` routes + `rate_limit.rs`.
4. **WebSocket Layer:** `ws/hub.rs` + `ws/handler.rs` (heartbeat, `4401` exp timer, `4410` graceful shutdown, fan-out after DB commit).
5. **Automated Acceptance Tests (`tests/integration.rs`):**
   Use a local mock HTTP server (e.g., `axum` or `wiremock`) for `AUTH_SERVICE_URL` and a test Postgres DB to verify all 10 cases:
   - [ ] Valid JWT connects to `/v1/ws`; invalid/expired JWT rejected (`401`).
   - [ ] `POST /v1/conversations` creates a 1:1 conversation and returns the exact same ID on repeat or reversed-user calls; rejects self-conversation (`400`) and unknown users (`404`).
   - [ ] Two members send and receive messages over REST and receive `message.created` + `conversation.updated` over WebSocket (with recipient-specific `peer`).
   - [ ] Non-member receives `403 NOT_A_MEMBER` on message read/write and hide.
   - [ ] Duplicate `clientMessageId` returns `200` with the original message and does not insert a duplicate or double-broadcast.
   - [ ] `before` and `after` cursor pagination return accurate slices and `nextCursor`.
   - [ ] `DELETE /v1/conversations/{id}` hides only for the caller, and a new message unhides it for both.
   - [ ] `DELETE /v1/me` and `DELETE /internal/users/{id}` remove the user's membership and `chat_users`, delete orphaned conversations/messages, keep conversations with a remaining member intact, and close active sockets with `4403`.
   - [ ] Expiring JWT during an open WebSocket session closes with `4401`; server shutdown closes with `4410`.
   - [ ] Rate limits return `429` with `Retry-After` on conversation creation, message sending, and exceeding `MAX_CONNECTIONS_PER_USER`.
6. **Final Quality Gate:**
   - `cargo fmt --check` passes cleanly.
   - `cargo clippy --all-targets -- -D warnings` passes with zero warnings.
   - `cargo test` passes completely.
