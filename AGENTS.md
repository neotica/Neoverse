# AGENTS.md

## Repo map
Kotlin/Compose Multiplatform monorepo with several apps. `:app` is now a chat-only Neoverse app; the world/Alexandria features are hidden and no longer wired into it.

- `:app` — Neoverse Android phone app (`id.neotica.neoverse`). Current scope: auth, conversations, chat, profile. Koin entry: `app/src/main/kotlin/id/neotica/neoverse/di/CoreModules.kt`.
- `:neoversewear` — legacy Wear OS app (same applicationId). Out of scope for the chat refactor.
- `:hadall` — legacy Alexandria app for Android + iOS. Android id `id.neotica.hadall`, Kotlin package `id.neotica.alexandria`, iOS framework `Hadall`. Out of scope.
- `alexandria/` — Xcode project for the Hadall app. Not a Gradle module.
- `iosApp/` — separate SwiftUI Neoverse iOS app; only embeds `core:core` + `core:domain`. Not part of the chat refactor.
- `feature/alexandria` — shared KMP library still used by Hadall. No longer used by `:app`.
- `feature/world`, `feature/tavern`, `feature/raven`, `feature/friend`, `core:ui`, `core:network`, `core:firebase` — legacy modules kept in the repo but not in `:app` dependencies.

## Current backend
- Auth and chat base URLs are configured in `core/core/src/commonMain/kotlin/id/neotica/neoverse/utils/Constants.kt` (`authBaseUrl`, `chatBaseUrl`, `chatWebSocketUrl`).
- Auth: Neoserver auth.
- Chat: Rust/Axum/SQLx Nusk service.
- Backend contracts: `docs/nusk/BACKEND.md` and `docs/nusk/openapi.yaml`.
- Auth uses access + refresh JWTs. Refresh through `GET /auth/reload` with the refresh token as bearer.

## Commands
- Build/test the phone app:
  - `./gradlew :app:assembleDebug`
  - `./gradlew :app:testDebugUnitTest`
  - `./gradlew :app:installDebug` (device/emulator)
- Focused module checks: `./gradlew :core:chat:compileDebugKotlin`, `:feature:auth:compileDebugKotlinAndroid`, `:feature:home:compileDebugKotlin`, `:feature:chat:compileDebugKotlin`.
- `:neoversewear`, `:hadall`, and the iOS Xcode projects are out of scope. Do not treat their failures as regressions unless the task touches them.
- No ktlint/detekt/format task is configured; Kotlin style is `official`.

## Setup gotchas
- `:app` no longer uses Firebase or Google Services. Do not add `google-services.json` or Firebase initialization back.
- `hadall/google-services.json` remains legacy and is gitignored.
- `local.properties` is local-only (SDK path/signing credentials). Never commit or print it.
- Toolchain: Gradle 8.13, AGP 8.11.1, Kotlin 2.2.0, JDK 17; `:app` requests a Java 11 toolchain. compileSdk 36; minSdk 26.
- Dependency versions live in `gradle/libs.versions.toml`; use `libs.*` and type-safe `projects.*` accessors.
- CI (`.github/workflows/release.yml`) is stale and only builds a release; it runs no tests.

## Architecture notes
- Routes live in `core/routes/src/commonMain/kotlin/id/neotica/routes/Screen.kt`. The phone chat uses `Screen.Conversation`; the old `Screen.Chat` remains for legacy Wear code.
- Koin is manual. `:app` includes `authModules`, `profileModules`, `chatModules`, `ktorModule`, and its own view-model module.
- Auth (`feature/auth`):
  - Ktor client for the configured auth base URL.
  - `TokenStore` expect/actual (Android EncryptedSharedPreferences, iOS NSUserDefaults for now).
  - `SessionRepositoryImpl` implements both `SessionRepository` and `TokenProvider`.
  - Login/register/verification/password reset follow the Neoserver auth contract.
- Chat:
  - `core:chat` is an Android-only Ktor REST + WebSocket module (`id.neotica.chat.client`).
  - `ChatRealtimeClient` owns the WebSocket reconnect loop.
  - `ConversationRepositoryImpl` implements the domain `ConversationRepository`.
  - No friends graph: add-chat uses exact username lookup through `POST /v1/conversations`.
  - No push notifications, avatars, groups, or message attachments.
- `core:domain` owns `Conversation`, `ChatMessage`, `ConversationRepository`, `SessionRepository`, and `TokenProvider`.
- `:app` no longer depends on `core:database`, `core:network`, `core:firebase`, `core:camera`, `feature:world`, or `feature:friend`. Keep it that way.
- `core:database` now owns `ProfileEntity`/`ProfileDao` in `id.neotica.database.profile`; it no longer depends on `feature:profile`.
- `core:ktor` installs `WebSockets` in its shared `HttpClient`; `core:chat` is the consumer.
- Profile is minimal: username + default avatar + account actions. Profile editing/avatar/camera are not wired.
- UI uses `core:ui-shared` (`id.neotica.ui.shared`). Legacy `core:ui` is only for the abandoned world/Wear modules.
- `feature/alexandria` files live under `src/commonMain/kotlin/id.neotica/alexandria/...` (literally `id.neotica`).
- KMP modules use two Android setups:
  - legacy `com.android.library` + `androidTarget`: `androidMain`, `androidUnitTest`/`androidInstrumentedTest`.
  - new `com.android.kotlin.multiplatform.library`: `androidLibrary {}`, `androidHostTest`/`androidDeviceTest`.
- iOS framework base names are fixed (`coreNeoverse`, `domain`, `roomKit`, `ktorKit`, `firebaseKit`, `routesKit`, `ComposeUIKit`, `authKit`, `profileKit`, `Hadall`, `shared`).
- JVM targets are mixed: `core:domain` and `feature:profile` use JVM 17, most others use 11.

## Testing quirks
- `core:domain` uses JUnit 5 (`android-junit5` + Jupiter); most other modules use JUnit 4 + Mockito + `kotlinx-coroutines-test`.
- `:app:testDebugUnitTest` is the quick verification for app-shell changes.
- The chat backend is external; client code can be tested with Ktor `MockEngine` and a fake `SessionRepository`. End-to-end chat requires the configured Nusk service.
