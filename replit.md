# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application built with Kotlin and Jetpack Compose. This app features a modern Material Design 3 navigation drawer with multiple screens (Home, Gallery, Slideshow, and Settings) demonstrating current Android development best practices with a highly modular architecture. The app includes theme management with DataStore persistence.

## Project Type
**Android Mobile Application** - This project builds an Android APK using Jetpack Compose for the UI layer with a multi-module Gradle structure.

## Architecture
- **Language**: Kotlin 2.0.0
- **UI Framework**: Jetpack Compose with Material Design 3
- **Build System**: Gradle 8.14.3 with Kotlin DSL
- **Target SDK**: Android 34 (Android 14)
- **Minimum SDK**: Android 29 (Android 10)
- **Java Version**: Java 17 (OpenJDK 17.0.15)
- **Modular Architecture**: Multi-module Gradle project with feature and core modules
- **Data Persistence**: Proto DataStore for user preferences

## Project Structure
```
CodeLikeBastiMove/
├── app/                                    # Main application module
│   ├── src/main/
│   │   ├── java/com/scto/codelikebastimove/
│   │   │   ├── MainActivity.kt
│   │   │   ├── navigation/
│   │   │   │   ├── AppNavigation.kt
│   │   │   │   └── DrawerHeader.kt
│   │   │   └── ui/theme/
│   │   │       ├── Theme.kt
│   │   │       └── Type.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── core/                                   # Core modules
│   ├── core-datastore/                     # DataStore repository module
│   │   ├── src/main/java/.../core/datastore/
│   │   │   ├── ThemeMode.kt
│   │   │   ├── UserPreferences.kt
│   │   │   ├── UserPreferencesRepository.kt
│   │   │   └── UserPreferencesSerializer.kt
│   │   └── build.gradle.kts
│   └── core-datastore-proto/               # Proto DataStore schema module
│       ├── src/main/proto/
│       │   └── user_preferences.proto
│       └── build.gradle.kts
├── features/                               # Features aggregator module
│   ├── build.gradle.kts                    # Re-exports all feature modules via api()
│   ├── feature-home/                       # Home feature module
│   │   ├── src/main/java/.../feature/home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   └── build.gradle.kts
│   ├── feature-gallery/                    # Gallery feature module
│   │   ├── src/main/java/.../feature/gallery/
│   │   │   ├── GalleryScreen.kt
│   │   │   └── GalleryViewModel.kt
│   │   └── build.gradle.kts
│   ├── feature-slideshow/                  # Slideshow feature module
│   │   ├── src/main/java/.../feature/slideshow/
│   │   │   ├── SlideshowScreen.kt
│   │   │   └── SlideshowViewModel.kt
│   │   └── build.gradle.kts
│   └── feature-settings/                   # Settings feature module
│       ├── src/main/java/.../feature/settings/
│       │   ├── SettingsScreen.kt (GeneralSettings)
│       │   └── SettingsViewModel.kt
│       └── build.gradle.kts
├── gradle/
│   └── wrapper/
├── build.gradle.kts                        # Root build file (includes protobuf plugin)
└── settings.gradle.kts                     # Includes all modules
```

## Module Structure

### app module
The main application module containing:
- `MainActivity.kt` - Entry point with Compose UI setup and theme management
- `navigation/` - Navigation drawer and screen routing
- `ui/theme/` - Material Design 3 theming with dynamic colors and dark/light mode support

### core modules
Core functionality shared across the app:
- **core-datastore-proto**: Proto DataStore schema definitions for user preferences (theme mode, dynamic colors)
- **core-datastore**: Repository pattern implementation for reading/writing user preferences

### features module (Aggregator)
A thin aggregator library that re-exports all feature modules via `api()` dependencies. The app module only needs to depend on `:features` to access all feature screens.

### Feature Modules (Android Libraries)
Each feature is a separate Gradle module:
- **feature-home**: Home screen with 6 buttons (Create Project, Open Project, Clone Repository, Settings, Help, FAQ). Settings button navigates to SettingsScreen.
- **feature-gallery**: Gallery screen with GalleryViewModel
- **feature-slideshow**: Slideshow screen with SlideshowViewModel
- **feature-settings**: GeneralSettings screen with theme switching and dynamic colors toggle

