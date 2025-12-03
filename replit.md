# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application built with Kotlin and Jetpack Compose. This app features a modern Material Design 3 navigation drawer with multiple screens (Home, Gallery, and Slideshow) demonstrating current Android development best practices.

## Project Type
**Android Mobile Application** - This project builds an Android APK using Jetpack Compose for the UI layer.

## Architecture
- **Language**: Kotlin 2.0.0
- **UI Framework**: Jetpack Compose with Material Design 3
- **Build System**: Gradle 8.14.3 with Kotlin DSL
- **Target SDK**: Android 34 (Android 14)
- **Minimum SDK**: Android 29 (Android 10)
- **Java Version**: Java 17 (OpenJDK 17.0.15)

## Project Structure
```
CodeLikeBastiMove/
├── app/
│   ├── src/main/
│   │   ├── java/com/scto/codelikebastimove/
│   │   │   ├── MainActivity.kt
│   │   │   ├── navigation/
│   │   │   │   ├── AppNavigation.kt
│   │   │   │   └── DrawerHeader.kt
│   │   │   └── ui/
│   │   │       ├── theme/
│   │   │       │   ├── Theme.kt
│   │   │       │   └── Type.kt
│   │   │       ├── home/
│   │   │       │   ├── HomeScreen.kt
│   │   │       │   └── HomeViewModel.kt
│   │   │       ├── gallery/
│   │   │       │   ├── GalleryScreen.kt
│   │   │       │   └── GalleryViewModel.kt
│   │   │       └── slideshow/
│   │   │           ├── SlideshowScreen.kt
│   │   │           └── SlideshowViewModel.kt
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   └── values/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── wrapper/
├── build.gradle.kts
└── settings.gradle.kts
```

## Key Features
- **Jetpack Compose UI**: Modern declarative UI framework
- **Navigation Drawer**: Material Design 3 modal navigation drawer
- **MVVM Architecture**: Uses ViewModel with StateFlow for reactive UI
- **Compose Navigation**: AndroidX Navigation Compose for screen navigation
- **Material Design 3**: Latest Material You design language

## Dependencies
- Jetpack Compose BOM 2024.06.00
- Compose Material 3
- Compose Navigation
- Lifecycle ViewModel Compose
- Kotlin Coroutines

## Replit Environment Setup

### Installed Tools
- **Java 17**: OpenJDK 17.0.15 (required for Android builds)
- **Kotlin 2.0.0**: With Compose Compiler Plugin
- **Android SDK**: Installed in `~/android-sdk`
  - Platform Tools
  - Android Platform 34
  - Build Tools
- **Gradle**: 8.14.3 (installed via wrapper)

### Environment Variables
- `ANDROID_HOME=/home/runner/android-sdk`
- `JAVA_HOME=/nix/store/.../openjdk-17.0.15+6`

## Building the Application

### Using the Workflow
The "Build Android App" workflow is configured to build the debug APK:
```bash
./gradlew assembleDebug
```

### Manual Build
```bash
./gradlew assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Running the Application
Since this is an Android application, it needs to be run on:
1. **Android Device**: Download the APK and install it on an Android device
2. **Android Emulator**: Use Android Studio's emulator or other Android emulators
3. **Android Debugging Bridge (adb)**: Install via `adb install app/build/outputs/apk/debug/app-debug.apk`

## Recent Changes (December 3, 2025)
- ✅ Migrated from View-based UI to Jetpack Compose
- ✅ Upgraded to Kotlin 2.0.0 with Compose Compiler Plugin
- ✅ Converted ViewModels from LiveData to StateFlow
- ✅ Replaced Fragments with Compose Screens
- ✅ Implemented Compose Navigation with Navigation Drawer
- ✅ Created Material Design 3 theme
- ✅ Successfully built 54MB Compose APK

## Compose Migration Notes
The project was converted from the traditional View-based UI (XML layouts + Fragments) to Jetpack Compose:

### Before (View-based)
- XML layouts for each screen
- Fragment classes with View Binding
- ViewModels with LiveData
- Navigation Component with XML navigation graph

### After (Compose)
- Composable functions for each screen
- ViewModel with StateFlow
- Compose Navigation with sealed classes for routes
- Material Design 3 theming in Compose

## Development Workflow
1. Make changes to Kotlin/Compose source files
2. Run the workflow or `./gradlew assembleDebug` to build
3. Install the APK on an Android device/emulator for testing

## Troubleshooting
- If build fails, ensure ANDROID_HOME and JAVA_HOME are set correctly
- Run `./gradlew clean` before building if you encounter caching issues
- Check that Kotlin version matches Compose Compiler Plugin version
