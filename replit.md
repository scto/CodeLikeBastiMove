# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application built with Kotlin and Jetpack Compose. It aims to demonstrate modern Android development practices through a highly modular architecture, offering a Material Design 3 experience. The project focuses on creating a well-structured, maintainable, and scalable application with a mobile-first approach and an intuitive user experience. Key capabilities include advanced navigation, comprehensive theme management, a robust project template system, and an integrated development environment (IDE)-like interface inspired by AndroidIDE, offering features like a code editor, asset studio, and Git integration.

## User Preferences
I prefer iterative development and clear, concise explanations. Ask before making major architectural changes or significant modifications to existing features. I value well-documented code and a logical, modular structure.

## System Architecture
The application is an Android mobile application leveraging Jetpack Compose for the UI and a multi-module Gradle structure.

**UI/UX Decisions:**
- **Material Design 3:** Utilizes the latest Material You design language with dynamic colors.
- **AndroidIDE-Inspired Interface:** Features a home screen with action cards, an IDE workspace with drawer navigation, bottom sheet panels, and a NavigationRail for feature switching (Editor, Project, Git, Assets, Theme, Layout). BottomSheetBar for development panels (Terminal, Build Output, Logcat, Problems, TODO).
- **Theming:** Centralized theme management supporting light/dark schemes and dynamic colors.
- **Branding:** Custom "CLBM" launcher icon with a cyan-to-purple gradient.
- **Navigation:** Type-safe screen navigation using sealed classes and AnimatedContent transitions.

**Technical Implementations:**
- **Language & Frameworks:** Kotlin, Jetpack Compose.
- **Build System:** Gradle with Kotlin DSL and custom Convention Plugins, using Koin DI.
- **SDKs:** Compile SDK 36, Target SDK 35, Minimum SDK 24.
- **Architecture:** MVVM with ViewModel and StateFlow, emphasizing a multi-module design.
- **State Management:** MainViewModel manages core application state.
- **Data Persistence:** Proto DataStore for user preferences and EncryptedSharedPreferences for secure data.
- **Project Management:** Provides 5 distinct project templates, a creation wizard, project list management, Git repository cloning, and import existing Android projects (with copy-to-workspace or link-in-place options).
- **Code Editor:** Advanced Sora Editor integration with TextMate and TreeSitter syntax highlighting, supporting various languages. Features tabbed editing, undo/redo, find/replace, and multiple color themes. Configurable editor settings for font, tabs, line numbers, word wrap, etc.
- **File System Interaction:** Hierarchical file structure display (Android, Project, Packages views) with file operations (create, rename, delete, copy, move), comprehensive browsing, and project selection.
- **Asset Studio:** Comprehensive Vector Asset Studio with icon repository system, SVG to AVD conversion, AVD editor, and export options.
- **Theming:** Full Material Theme Builder with interactive color picker, tonal palettes, font selection, platform selector, dynamic color toggle, schema style selector, and export options.
- **Onboarding:** Multi-page flow for initial setup, including permission handling with live checking and self-healing.
- **Scoped Storage:** Implemented Android 10+ scoped storage using DocumentFile and Storage Access Framework.

