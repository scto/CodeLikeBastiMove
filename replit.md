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
- `codelikebastimove.android.feature` - For feature modules (features/*) - includes library, compose, core-ui, core-resources

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
- Automatic core-ui and core-resources integration for feature modules

## Project Structure
```
CodeLikeBastiMove/
├── app/                                    # Main application module
│   ├── src/main/
│   │   ├── java/com/scto/codelikebastimove/
│   │   │   ├── MainActivity.kt
│   │   │   └── navigation/
│   │   │       ├── AppNavigation.kt
│   │   │       └── DrawerHeader.kt
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
│   ├── core-ui/                            # Centralized Material3 theming
│   │   └── src/main/java/.../core/ui/theme/
│   │       ├── Theme.kt                    # Main theme composable
│   │       ├── ThemeMode.kt                # ThemeMode enum (LIGHT, DARK, FOLLOW_SYSTEM)
│   │       ├── Colors.kt                   # Light/Dark color schemes
│   │       ├── Typography.kt               # App typography
│   │       ├── Icons.kt                    # AppIcons object
│   │       └── Gradients.kt                # Gradient brushes
│   ├── core-resources/                     # Centralized resources
│   │   └── src/main/res/values/
│   │       ├── strings.xml                 # App strings
│   │       ├── dimens.xml                  # Dimensions
│   │       └── colors.xml                  # Color definitions
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
- **Centralized Theming**: core-ui module with Material3 theme, colors, typography, icons
- **Centralized Resources**: core-resources module for shared strings, dimensions, colors
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
- ✅ **core-ui Module** created with centralized Material3 theming:
  - Theme.kt with CodeLikeBastiMoveTheme composable
  - ThemeMode.kt enum (LIGHT, DARK, FOLLOW_SYSTEM)
  - Colors.kt with Light/Dark color schemes
  - Typography.kt with app typography
  - Icons.kt with AppIcons object
  - Gradients.kt with gradient brushes
- ✅ **core-resources Module** created with centralized resources:
  - strings.xml with app-wide strings
  - dimens.xml with dimension values
  - colors.xml with color definitions
- ✅ **AndroidFeatureConventionPlugin** updated to include core-ui and core-resources
- ✅ **MainActivity** updated to import theme from core-ui
- ✅ Old theme files removed from app/src/main/java/.../ui/theme/
- ✅ **Build-Logic Module** created with convention plugins:
  - AndroidApplicationConventionPlugin for app module
  - AndroidLibraryConventionPlugin for library modules
  - AndroidFeatureConventionPlugin for feature modules with full dependencies
  - SDK versions centralized via version catalog (CompileSDK 36, TargetSDK 35, MinSDK 29)
  - All modules migrated to use convention plugins
  - Fixed feature-onboarding activity-compose dependency via convention plugin

## Previous Changes (December 4, 2025)
- ✅ **Onboarding Module** implemented with 4 pages
- ✅ Extended Proto DataStore schema with OnboardingConfigProto
- ✅ Created feature-git module with all Git commands
- ✅ Implemented BottomAppBar in EditorScreen with 6 navigation buttons
- ✅ **Git Clone Dialog** in HomeScreen with multi-step wizard

## Troubleshooting
- If build fails, ensure ANDROID_HOME and JAVA_HOME are set correctly
- Run `./gradlew clean` before building if you encounter caching issues
- For module-related issues, verify settings.gradle.kts includes all modules
- If imports fail, ensure the features aggregator module uses `api()` instead of `implementation()`

## Design Decisions
- **ThemeMode duplication**: ThemeMode enum exists in both core-ui and core-datastore to avoid circular dependencies. MainActivity maps between them.
- **Convention plugin dependencies**: Feature modules get core-ui and core-resources via `implementation()` to prevent resource leakage while maintaining proper encapsulation.
