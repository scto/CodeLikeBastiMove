# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application built with Kotlin and the Android SDK. This is a navigation drawer-based app template with multiple fragments (Home, Gallery, and Slideshow) demonstrating modern Android development practices.

## Project Type
**Android Mobile Application** - This project builds an Android APK that can be installed on Android devices or emulators.

## Architecture
- **Language**: Kotlin 1.9.22
- **Build System**: Gradle 8.14.3 with Kotlin DSL
- **Target SDK**: Android 33 (Android 13)
- **Minimum SDK**: Android 29 (Android 10)
- **Java Version**: Java 17 (OpenJDK 17.0.15)

## Project Structure
```
CodeLikeBastiMove/
├── app/
│   ├── src/main/
│   │   ├── java/com/scto/codelikebastimove/
│   │   │   ├── MainActivity.kt
│   │   │   └── ui/
│   │   │       ├── home/
│   │   │       ├── gallery/
│   │   │       └── slideshow/
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── drawable/
│   │   │   ├── navigation/
│   │   │   └── values/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

## Key Features
- **Navigation Drawer**: Material Design navigation drawer with multiple destinations
- **MVVM Architecture**: Uses ViewModel and LiveData for reactive UI
- **View Binding**: Enabled for type-safe view access
- **Material Design**: Google Material Design 3 components
- **Jetpack Navigation**: AndroidX Navigation component for fragment navigation

## Dependencies
- AndroidX Core KTX
- AndroidX AppCompat
- Material Design Components
- Navigation Component (Fragment & UI)
- Lifecycle ViewModel & LiveData
- Kotlin Coroutines
- ConstraintLayout

## Replit Environment Setup

### Installed Tools
- **Java 17**: OpenJDK 17.0.15 (required for Android builds)
- **Android SDK**: Installed in `~/android-sdk`
  - Platform Tools
  - Android Platform 33
  - Build Tools 33.0.0+
- **Gradle**: 8.14.3 (installed via wrapper)
- **System Tools**: android-tools, wget, unzip

### Environment Variables
- `ANDROID_HOME=/home/runner/android-sdk`
- `JAVA_HOME=/nix/store/.../openjdk-17.0.15+6`

### Build Configuration
The project includes `local.properties` with the Android SDK path, which is automatically used by Gradle.

## Building the Application

### Using the Workflow
The "Build Android App" workflow is configured to build the debug APK automatically. It runs:
```bash
./gradlew assembleDebug
```

### Manual Build
To build manually, run:
```bash
./gradlew assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Other Gradle Tasks
```bash
./gradlew tasks          # List all available tasks
./gradlew clean          # Clean build artifacts
./gradlew assembleRelease # Build release APK (requires signing)
./gradlew build          # Build and run tests
```

## Running the Application
Since this is an Android application, it needs to be run on:
1. **Android Device**: Download the APK from `app/build/outputs/apk/debug/app-debug.apk` and install it on an Android device
2. **Android Emulator**: Use Android Studio's emulator or other Android emulators
3. **Android Debugging Bridge (adb)**: Install via `adb install app/build/outputs/apk/debug/app-debug.apk`

## Recent Changes (December 3, 2025)
- ✅ Installed Java 17 (OpenJDK) for Android builds
- ✅ Downloaded and configured Android SDK (Platform 33, Build Tools)
- ✅ Created `local.properties` with SDK path
- ✅ Set up environment variables (ANDROID_HOME, JAVA_HOME)
- ✅ Successfully built debug APK (13MB)
- ✅ Configured workflow for building the app

## Project Configuration Files

### build.gradle.kts (Root)
- Android Gradle Plugin 8.11.0
- Kotlin 1.9.22
- Basic project configuration

### app/build.gradle.kts
- Application ID: `com.scto.codelikebastimove`
- Compile SDK: 33
- Min SDK: 29
- View Binding enabled
- ProGuard enabled for release builds
- Release signing configuration (requires release.properties)

### gradle/libs.versions.toml
Contains version catalog for dependencies and plugins.

## Notes
- This is a **mobile application**, not a web application
- No frontend server is needed (this is not a web app)
- The APK must be installed on an Android device or emulator to run
- The build workflow compiles the app but doesn't run it (no server to start)

## Development Workflow
1. Make changes to Kotlin source files in `app/src/main/java/`
2. Update layouts in `app/src/main/res/layout/`
3. Run the workflow or `./gradlew assembleDebug` to build
4. Install the APK on an Android device/emulator for testing

## Troubleshooting
- If build fails, ensure ANDROID_HOME and JAVA_HOME are set correctly
- Check that Android SDK Platform 33 and Build Tools are installed
- Run `./gradlew clean` before building if you encounter caching issues
- Check build logs in the workflow output for detailed error messages
