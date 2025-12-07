# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application built with Kotlin and Jetpack Compose. This app features a modern Material Design 3 navigation drawer with multiple screens (Home, Gallery, Slideshow, Settings, and Editor) demonstrating current Android development best practices with a highly modular architecture. The app includes theme management with DataStore persistence and a comprehensive project template system for creating new Android projects.

## Project Type
**Android Mobile Application** - This project builds an Android APK using Jetpack Compose for the UI layer with a multi-module Gradle structure.

## Architecture
- **Language**: Kotlin 2.2.20
- **UI Framework**: Jetpack Compose with Material Design 3
- **Build System**: Gradle 8.14.3 with Kotlin DSL and Convention Plugins
- **Compile SDK**: Android 36
- **Target SDK**: Android 35
- **Minimum SDK**: Android 29 (Android 10)
- **Java Version**: Java 17 (OpenJDK 17.0.15)
- **Modular Architecture**: Multi-module Gradle project with feature and core modules
- **Data Persistence**: Proto DataStore for user preferences
- **Build Logic**: Custom convention plugins for consistent module configuration

## Build Logic (Convention Plugins)
The project uses a `build-logic` included build with custom Gradle convention plugins:

### Available Plugins
- `codelikebastimove.android.application` - For the main app module
- `codelikebastimove.android.application.compose` - Adds Compose support to app module
- `codelikebastimove.android.library` - For library modules (core/*)
- `codelikebastimove.android.library.compose` - Adds Compose support to library modules
- `codelikebastimove.android.feature` - For feature modules (features/*) - includes library, compose, and common dependencies

### SDK Configuration
All SDK versions are centralized in `gradle/libs.versions.toml`:
- `sdk-compile = "36"`
- `sdk-target = "35"`
- `sdk-min = "29"`

### Convention Plugin Benefits
- Consistent SDK versions across all modules
- Shared Kotlin and Java configuration
- Centralized Compose setup with packaging configurations
- Common dependencies for feature modules (Compose, Lifecycle, Coroutines, Activity-Compose)
- Dependencies exposed as `api()` for proper transitive access

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
├── build-logic/                            # Convention plugins
│   ├── settings.gradle.kts
│   └── convention/
│       ├── build.gradle.kts
│       └── src/main/kotlin/
│           ├── AndroidApplicationConventionPlugin.kt
│           ├── AndroidApplicationComposeConventionPlugin.kt
│           ├── AndroidLibraryConventionPlugin.kt
│           ├── AndroidLibraryComposeConventionPlugin.kt
│           ├── AndroidFeatureConventionPlugin.kt
│           ├── KotlinAndroid.kt
│           ├── AndroidCompose.kt
│           └── ProjectExtensions.kt
├── core/                                   # Core modules
│   ├── core-datastore/                     # DataStore repository module
│   ├── core-datastore-proto/               # Proto DataStore schema module
│   ├── templates-api/                      # Project templates API module
│   │   └── src/main/java/.../core/templates/api/
│   │       ├── GradleLanguage.kt
│   │       ├── Project.kt
│   │       ├── ProjectConfig.kt
│   │       ├── ProjectFile.kt
│   │       ├── ProjectLanguage.kt
│   │       ├── ProjectManager.kt
│   │       ├── ProjectTemplate.kt
│   │       └── TreeNode.kt
│   └── templates-impl/                     # Project templates implementation
│       └── src/main/java/.../core/templates/impl/
│           ├── EmptyActivityTemplate.kt
│           ├── EmptyComposeActivityTemplate.kt
│           ├── BottomNavigationTemplate.kt
│           ├── NavigationDrawerTemplate.kt
│           ├── TabbedActivityTemplate.kt
│           └── ProjectManagerImpl.kt
├── features/                               # Features aggregator module
│   ├── build.gradle.kts
│   ├── feature-home/                       # Home feature module
│   ├── feature-gallery/                    # Gallery feature module
│   ├── feature-slideshow/                  # Slideshow feature module
│   ├── feature-settings/                   # Settings feature module
│   ├── feature-treeview/                   # TreeView component module
│   ├── feature-git/                        # Git commands module
│   │   └── src/main/java/.../feature/git/
│   │       ├── GitCommand.kt               # All Git commands definitions
│   │       └── GitScreen.kt                # Git commands UI
│   ├── feature-onboarding/                 # Onboarding module (4 pages)
│   │   └── src/main/java/.../feature/onboarding/
│   │       ├── OnboardingScreen.kt         # Main onboarding navigation
│   │       ├── OnboardingViewModel.kt      # Onboarding data management
│   │       ├── WelcomePage.kt              # Page 1: App intro
│   │       ├── PermissionsPage.kt          # Page 2: Permission requests
│   │       ├── InstallationOptionsPage.kt  # Page 3: SDK/Tools selection
│   │       └── SummaryPage.kt              # Page 4: Configuration summary
│   └── feature-editor/                     # Editor feature module
│       └── src/main/java/.../feature/editor/
│           ├── EditorScreen.kt             # Editor with BottomAppBar navigation
│           └── EditorViewModel.kt          # Multi-tab file management
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml                  # Version catalog
├── build.gradle.kts
└── settings.gradle.kts
```

## Key Features
- **Jetpack Compose UI**: Modern declarative UI framework
- **Navigation Drawer**: Material Design 3 modal navigation drawer
- **MVVM Architecture**: Uses ViewModel with StateFlow for reactive UI
- **Compose Navigation**: AndroidX Navigation Compose for screen navigation
- **Material Design 3**: Latest Material You design language
- **Multi-module Architecture**: Separated features and core modules
- **Theme Management**: Light/Dark/Follow System theme switching with DataStore persistence
- **Dynamic Colors**: Material You dynamic color support toggle
- **5 Project Templates**: Create new Android projects with various architectures
- **Git Clone Dialog**: Multi-step wizard for cloning repositories with credential management
- **Secure Credential Storage**: EncryptedSharedPreferences for Git credentials
- **Slideable TreeView**: Swipe left/right to show/hide project file tree
- **Tabbed Code Editor**: Open and edit multiple files with tab management
- **File TreeView**: Hierarchical file structure display with expand/collapse

## Project Templates (5 Available)
1. **Empty Activity**: Basic Android project with a single empty activity
2. **Empty Compose Activity**: Jetpack Compose project with a simple composable
3. **Bottom Navigation Activity**: Project with BottomNavigationView and 3 fragments
4. **Navigation Drawer Activity**: Project with DrawerLayout and NavigationView
5. **Tabbed Activity**: Project with ViewPager2 and TabLayout

Each template supports:
- Project Name / Package Name configuration
- Minimum SDK selection (API 21-34)
- Language choice (Java or Kotlin)
- Gradle Language choice (Groovy or Kotlin DSL)

## Editor Features
- **Slideable TreeView**: Swipe from left-to-right to open, right-to-left to close
- **Tab Management**: Open multiple files in tabs, switch between them
- **Code Editor**: Monospace font with line numbers
- **Animated Transitions**: Smooth expand/collapse animations
- **BottomAppBar Navigation**: 6 navigation buttons (TreeView, Git, Settings, Asset Studio, Submodul Creator, Konsole)

## Git Module Features
- **Comprehensive Git Commands**: All Git commands organized by category
- **Categories**: Setup, Basic Snapshotting, Branching, Sharing, Inspection, Patching, Administration
- **Interactive UI**: Expandable command cards with usage examples
- **Commands Include**: init, clone, add, status, diff, commit, branch, checkout, merge, fetch, pull, push, and many more

## Settings Screen
1. **Design Section**: Theme mode selection (Hell/Dunkel/System folgen)
2. **Dynamic Colors Section**: Toggle for Material You dynamic colors

## Building the Application
```bash
./gradlew assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Recent Changes (December 7, 2025)
- ✅ **Build-Logic Module** created with convention plugins:
  - AndroidApplicationConventionPlugin for app module
  - AndroidLibraryConventionPlugin for library modules
  - AndroidFeatureConventionPlugin for feature modules with full dependencies
  - SDK versions centralized via version catalog (CompileSDK 36, TargetSDK 35, MinSDK 29)
  - All modules migrated to use convention plugins
  - Fixed feature-onboarding activity-compose dependency via convention plugin

## Recent Changes (December 4, 2025)
- ✅ **Onboarding Module** implemented with 4 pages:
  - Page 1 (Welcome): App logo, name, description, and welcome message
  - Page 2 (Permissions): File access, usage analytics, battery optimization permission cards
  - Page 3 (Installation Options): OpenJDK version (17/22), Build Tools version (35.0.1/34.0.2/33.0.1), optional tools (git, git-lfs, ssh)
  - Page 4 (Summary): Configuration overview with installation start button
- ✅ Extended Proto DataStore schema with OnboardingConfigProto
- ✅ UserPreferencesRepository extended with onboarding configuration methods
- ✅ Onboarding shown on first app start, persisted via DataStore
- ✅ Created feature-git module with all Git commands
- ✅ Added GitCommand.kt with 50+ Git commands in 7 categories
- ✅ Added GitScreen.kt with interactive expandable UI
- ✅ Implemented BottomAppBar in EditorScreen with 6 navigation buttons
- ✅ Added navigation between TreeView, Git, Settings, Asset Studio, Submodul Creator, Konsole panels
- ✅ **Git Clone Dialog** in HomeScreen with multi-step wizard:
  - Git Config setup (Name, Email) stored in Proto DataStore
  - Secure credentials storage (Username, Token) with EncryptedSharedPreferences
  - Clone options: Repository URL, Branch selection, Submodule toggle
  - Automatic `safe.directory` config after cloning
- ✅ Extended Proto DataStore schema with GitConfigProto and ClonedRepositoryProto
- ✅ Created GitCredentialsStore with AndroidX Security Crypto for secure credential storage
- ✅ Improved EditorBottomBar: smaller icons (18dp), smaller fonts (9sp), scrollable, reduced height (56dp)
- ✅ Reduced TreeView width from 280dp to 220dp

## Previous Changes (December 3, 2025)
- ✅ Implemented slideable TreeView with swipe gestures (AnimatedVisibility)
- ✅ Added tab support for multiple open files in Editor
- ✅ Added 3 new project templates (Bottom Navigation, Navigation Drawer, Tabbed Activity)
- ✅ Fixed settings.gradle.kts syntax for Kotlin DSL templates
- ✅ ProjectManagerImpl now provides all 5 templates
- ✅ Successfully built modular Compose APK (~55MB)

## Troubleshooting
- If build fails, ensure ANDROID_HOME and JAVA_HOME are set correctly
- Run `./gradlew clean` before building if you encounter caching issues
- For module-related issues, verify settings.gradle.kts includes all modules
- If imports fail, ensure the features aggregator module uses `api()` instead of `implementation()`
