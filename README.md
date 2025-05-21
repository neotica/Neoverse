# Neoverse

Welcome to Neoverse, a unique social media experience that blends the thrill of RPG gaming with the joy of connecting with others! Unlike traditional platforms, Neoverse immerses you in a dynamic world where your interactions and achievements directly impact your status and influence. The core of Neoverse revolves around Aura, a mystical energy you gather through socializing, completing quests, and engaging with the community. Your main goal is to accumulate Aura, rise through the ranks, and establish yourself as a powerful figure in the Neoverse. Whether you're strategizing your next move in The Dragon Chest’s Tavern, forging alliances, or simply chatting with friends, every action contributes to your journey. Plus, with seamless cross-platform availability, including a dedicated Wear OS app, you can stay connected and continue your adventure anytime, anywhere. Neoverse offers a fun, new way to connect with friends and make new ones, all while embarking on an exciting RPG adventure.

## Features

- **Gain Aura and Rise to Power**: Collect Aura, a vital resource that determines your status in Neoverse. The more Aura you have, the greater your influence and rewards.
- **Socialize and Connect**: Chat with friends, build connections, and challenge rivals to climb the leaderboards.
- **Explore a Vast World**: Embark on exciting adventures—unearth treasures in The Dragon Chest’s Tavern, or forge your path to success starting from The Mining Field.
- **Cross-Platform Experience**: Enjoy Neoverse seamlessly across platforms, with an intuitive Wear OS app for quick interactions on the go.

## Tech Stack

Neoverse is built with a modern, Kotlin-first approach, leveraging powerful libraries and frameworks across its multiplatform architecture:

-   **Primary Language:** Kotlin
-   **UI Frameworks:**
    -   Jetpack Compose (for Android & Wear OS UI)
    -   Swift/SwiftUI (for iOS UI, integrated with Kotlin Multiplatform)
-   **Platforms:**
    -   Android (Phone, Tablet)
    -   iOS (via Kotlin Multiplatform)
    -   Wear OS
-   **Core Architecture (Kotlin Multiplatform):**
    -   Shared business logic across Android, iOS, and potentially other platforms.
    -   Koin for dependency injection in shared code and platform modules.
-   **Networking & Data (KMP & Android):**
    -   **Ktor:**
        -   Primary HTTP client for Kotlin Multiplatform (shared networking layer).
        -   Capable of being used for building backend services (though current use is client-side).
    -   **Retrofit:** Utilized in Android-specific modules for type-safe HTTP communication.
    -   **Kotlin Serialization:** For efficient JSON parsing and serialization in KMP and Android.
    -   **Room:** For local database persistence on Android and Wear OS.
-   **Asynchronous Programming:**
    -   Kotlin Coroutines (for managing background threads and asynchronous operations across all platforms).
-   **Dependency Injection:**
    -   **Koin:** For managing dependencies throughout the application (Koin Core, Koin AndroidX Compose).
-   **Backend & Cloud Services:**
    -   **Firebase:**
        -   Authentication (user sign-in and management)
        -   Firestore (NoSQL cloud database)
        -   Realtime Database (alternative NoSQL cloud database)
        -   Storage (for user-generated content like images and files)
        -   Cloud Messaging (FCM for push notifications)
        -   App Check (protecting backend resources)
        -   Analytics (for usage insights)
    -   **Ktor (Backend Potential):** While primarily used as a client, Ktor is also a capable framework for building backend services, complementing Firebase if needed.
-   **Android Specific:**
    -   **Image Loading:** Coil (for efficient image loading and caching).
    -   **Camera:** CameraX (for in-app camera functionalities).
    -   **Wear OS Libraries:**
        -   `androidx.wear.compose` (for building UI on Wear OS)
        -   `com.google.android.gms:play-services-wearable` (for communication with wearable devices)
-   **Testing:**
    -   **JUnit & JUnit Jupiter:** For unit testing.
    -   **AndroidX Test:** (includes `androidx.test.ext:junit`, `androidx.test.espresso:espresso-core`) for Android UI and integration testing.
    -   **Kotlin Test:** For multiplatform testing.
    -   **Mockito:** For creating mock objects in tests.
    -   **Espresso:** For Android UI testing.
    -   `kotlinx-coroutines-test`: For testing coroutine-based code.

## Download

Neoverse is available for download on the Google Play Store:

<a href="https://play.google.com/store/apps/details?id=id.neotica.neoverse">
  <img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" alt="Neoverse on Google Play" width="200"/>
</a>

## Installation/Setup Instructions

Follow these steps to get Neoverse up and running on your local development environment.

### Prerequisites

Ensure you have the following software installed:

-   **Git:** For cloning the repository.
-   **Java Development Kit (JDK):** JDK 17 or newer recommended.
-   **Android Studio:** Latest stable version recommended (e.g., Hedgehog or newer).
-   **Xcode:** Latest stable version recommended (for building the iOS app).

### Cloning the Repository

1.  Open your terminal or command prompt.
2.  Clone the repository using the following command:
    ```bash
    git clone https://github.com/Ryoment/Neoverse.git
    ```
3.  Navigate into the cloned project directory:
    ```bash
    cd Neoverse
    ```