**System Design Choices:**
- **Modular Architecture:** Organized into `core/` and `feature/` directories with grouped submodules for actions, plugins, templates, tooling, terminal, datastore, UI, resources, and logging.
- **Home Module (`feature/home`):** Dedicated module for home screen, project creation wizard, import projects, and project list management. Contains HomeViewModel for project-related operations (create, delete, open, clone, import) and HomeScreen, CreateProjectScreen, ImportProjectScreen, OpenProjectScreen UI components.
- **Git Module (`feature/git`):** Comprehensive Git version control integration with AndroidIDE-inspired UI, including GitOperations interface, data models, repository for binary interaction, ViewModel, and dedicated UI screens for changes, history, branches, remotes, and settings. Supports repository cloning with recursive submodules, stashing, and tagging.
- **Theme Builder Module (`feature/themebuilder`):** Full Material Theme Builder with MaterialKolor-style UI, seed color-based theme generation, HCT color space, 7 scheme styles (Tonal Spot, Vibrant, Expressive, Fidelity, Monochrome, Neutral, Content), tonal palette visualization (13 tones), light/dark scheme generation, and export to Android project folders (colors.xml, themes.xml, Color.kt, Theme.kt).
- **Sub-Module Maker (`feature/submodulemaker`):** Dedicated module for creating new Gradle sub-modules (Kotlin/Java) with automatic settings.gradle.kts updates. Features Gradle notation input (e.g., `:core:common`, `:feature:auth`), real-time validation, auto-suggestions, package name generation, Compose support toggle, and preview of generated structure.
- **Asset Studio (`feature/assetstudio`):** Comprehensive Vector Asset Studio with icon repository system, SVG to AVD conversion, AVD editor, and export options. Includes AssetExporter for real Android project export with vector drawables, launcher icons (all mipmap densities), adaptive icons (foreground/background layers), notification icons, and action bar icons.
- **Designer Module (`feature/designer`):** Dedicated module for Jetpack Compose live preview with ComposeParser for extracting @Composable functions, LivePreviewRenderer for visual rendering, CodeSynchronizer for bidirectional code updates, and ComposePreviewScreen with split view (code/preview/properties). Supports 40+ Material3 components including Column, Row, Box, Card, Text, Button, TextField, NavigationBar, etc.
- **Tree View (`feature/treeview`):** Enhanced file tree view with 4 view modes (File, Package, Module, Project), file operations, context menus, search, hidden file toggle, and animated expand/collapse indicators.
- **Sora Editor (`feature/soraeditor`):** Advanced code editor module using Rosemoe's sora-editor library with full TreeSitter integration for syntax highlighting. Supports Java, Kotlin, XML, JSON, C++, and Log languages via android-tree-sitter bindings. Features TsLanguageSpec with custom .scm query files (highlights, blocks, brackets, locals), TsTheme with comprehensive token styling, EditorContent UI component for tabbed file editing, and 5 built-in color themes (Dark Modern, Light Modern, Dracula, Monokai Pro, One Dark Pro). Query files located in `feature/soraeditor/src/main/assets/tree-sitter-queries/`. Includes intelligent editing features: AutoCloseTag (auto-closes HTML/XML tags), BulletContinuation (continues Markdown lists/quotes), FontCache (custom font loading from assets/files), and EditorColorSchemeBuilder (dynamic JSON-based theme color mapping).
- **Settings Module (`feature/settings`):** Comprehensive settings management with multiple screens: General (theme, dynamic colors), Editor (font, tabs, line numbers, word wrap), Build & Run (parallel build, offline mode), Documentation (Android/Kotlin docs links), Help (FAQ, bug reporting), About (version info, update checker, GitHub links), and Debug (logging, update check interval configuration). Features clickable entries for GitHub Project site and GitHub Releases.
- **Updater Module (`core/updater`):** Periodic update checking system with configurable intervals (Never, Hourly, 6 hours, 12 hours, Daily, Weekly). Includes UpdateChecker for GitHub Releases API integration, UpdateRepository for state management, UpdateWorker for background periodic checks via WorkManager, and Koin DI module. Supports version comparison, release notes, and APK download URLs.
- **Action System (`core/actions/`):** VS Code-style command/action system for event-driven architecture.
- **Plugin System (`core/plugin/`):** Core modules for Android Studio/VS Code style extensibility with plugin manager, dynamic loader, and security manager.
- **Termux Terminal System (`core/termux/`)::** Termux-inspired terminal emulator modules providing core utilities, emulation engine, and UI components.
- **Tooling Bridge (`core/tooling/`):** Bridge between Gradle outputs, Termux terminal, and IDE log views.
- **Convention Plugins:** Custom Gradle plugins centralize build logic and dependencies.
- **Centralized Resources:** `core/resources` module for shared resources, including all UI strings.
- **Centralized Logging:** `core/logger` module with a facade wrapping `android.util.Log` and runtime-toggleable logging.

## External Dependencies
- **Proto DataStore:** User preferences and settings.
- **EncryptedSharedPreferences:** Secure credential storage.
- **AndroidX Navigation Compose:** In-app navigation.
- **Jetpack Compose libraries:** UI development.
- **Material Design 3 libraries:** UI components and styling.
- **Accompanist Permissions:** Runtime permission handling.
- **Sora Editor (io.github.Rosemoe.sora-editor):** Advanced Android code editor library.
- **Android Tree-Sitter (com.itsaky.androidide.treesitter):** Official AndroidIDE Tree-sitter bindings (v4.3.2), providing TSLanguageJava, TSLanguageKotlin, TSLanguageXml, TSLanguageJson, TSLanguageCpp, and TSLanguageLog for syntax parsing.
- **OkHttp & Retrofit:** Network operations for update checking via GitHub API.
- **WorkManager:** Background periodic update checking.
- **Google Material library (`com.google.android.material:material`):** Material3 theme support.