## Key Features
- **Jetpack Compose UI**: Modern declarative UI framework
- **Navigation Drawer**: Material Design 3 modal navigation drawer with 4 screens
- **MVVM Architecture**: Uses ViewModel with StateFlow for reactive UI
- **Compose Navigation**: AndroidX Navigation Compose for screen navigation
- **Material Design 3**: Latest Material You design language
- **Multi-module Architecture**: Separated features and core modules
- **Theme Management**: Light/Dark/Follow System theme switching with DataStore persistence
- **Dynamic Colors**: Material You dynamic color support toggle

## Settings Screen (GeneralSettings)
The Settings screen provides:
1. **Design Section**: Theme mode selection
   - Hell (Light)
   - Dunkel (Dark)
   - System folgen (Follow System)
2. **Dynamic Colors Section**: Toggle for Material You dynamic colors

Settings are persisted using Proto DataStore and applied app-wide.

## Dependencies

### app module
- Jetpack Compose BOM 2024.06.00
- Compose Material 3 with icons-extended
- Compose Navigation
- Lifecycle ViewModel Compose
- Kotlin Coroutines
- features module (project dependency)
- core-datastore module (project dependency)

### core-datastore module
- Proto DataStore 1.1.1
- DataStore Preferences 1.1.1
- Protobuf JavaLite 3.25.1

### Feature modules (each)
- Jetpack Compose BOM 2024.06.00
- Compose Material 3
- Lifecycle ViewModel Compose
- Kotlin Coroutines

## Replit Environment Setup

### Installed Tools
- **Java 17**: OpenJDK 17.0.15 (required for Android builds)
- **Kotlin 2.0.0**: With Compose Compiler Plugin
- **Android SDK**: Installed in `~/android-sdk`
  - Platform Tools
  - Android Platform 34
  - Build Tools 34.0.0 and 35.0.0
- **Gradle**: 8.14.3 (installed via wrapper)
- **Protobuf**: 3.25.1 (via Gradle plugin 0.9.4)

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

### Building Individual Modules
```bash
# Build all feature modules
./gradlew :features:assembleDebug

# Build specific feature modules
./gradlew :features:feature-home:assembleDebug
./gradlew :features:feature-gallery:assembleDebug
./gradlew :features:feature-slideshow:assembleDebug
./gradlew :features:feature-settings:assembleDebug

# Build core modules
./gradlew :core:core-datastore:assembleDebug
./gradlew :core:core-datastore-proto:assembleDebug

# Build only the app module
./gradlew :app:assembleDebug
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
- ✅ Created features aggregator module
- ✅ Split features into 4 separate modules (home, gallery, slideshow, settings)
- ✅ Added new Settings screen to navigation drawer
- ✅ Added core-datastore-proto module with Proto DataStore schema
- ✅ Added core-datastore module with UserPreferencesRepository
- ✅ Implemented GeneralSettings screen with theme and dynamic colors sections
- ✅ Connected Settings button in HomeScreen to navigate to SettingsScreen
- ✅ Integrated theme settings into MainActivity for app-wide theming
- ✅ Successfully built modular Compose APK (~57MB)

## Development Workflow
1. Make changes to Kotlin/Compose source files in the appropriate feature module
2. Run the workflow or `./gradlew assembleDebug` to build
3. Install the APK on an Android device/emulator for testing

## Troubleshooting
- If build fails, ensure ANDROID_HOME and JAVA_HOME are set correctly
- Run `./gradlew clean` before building if you encounter caching issues
- Check that Kotlin version matches Compose Compiler Plugin version
- For module-related issues, verify settings.gradle.kts includes all modules
- If imports fail, ensure the features aggregator module uses `api()` instead of `implementation()`
- For Proto DataStore issues, ensure the protobuf plugin version is 0.9.4 and protobuf-javalite is 3.25.1
