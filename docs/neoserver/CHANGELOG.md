# Changelog

All notable API changes to **neoserver-admin** (the `:auth3` auth library and the
`:server` Ktor app) are documented in this file.

This log is written for two audiences:

- **`[BE]`** — backend services that depend on neoserver-admin
  (neostore, neomart, orpheum, neometrics, hecate).
- **`[FE]`** — frontend clients that call this service (e.g. the Kobweb app).

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Impact sections used:

- `### Breaking` — incompatible API change (major).
- `### Added` / `### Changed` / `### Deprecated` / `### Removed` — new or changed API (minor).
- `### Fixed` / `### Security` — backward-compatible fixes (patch).

Endpoints are listed relative to the service root (e.g. `/auth/login`,
`/bucket/v1/{bucket}/upload/raw`). `{bucket}` is one of `neomart-assets`,
`neotica-assets`, `neomart`, `neostore`, `orpheum`.

---

## [Unreleased]

### Changed

- `[ALL]` Unverified-account guidance: `/auth/register` duplicate responses and the
  `/auth/login` `403` now tell the user to check their email to verify their account
  (status codes unchanged).

---

## [1.3.0] - 2026-10-10

### Security

- `[ALL]` **`POST /auth/register` with an existing username/email now returns
  `409 Conflict`** with a field-specific message (`Username is already taken.` /
  `Email is already registered.`) instead of a raw `500` that leaked the database
  error, including the duplicate value.
- `[ALL]` **`GET /auth/login` with an unknown username now returns
  `401 Invalid username or password`** (was `404` with a placeholder message),
  removing a user-enumeration signal.
- `[ALL]` **`POST /auth/forgot-password` no longer reveals whether an email is
  registered** — it always returns `200`.
- `[ALL]` **Unhandled errors now return a generic `500 Internal server error`**;
  exception details (SQL, constraints, values) are logged server-side only.
- `[BE]` `[FE]` **S3 upload/delete/copy failures now return generic messages**
  instead of raw storage/SDK error text.

### Added

- `[ALL]` **`POST /bucket/v1/{bucket}/copy?source=<key>&dest=<key>`** — server-side
  copy of an object within a bucket via S3 `CopyObject`. Requires JWT (`auth-jwt`).
  The source object is retained (copy, not move). Available on every configured
  bucket. Returns `200` (`Success copied <source> -> <dest>`), `400` if `source`/`dest`
  is missing, `401` unauthenticated, `500` on failure.

### Changed

- `[ALL]` Error response bodies no longer include internal details: malformed
  request bodies return `400 Malformed request`, unsupported content types `415`,
  oversized payloads `413`, timeouts `504`. Clients that displayed raw server
  error text should show a generic failure state instead.

---

## [1.2.0] - 2026-07-24

### Breaking

- `[BE]` `[FE]` **`DELETE /bucket/v1/{bucket}/delete`** now requires JWT
  (`auth-jwt`) — previously unauthenticated. Send `Authorization: Bearer <accessToken>`.
- `[BE]` `[FE]` Removed the `auth-upload` static bearer scheme (token `"michaeljfox"`).
  Use the regular `auth-jwt` access token for S3 deletes.

### Added

- `[ALL]` **`DELETE /bucket/v1/{bucket}/delete/directory?prefix=<prefix>`** —
  recursive delete of all objects under a prefix via `listObjectsV2` +
  `deleteObjects` (paginated, 1000/batch). JWT required. Returns the deleted count.
- `[ALL]` **`GET /openapi.yaml`** — serves the bundled OpenAPI spec.

---

## [1.1.5] - 2026-07-24

### Added

- `[BE]` `[FE]` Published `openapi.yaml` — machine-readable API spec. No runtime API change.

---

## [1.1.4] - 2026-06-27

### Changed

- `[BE]` Internal only — Flyway downgraded to 10.22.0; AWS SDK version moved to the
  version catalog. No API impact.

---

## [1.1.3] - 2026-06-27

### Changed

- `[BE]` Internal only — Koin bumped to 4.2.1. No API impact.

---

## [1.1.2] - 2026-06-27

### Security

- `[FE]` Hashed passwords are no longer returned in user responses (`password` is
  blanked). Client code that read this field must be updated.
- `[BE]` Bumped Logback/Flyway to resolve known vulnerabilities.

### Added

- `[ALL]` **Rate limiting** on public routes. Exceeding a limit returns `429`
  with `Retry-After`, `X-RateLimit-Limit`, `X-RateLimit-Remaining` and
  `X-RateLimit-Reset` headers. Repeated violations escalate the block (15 min,
  doubling up to 24 h). Tiers: `strict` 5/15 min (`/auth/login`, `/auth/register`,
  `/auth/forgot-password`, `/auth/reset-password`, `DELETE /auth/`),
  `moderate` 10/15 min (`/auth/resend-verification`, `/auth/reload`, `/auth/check`),
  `relaxed` 30/15 min (`/bucket/v1/*`).

---

## [1.1.1] - 2026-06-27

### Changed

- `[BE]` Internal only — Exposed bumped to 1.1.1. No API impact.

---

## [1.1.0] - 2026-06-26

### Added

- `[ALL]` **`POST /auth/forgot-password`** — `Email` header; sends a reset link if
  the account exists (always returns `200`, no account enumeration).
- `[ALL]` **`POST /auth/reset-password`** — `Token` + `NewPassword` headers;
  resets the password.

---

## [1.0.0] - 2026-06-26

### Breaking

- `[ALL]` User routes moved from `/user/{...}` to `/auth/user` (JWT required).
- `[ALL]` Registration moved from `POST /auth/` to `POST /auth/register`.
- `[ALL]` `GET /auth/users` (list all users) was removed.

### Added

- `[ALL]` **`POST /auth/resend-verification`** — `Email` header; resends the
  verification email.
- `[ALL]` **`GET /auth/verify?token=<token>`** — verifies a user's email.
- `[ALL]` Email verification is now required before login.

### Changed

- `[BE]` Internal only — `:auth3` package/layout reorganized; `s3Route` moved to
  `id.neotica.s3`. No impact for HTTP consumers.

---

## [0.0.2] - 2026-05-30

### Breaking

- `[ALL]` Login moved from `GET /auth/` to `GET /auth/login`.

### Added

- `[ALL]` **`POST /bucket/v1/{bucket}/upload/raw`** — raw binary upload via channel
  (`X-File-Name` header, unlimited size).
- `[ALL]` **`POST /bucket/v1/{bucket}/upload/form`** — multipart form upload
  (max 50 MB).
- `[ALL]` **`DELETE /bucket/v1/{bucket}/delete?key=<key>`** — delete a single object.
- `[ALL]` Added the `orpheum` bucket.
- `[ALL]` JWT-protected `/buckets/{...}` proxy (SeaweedFS Filer) for weed files.

---

## [0.0.1] - 2026-02-21

### Added

- `[ALL]` Initial auth API under `/auth`: `GET /auth/` (login via `Username`/
  `Password` headers), `GET /auth/reload` (refresh tokens), `POST /auth/`
  (register), `DELETE /auth/` (`Id` header), `GET /auth/users`, `GET /auth/check`.
- `[ALL]` JWT access + refresh tokens (`auth-jwt` / `refresh-jwt`).
- `[ALL]` Initial S3-compatible file upload.