### Building and Running - Android

1.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open an existing project" and navigate to the cloned `Neoverse` directory.
2.  **Sync Project:**
    *   Allow Android Studio to sync the project and download all necessary Gradle dependencies. This might take a few minutes.
3.  **Select Run Configuration:**
    *   In the toolbar, select the `app` run configuration from the dropdown menu.
4.  **Run the App:**
    *   Choose an available Android emulator or connect a physical Android device.
    *   Click the "Run" button (green play icon) or use the `Shift + F10` shortcut.

### Building and Running - iOS

1.  **Navigate to iOS Project:**
    *   Open your terminal and navigate to the `iosApp` directory within the `Neoverse` project:
        ```bash
        cd iosApp
        ```
2.  **Open in Xcode:**
    *   Open the Xcode project. It's generally recommended to open the `.xcworkspace` file if it exists (e.g., for projects using CocoaPods or with multiple internal project targets). Otherwise, open the `.xcodeproj` file.
        ```bash
        # Preferred: Open .xcworkspace if it exists
        # open Neoverse.xcworkspace
        
        # Fallback: Open .xcodeproj if .xcworkspace is not present
        open Neoverse.xcodeproj 
        ```
        *Note: You might need to adjust the filename if your workspace/project has a different name.*
3.  **Select Target:**
    *   In Xcode, select your target device or simulator from the scheme menu in the toolbar.
4.  **Build and Run:**
    *   Click the "Play" button (triangle icon) or use the `Cmd + R` shortcut to build and run the app on the selected device/simulator.

## Screenshots
<img src="https://github.com/user-attachments/assets/008ba199-5d7b-4a32-a892-8731dfbbaada" alt="Neoverse Screenshot 1" width="150"/>
<img src="https://github.com/user-attachments/assets/079ab4e2-4d7e-49a4-9d7d-a5a4c5f63834" alt="Neoverse Screenshot 2" width="150"/>
<img src="https://github.com/user-attachments/assets/aa96253e-57b2-4fe0-a715-e6bafbd7338c" alt="Neoverse Screenshot 3" width="150"/>
<img src="https://github.com/user-attachments/assets/3a57c7e9-f7e6-4fb8-8f06-0d491d902d61" alt="Neoverse Screenshot 4" width="150"/>

## Contributing

We welcome contributions to Neoverse! Whether you're fixing a bug, proposing a new feature, or improving documentation, your help is appreciated. Please take a moment to review these guidelines to make the contribution process smooth for everyone.

### Reporting Bugs

If you encounter a bug, please help us by reporting it. Before submitting a new bug report, please:

1.  **Check Existing Issues:** Search the issue tracker to see if the bug has already been reported. If it has, you can add any additional information or confirm that you're also experiencing it.
2.  **Provide Detailed Information:** If the bug hasn't been reported, please create a new issue and include the following details:
    *   A clear and descriptive title.
    *   Steps to reproduce the bug.
    *   What you expected to happen.
    *   What actually happened.
    *   Screenshots or videos, if applicable.
    *   Your environment details (e.g., OS version, device model, Neoverse app version).

### Suggesting Enhancements/Features

We're always looking for ideas to make Neoverse better! If you have a suggestion for an enhancement or a new feature:

1.  **Check Existing Requests:** Search the issue tracker for existing enhancement or feature requests. If a similar idea exists, feel free to add your thoughts to the discussion.
2.  **Provide a Clear Explanation:** If your idea is new, create a new issue and provide a clear and detailed explanation of the proposed enhancement or feature. Describe:
    *   What the enhancement is and what problem it solves.
    *   The benefits it would bring to users.
    *   Any potential drawbacks or considerations.

### Pull Requests

If you'd like to contribute code to Neoverse, please follow these steps:

1.  **Fork the Repository:** Create your own fork of the [Neoverse repository](https://github.com/Ryoment/Neoverse.git) on GitHub.
2.  **Create a Branch:** Create a new branch in your fork for your feature or bugfix. Use a descriptive name, for example:
    *   `git checkout -b feature/awesome-new-feature`
    *   `git checkout -b bugfix/fix-login-issue`
3.  **Make Your Changes:** Implement your feature or bugfix.
4.  **Follow Coding Style:** Ensure your code adheres to the project's coding style. (While a formal style guide might not be explicitly defined, aim for clean, readable, and well-commented Kotlin code.)
5.  **Write Clear Commit Messages:** Write clear, concise, and informative commit messages that explain the purpose of your changes.
6.  **Push to Your Fork:** Push your changes to your branch on your forked repository.
    ```bash
    git push origin feature/your-feature-name
    ```
7.  **Open a Pull Request:** Go to the original Neoverse repository and open a pull request from your forked branch to the `main` branch of the Neoverse repository.
8.  **Describe Your PR:** Provide a clear description of your pull request, explaining the changes you've made and why. If your PR addresses an existing issue, please link to it (e.g., "Fixes #123").

Thank you for contributing to Neoverse!

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

## Contact

For support or inquiries, please contact:

- **Email**: [laetuzg@gmail.com](mailto:laetuzg@gmail.com)
- **Developer Website**: [Neotica](https://neotica.id)
