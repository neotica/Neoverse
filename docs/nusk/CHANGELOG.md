# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-11-26

### Added
- Full 1:1 chat v1 implementation with Axum, Tokio, and SQLx/Postgres.
- REST conversations and messages APIs: inbox, create/reopen, hide, history, and idempotent send.
- WebSocket live delivery with heartbeats, per-user connection limits, token-expiry enforcement (4401), account-deletion closes (4403), and shutdown closes (4410).
- Cursor pagination (`before`/`after`), per-user conversation hiding with automatic unhide on new messages, and account-deletion cascade.
- JWT authentication, constant-time service-token protection for `/internal/*`, and auth-service lookup caching.
- Configurable per-user rate limits and a 64 KiB request body limit.
- SQLx migration `0001_init.sql`, embedded at startup when `RUN_MIGRATIONS=true`.
- Integration suite covering all acceptance cases from `BACKEND.md` §12.5 against real Postgres with a mock auth server.
- Multi-stage Dockerfile and a valid OpenAPI 3.0 contract in `openapi.yaml`.
- Local `.env` loading via `dotenvy`.

### Changed
- Default HTTP port is now `8078` (spec updated).
- Local development uses the `db_nusk` database inside the existing `neoserver` Postgres container.
- Adapted authentication to neoserver/auth3: accepts the `id` JWT claim, resolves usernames via `/auth/user/username`, and performs target lookups via `/auth/check` with caching to respect auth3 rate limits.
- Replaced the previous markdown copy in `openapi.yaml` with a real OpenAPI 3.0 document.

### Fixed
- Graceful shutdown no longer fires after 15 seconds of normal operation; the 5-second drain budget now applies only after SIGINT/SIGTERM.