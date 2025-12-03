# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application built with Kotlin and Jetpack Compose. This app features a modern Material Design 3 navigation drawer with multiple screens (Home, Gallery, Slideshow, Settings, and Editor) demonstrating current Android development best practices with a highly modular architecture. The app includes theme management with DataStore persistence and a project template system for creating new Android projects.

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
│   │   └── src/main/java/.../core/datastore/
│   ├── core-datastore-proto/               # Proto DataStore schema module
│   │   └── src/main/proto/user_preferences.proto
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
│           └── ProjectManagerImpl.kt
├── features/                               # Features aggregator module
│   ├── build.gradle.kts
│   ├── feature-home/                       # Home feature module
│   │   └── src/main/java/.../feature/home/
│   │       ├── CreateProjectDialog.kt
│   │       ├── HomeScreen.kt
│   │       └── HomeViewModel.kt
│   ├── feature-gallery/                    # Gallery feature module
│   ├── feature-slideshow/                  # Slideshow feature module
│   ├── feature-settings/                   # Settings feature module
│   ├── feature-treeview/                   # TreeView component module
│   │   └── src/main/java/.../feature/treeview/
│   │       ├── TreeView.kt
│   │       └── TreeViewUtils.kt
│   └── feature-editor/                     # Editor feature module
│       └── src/main/java/.../feature/editor/
│           ├── EditorScreen.kt
│           └── EditorViewModel.kt
├── gradle/
│   └── wrapper/
├── build.gradle.kts
└── settings.gradle.kts
```

## Module Structure

### app module
The main application module containing:
- `MainActivity.kt` - Entry point with Compose UI setup and theme management
- `navigation/` - Navigation drawer and screen routing (includes Editor route)
- `ui/theme/` - Material Design 3 theming with dynamic colors and dark/light mode support

### core modules
Core functionality shared across the app:
- **core-datastore-proto**: Proto DataStore schema definitions for user preferences
- **core-datastore**: Repository pattern implementation for reading/writing user preferences
- **templates-api**: Interfaces and data classes for project templates (ProjectTemplate, ProjectConfig, ProjectManager)
- **templates-impl**: Implementation of project templates (EmptyActivityTemplate, EmptyComposeActivityTemplate)

### features module (Aggregator)
A thin aggregator library that re-exports all feature modules via `api()` dependencies.

### Feature Modules (Android Libraries)
- **feature-home**: Home screen with project creation dialog. "Erstelle ein Projekt" opens CreateProjectDialog.
- **feature-gallery**: Gallery screen with GalleryViewModel
- **feature-slideshow**: Slideshow screen with SlideshowViewModel
- **feature-settings**: GeneralSettings screen with theme switching and dynamic colors toggle
- **feature-treeview**: Reusable TreeView component for displaying hierarchical file structures
- **feature-editor**: EditorScreen with file tree on the left and code editor on the right

## Key Features
- **Jetpack Compose UI**: Modern declarative UI framework
- **Navigation Drawer**: Material Design 3 modal navigation drawer
- **MVVM Architecture**: Uses ViewModel with StateFlow for reactive UI
- **Compose Navigation**: AndroidX Navigation Compose for screen navigation
- **Material Design 3**: Latest Material You design language
- **Multi-module Architecture**: Separated features and core modules
- **Theme Management**: Light/Dark/Follow System theme switching with DataStore persistence
- **Dynamic Colors**: Material You dynamic color support toggle
- **Project Templates**: Create new Android projects (Empty Activity, Empty Compose Activity)
- **File TreeView**: Hierarchical file structure display with expand/collapse
- **Code Editor**: Basic code editor with line numbers and syntax highlighting colors

## Project Creation System
The CreateProjectDialog allows users to:
1. **Select Template**: Empty Activity or Empty Compose Activity
2. **Configure Project**:
   - Project Name
   - Package Name
   - Minimum SDK (API 21-34)
   - Language (Java or Kotlin)
   - Gradle Language (Groovy or Kotlin DSL)

After creation, the Editor screen displays the project files in a TreeView.

## Settings Screen (GeneralSettings)
1. **Design Section**: Theme mode selection (Hell/Dunkel/System folgen)
2. **Dynamic Colors Section**: Toggle for Material You dynamic colors

## Dependencies

### app module
- Jetpack Compose BOM 2024.06.00
- Compose Material 3 with icons-extended
- Compose Navigation
- Lifecycle ViewModel Compose
- Kotlin Coroutines
- features module (project dependency)
- core-datastore module (project dependency)
- templates-api module (project dependency)

### core-datastore module
- Proto DataStore 1.1.1
- DataStore Preferences 1.1.1
- Protobuf JavaLite 3.25.1

### templates-api/templates-impl modules
- Kotlin Coroutines

### Feature modules
- Jetpack Compose BOM 2024.06.00
- Compose Material 3
- Lifecycle ViewModel Compose
- Kotlin Coroutines

## Building the Application

### Using the Workflow
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

# Build core modules
./gradlew :core:core-datastore:assembleDebug
./gradlew :core:templates-api:assembleDebug
./gradlew :core:templates-impl:assembleDebug

# Build feature modules
./gradlew :features:feature-treeview:assembleDebug
./gradlew :features:feature-editor:assembleDebug
```

