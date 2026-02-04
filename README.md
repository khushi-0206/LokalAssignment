# Lokal Android Assignment

## Setup Instructions
1.  Open the project in Android Studio.
2.  Sync Gradle with Project.
3.  Run the app on an emulator or device.
    *   **Note**: The app logs OTPs to Logcat using Timber. Filter Logcat by `LokalApp` or `OTP` to see the generated code.

## Functional Overview
*   **Login**: Enter any valid email. Click "Send OTP".
    *   **UI**: Soft Purple gradient background with Lock icon.
    *   **Validation**: Toasts for invalid email formats.
*   **OTP**: Check Logcat for the 6-digit code. Enter it to verify.
    *   **UI**: Soft Orange gradient background with Shield icon.
    *   **Timer**: Visual 60-second countdown.
    *   **Validation**: Max 3 attempts allowed. Remaining attempts are highlighted on error.
*   **Session**: Displays session start time and a live duration timer (mm:ss).
    *   **UI**: Soft Teal gradient background with Person icon.
    *   **Features**: Timer survives screen rotation. Click "Logout" to return to login.


## Documentation

### 1. OTP Logic and Expiry Handling
*   **Generation**: A random 6-digit number is generated in `OtpManager`. It is stored in a thread-safe `ConcurrentHashMap` mapped to the email.
*   **Expiry**: We store `generatedTime` with the OTP. When validating, we check `System.currentTimeMillis() - generatedTime > 60000`. If expired, we return `LinkExpired` state.
*   **Validation**: We check for exact match. Attempts are tracked in `OtpData`. If attempts >= 3, we return `MaxAttemptsExceeded`.

### 2. Data Structures Used
*   **`ConcurrentHashMap<String, OtpData>`**: Used in `OtpManager` to store OTPs.
    *   **Why**: Provides thread-safety (good practice even if not strictly needed for this single-user simple app) and constant-time O(1) access for looking up OTP data by email.
*   **`Sealed Interface AuthState`**: Used to represent the UI state of the authentication flow.
    *   **Why**: Ensures type safety and exhaustive `when` blocks in the UI, making state management predictable and clean.
*   **`StateFlow`**: Used in `AuthViewModel` to expose state to Compose.
    *   **Why**: Efficiently handles state updates and survives configuration changes when held in ViewModel.

### 3. External SDK Integration
*   **Library**: **Timber** (`com.jakewharton.timber:timber`)
*   **Why**: Timber provides a clean, extensible API for logging. It automatically handles tagging (using class names) and allows for easily planting different "Trees" (logging strategies) for debug vs release builds. It's much cleaner than standard `Log.d` calls.
    *   Logged Events: `OTP_GENERATED`, `OTP_VALIDATION_SUCCESS`, `OTP_VALIDATION_FAILURE`, `LOGOUT`.

### 4. AI Usage Declaration
*   **What I understood & implemented**:
    *   Project structure and Architecture (MVVM, Clean Architecture principles).
    *   Jetpack Compose UI implementation (screens, state hoisting).
    *   Kotlin logic (Coroutines, Flow, Timer logic).
    *   Dependency injection (manual/simple for this scale).
*   **What I used GPT for**:
    *   Quickly generating the boilerplate for `build.gradle.kts` dependencies (Compose BOM versions).
    *   Formatting this README.
    *   Double-checking the standard `SimpleDateFormat` patterns.
