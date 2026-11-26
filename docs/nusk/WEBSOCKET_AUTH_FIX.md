# Nusk WebSocket Auth — Backend Fix

## Status
Open: WebSocket live delivery is broken.

## Symptom
The Android client opens a WebSocket to `{NUSK_WS_URL}`.
The upgrade is rejected with `400 Bad Request`, so messages only appear
after leaving and re-entering the chat, which triggers a REST history fetch.

## Evidence
- No `Authorization` header -> `401 UNAUTHORIZED`.
- Invalid token -> `401 UNAUTHORIZED`.
- Valid access token accepted by REST (`/v1/conversations`) -> `400` on
  the WebSocket upgrade.
- The Android client now uses a WebSocket-capable Ktor OkHttp engine.
- Therefore the failure is after Nusk authentication, during or after
  the WebSocket upgrade path. It is not an Android engine limitation.

## Expected contract
- WebSocket handshake requires `Authorization: Bearer <accessToken>`.
- Caller identity is the UUID in the `id` claim, or `sub` as fallback.
- `username` is optional.
- If `username` is absent, Nusk resolves it through
  `{AUTH_SERVICE_URL}/auth/user/username`.
- Missing, invalid, or expired auth -> `401 UNAUTHORIZED`.
- `400` is reserved for a malformed WebSocket upgrade request only.
- `429 RATE_LIMITED` is used for too many concurrent sockets.
- `500 INTERNAL_ERROR` is used for auth3 lookup or internal failures.

## Required Nusk changes
1. Use the same `AuthUser` extractor for REST and `/v1/ws`.
2. Accept `id` or `sub`; validate the UUID.
3. Resolve a missing `username` through auth3 and cache it.
4. Authenticate before `WebSocketUpgrade`.
5. Do not require a `username` claim.
6. Return `401`, not `400`, for JWT/claim/auth failures.
7. Log the following without logging tokens:
   - whether `Authorization` is present
   - claim names present
   - whether `/auth/user/username` was called
   - whether rejection came from auth or upgrade handling
8. Add WebSocket auth tests.

## Required Neoserver changes
Prefer none.

Only change Neoserver if the access token contains neither `id` nor `sub`.
In that case, add a stable UUID claim (`sub` preferred) to the access token.

Do not add `/internal/auth/*` routes. Nusk should use:
- `{AUTH_SERVICE_URL}/auth/check`
- `{AUTH_SERVICE_URL}/auth/user/username`

## Proxy check
If valid-token `curl` also returns `400`, verify the reverse proxy forwards:
- `Authorization`
- `Connection: Upgrade`
- `Upgrade: websocket`
- `Sec-WebSocket-Version`
- `Sec-WebSocket-Key`

## Acceptance tests
- Token with `sub` only, no `username` -> `101`.
- Token with `id` only -> `101`.
- Missing token -> `401`.
- Invalid/expired token -> `401`.
- Malformed upgrade headers -> `400`.
- More than `MAX_CONNECTIONS_PER_USER` -> `429` with `Retry-After`.
- Two users connected -> both receive `message.created`.
- Missing username -> auth3 lookup works and is cached.
- Auth3 failure -> `500`, not `400`.

## Definition of done
- Valid-token WebSocket handshake returns `101`.
- Two open chat screens receive messages live.
- WebSocket auth failures return `401`, never `400`.
- `docs/nusk/BACKEND.md` and `docs/nusk/openapi.yaml` agree with the above.
- Nusk CHANGELOG records the fix.
- `cargo fmt --check`, `cargo clippy --all-targets -- -D warnings`, and
  `cargo test` pass.