## Running the Application
1. **Android Device**: Download the APK and install it
2. **Android Emulator**: Use Android Studio's emulator
3. **ADB**: `adb install app/build/outputs/apk/debug/app-debug.apk`

## Recent Changes (December 3, 2025)
- ✅ Added templates-api module with ProjectTemplate, ProjectConfig, ProjectManager interfaces
- ✅ Added templates-impl module with EmptyActivityTemplate and EmptyComposeActivityTemplate
- ✅ Added feature-treeview module with TreeView composable component
- ✅ Added feature-editor module with EditorScreen and code editor
- ✅ Added CreateProjectDialog to HomeScreen for project creation
- ✅ Updated navigation to include Editor route
- ✅ Successfully built modular Compose APK (~55MB)

## Troubleshooting
- If build fails, ensure ANDROID_HOME and JAVA_HOME are set correctly
- Run `./gradlew clean` before building if you encounter caching issues
- Check that Kotlin version matches Compose Compiler Plugin version
- For module-related issues, verify settings.gradle.kts includes all modules
- If imports fail, ensure the features aggregator module uses `api()` instead of `implementation()`
- For Proto DataStore issues, ensure the protobuf plugin version is 0.9.4

- If build :core:datastore-proto fails
- Das ist ein klassisches Problem beim Entwickeln direkt auf Android (Termux).
- Der Grund:
- Das Protobuf-Gradle-Plugin versucht normalerweise, eine protoc-Binary herunterzuladen, die für Linux (Desktop) kompiliert ist. Diese Binaries erwarten die GNU C Library (glibc). Android (und damit Termux) verwendet aber Bionic libc. Deshalb stürzt die heruntergeladene Binary ab oder lässt sich gar nicht erst ausführen.
- Du musst Gradle zwingen, den in Termux installierten Compiler zu nutzen, statt einen herunterzuladen.
- Hier ist die Lösung in 3 Schritten:
- 1. Protobuf in Termux installieren
- Installiere den Compiler direkt über den Paketmanager von Termux.
- pkg install protobuf
-
- Überprüfe danach, wo er liegt (meistens unter /data/data/...):
- which protoc
- Ausgabe merken, meistens: /data/data/com.termux/files/usr/bin/protoc
-
- 2. build.gradle.kts anpassen
- Gehe in dein Modul (z. B. :core:core-datastore-proto) und bearbeite die build.gradle.kts.
- Du musst eine Logik einbauen: "Wenn ich auf Termux bin, nimm den lokalen Pfad. Wenn ich am PC bin, lade das Artefakt herunter."
- Füge diesen Block in deine protobuf-Konfiguration ein:
-
- import java.io.File
// ... plugins und android block ...

- protobuf {
-     protoc {
-         // Der Standard-Pfad für protoc in Termux
-         val termuxProtocPath = "/data/data/com.termux/files/usr/bin/protoc"
-         val termuxProtocFile = File(termuxProtocPath)
- 
-         if (termuxProtocFile.exists()) {
-             // Nutze die lokale Installation auf dem Handy
-             path = termuxProtocPath
-         } else {
-             // Fallback für deinen PC/Mac (lädt das Artefakt herunter)
-             artifact = "com.google.protobuf:protoc:3.24.4" 
-         }
-     }
-
-     // WICHTIG: Die generierten Klassen (Lite vs Java)
-     generateProtoTasks {
-         all().forEach { task ->
-             task.builtins {
-                 create("java") {
-                     option("lite")
-                 }
-                 // Falls du Kotlin generierst:
-                 create("kotlin") {
-                     option("lite")
-                 }
-             }
-         }
-     }
- }

- 3. Clean und Build
- Führe nun den Build erneut in Termux aus:
- ./gradlew clean :core:core-datastore-proto:generateDebugProto

- Alternative: Falls du proot-distro nutzt
- Falls du in Termux eine Linux-Distro (wie Ubuntu via proot-distro) nutzt, funktioniert der Pfad zu /data/data/... oft nicht direkt oder es gibt Berechtigungsprobleme.
- In diesem Fall installiere protobuf-compiler innerhalb von Ubuntu (apt install protobuf-compiler) und setze den Pfad in der build.gradle.kts auf /usr/bin/protoc.
- Hat das geklappt, oder gibt es noch Versionskonflikte zwischen der Termux-Version und der im Projekt erwarteten Version?
