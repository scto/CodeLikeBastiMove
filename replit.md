# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application built with Kotlin and Jetpack Compose. It aims to demonstrate modern Android development practices through a highly modular architecture, offering a Material Design 3 experience. The project focuses on creating a well-structured, maintainable, and scalable application with an IDE-like interface, offering features like a code editor, asset studio, and Git integration, inspired by AndroidIDE. Its key capabilities include advanced navigation, comprehensive theme management, and a robust project template system.

## User Preferences
I prefer iterative development and clear, concise explanations. Ask before making major architectural changes or significant modifications to existing features. I value well-documented code and a logical, modular structure.

## System Architecture
The application is an Android mobile application leveraging Jetpack Compose for the UI and a multi-module Gradle structure.

**UI/UX Decisions:**
- **Material Design 3:** Utilizes the latest Material You design language with dynamic colors.
- **Editor-Centric Interface:** Features a unified top app bar, drawer-based navigation via 9 horizontal tabs (Files, Build, Module, Assets, Git, Theme, Layout, Settings, Terminal), and a bottom sheet bar for development panels.
- **Theming:** Centralized theme management supporting light/dark schemes and dynamic colors, with a full Material Theme Builder.
- **Navigation:** Type-safe screen navigation using sealed classes and AnimatedContent transitions.

**Technical Implementations:**
- **Language & Frameworks:** Kotlin, Jetpack Compose.
- **Build System:** Gradle with Kotlin DSL, custom Convention Plugins, and Koin DI.
- **SDKs:** Compile SDK 36, Target SDK 35, Minimum SDK 24.
- **Architecture:** MVVM with ViewModel and StateFlow, emphasizing a multi-module design.
- **Data Persistence:** Proto DataStore for user preferences and EncryptedSharedPreferences for secure data.
- **Project Management:** Provides 5 distinct project templates, project creation wizard, list management, Git repository cloning, and existing Android project import.
- **Code Editor:** Advanced Sora Editor integration with TextMate and TreeSitter syntax highlighting for various languages, featuring tabbed editing, undo/redo, find/replace, and configurable settings.
- **File System Interaction:** Hierarchical file structure display with file operations and project selection.
- **Asset Studio:** Comprehensive Vector Asset Studio with icon repository, SVG to AVD conversion, AVD editor, and export options.
- **Onboarding:** Multi-page flow for initial setup and permission handling.
- **Scoped Storage:** Implemented Android 10+ scoped storage using DocumentFile and Storage Access Framework.

**System Design Choices:**
- **Modular Architecture:** Organized into `core/` and `feature/` directories with grouped submodules.
- **Main Module (`feature/main`):** Central module for project operations (create, delete, open, close, clone, import) and navigation management, with persistence across app reinstalls.
- **Git Module (`feature/git`):** Comprehensive Git version control integration using JGit library, supporting all standard Git operations without an external binary. Includes dedicated UI for changes, history, branches, remotes, and settings.
- **Theme Builder Module (`feature/themebuilder`):** Full Material Theme Builder with interactive color picker, tonal palettes, font selection, and export to Android project folders.
- **Sub-Module Maker (`feature/submodulemaker`):** Dedicated module for creating new Gradle sub-modules with automatic settings.gradle.kts updates and build variants feature using GradleParser.
- **Asset Studio (`feature/assetstudio`):** Includes AssetExporter for real Android project export of vector drawables, launcher icons, adaptive icons, and notification icons.
- **Designer Module (`feature/designer`):** Dedicated module for Jetpack Compose live preview with ComposeParser, LivePreviewRenderer, and CodeSynchronizer, supporting 40+ Material3 components.
- **Tree View (`feature/treeview`):** Enhanced file tree view with 5 view modes (Files, Android, Packages, Modules, Project), file operations, context menus, and search.
- **Sora Editor (`feature/soraeditor`):** Advanced code editor module leveraging Rosemoe's sora-editor and Android Tree-Sitter for syntax highlighting, intelligent editing features, and an LSP infrastructure for future language server implementations. It also includes extensive Git submodule management actions.
- **Settings Module (`feature/settings`):** Comprehensive settings management for general, editor, build & run, documentation, help, about, and debug configurations.
- **Updater Module (`core/updater`):** Periodic update checking system via GitHub Releases API with configurable intervals, background checks, and version comparison.
- **Action System (`core/actions/`):** VS Code-style command/action system.
- **Plugin System (`core/plugin/`):** Core modules for Android Studio/VS Code style extensibility.
- **Termux Terminal System (`core/termux/`):** Termux-inspired terminal emulator modules.
- **Tooling Bridge (`core/tooling/`):** Bridge between Gradle outputs, Termux terminal, and IDE log views.
- **Convention Plugins:** Custom Gradle plugins for centralized build logic.
- **Centralized Resources:** `core/resources` module for shared UI strings.
- **Centralized Logging:** `core/logger` module with runtime-toggleable logging.
- **Shared Utilities (`core/utils`):** Common utility module for file operations and project validation.

## External Dependencies
- **Proto DataStore:** User preferences and settings.
- **EncryptedSharedPreferences:** Secure credential storage.
- **AndroidX Navigation Compose:** In-app navigation.
- **Jetpack Compose libraries:** UI development.
- **Material Design 3 libraries:** UI components and styling.
- **Accompanist Permissions:** Runtime permission handling.
- **Sora Editor (io.github.Rosemoe.sora-editor):** Advanced Android code editor library.
- **Android Tree-Sitter (com.itsaky.androidide.treesitter):** Tree-sitter bindings for syntax parsing (Java, Kotlin, XML, JSON, C++, Log).
- **OkHttp & Retrofit:** Network operations for update checking via GitHub API.
- **WorkManager:** Background periodic update checking.
- **Google Material library (`com.google.android.material:material`):** Material3 theme support.
- **JGit (org.eclipse.jgit:org.eclipse.jgit):** Pure Java Git implementation.