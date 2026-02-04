# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application built with Kotlin and Jetpack Compose. It aims to demonstrate modern Android development practices through a highly modular architecture, offering a Material Design 3 experience. Key features include advanced navigation, comprehensive theme management, a robust project template system, and an integrated development environment (IDE)-like interface inspired by AndroidIDE. The project focuses on creating a well-structured, maintainable, and scalable application with a mobile-first approach and an intuitive user experience.

## User Preferences
I prefer iterative development and clear, concise explanations. Ask before making major architectural changes or significant modifications to existing features. I value well-documented code and a logical, modular structure.

## System Architecture
The application is an Android mobile application leveraging Jetpack Compose for the UI and a multi-module Gradle structure.

**UI/UX Decisions:**
- **Material Design 3:** Utilizes the latest Material You design language with dynamic colors.
- **AndroidIDE-Inspired Interface:** Features a home screen with action cards, an IDE workspace with drawer navigation, bottom sheet panels, and a NavigationRail.
- **Theming:** Centralized theme management supporting light/dark schemes and dynamic colors.
- **Branding:** Custom "CLBM" launcher icon with a cyan-to-purple gradient.
- **Navigation:** Type-safe screen navigation using sealed classes and AnimatedContent transitions.
- **IDE Workspace:** Includes a TopAppBar, NavigationRail for feature switching (Editor, Project, Git, Assets, Theme, Layout), and a BottomSheetBar for development panels (Terminal, Build Output, Logcat, Problems, TODO).

**Technical Implementations:**
- **Language & Frameworks:** Kotlin, Jetpack Compose.
- **Build System:** Gradle with Kotlin DSL and custom Convention Plugins (migrated to Koin DI, no Dagger Hilt).
- **SDKs:** Compile SDK 36, Target SDK 35, Minimum SDK 24.
- **Architecture:** MVVM with ViewModel and StateFlow, emphasizing a multi-module design.
- **State Management:** MainViewModel manages core application state.
- **Data Persistence:** Proto DataStore for user preferences and EncryptedSharedPreferences for secure data.
- **Project Management:** Provides 5 distinct project templates, creation wizard, project list management, and Git repository cloning.
- **Code Editor:** Advanced Sora Editor integration with TextMate and TreeSitter syntax highlighting, supporting various languages. Features tabbed editing, undo/redo, find/replace, and multiple color themes.
- **File System Interaction:** Hierarchical file structure display (Android, Project, Packages views) with file operations, comprehensive browsing, and project selection.
- **Asset Studio:** Comprehensive Vector Asset Studio with icon repository system, SVG to AVD conversion, AVD editor, and export options.
- **Theming:** Full Material Theme Builder with interactive color picker, tonal palettes, font selection, platform selector, dynamic color toggle, schema style selector, and export options.
- **Onboarding:** Multi-page flow for initial setup, including permission handling with live checking and self-healing.
- **Scoped Storage:** Implemented Android 10+ scoped storage using DocumentFile and Storage Access Framework.

**System Design Choices:**
- **Modular Architecture:** Organized into `core/` and `feature/` directories with grouped submodules:
  - `core/actions/` - Action system (actions-api, actions-impl)
  - `core/plugin/` - Plugin system (plugin-api, plugin-impl)
  - `core/templates/` - Project templates (templates-api, templates-impl)
  - `core/tooling/` - Tooling bridge (tooling-api, tooling-impl)
  - `core/termux/` - Terminal emulator (termux-shared, termux-emulator, termux-view, termux-app)
  - `core/datastore/` - Data persistence (datastore, datastore-proto)
  - `core/ui`, `core/resources`, `core/logger`
- **Git Module (`feature/git`):** Comprehensive Git version control integration with AndroidIDE-inspired UI, including GitOperations interface, data models, repository for binary interaction, ViewModel, and dedicated UI screens for changes, history, branches, remotes, and settings.
- **Theme Builder Module (`feature/themebuilder`):** Dedicated module for Material Theme Builder functionality with data models, color utilities, UI components, and theme export generators.
- **Sub-Module Maker (`feature/submodulemaker`):** Dedicated module for creating new Gradle sub-modules with AndroidIDE-inspired dark UI, supporting Kotlin Gradle notation `:folderName:moduleName`. Features include programming language selector (Kotlin/Java), folder path input, module name input, and automatic settings.gradle.kts updates.
- **Asset Studio (`feature/assetstudio`):** Dedicated module for Vector Asset Studio functionality with data models, SVG to AVD converter, icon repository system, and editor screens.
- **Tree View (`feature/treeview`):** File tree view component with AndroidIDE-inspired dark design, featuring brownish folder icons, animated chevron expand/collapse indicators, project header with settings and new folder buttons, and support for hidden files display.
- **Sora Editor (`feature/soraeditor`):** Advanced code editor module using Rosemoe's sora-editor library, featuring language support, theming, UI widgets, Jetpack Compose integration, and a plugin system for extensions.
- **Main (`feature/main`):** Main IDE workspace module with NavigationRail, drawer navigation, and BottomSheetBar panels.
- **Settings (`feature/settings`):** Application settings and preferences UI.
- **Onboarding (`feature/onboarding`):** Multi-page onboarding flow with permission handling.
- **Designer (`feature/designer`):** Layout designer for visual UI editing.
- **Explorer (`feature/explorer`):** File system exploration and browsing.
- **Action System (`core/actions/`):** VS Code-style command/action system for event-driven architecture, including interfaces for actions, events, registry, keybinding, and plugin contributions.
- **Plugin System (`core/plugin/`):** Core modules for Android Studio/VS Code style extensibility, defining plugin descriptors, lifecycle, context, extension points, event system, and annotations. Implementation includes a plugin manager, dynamic loader, and security manager.
- **Termux Terminal System (`core/termux/`):** Termux-inspired terminal emulator modules providing core shared utilities, terminal emulation engine, and UI components for integration.
- **Tooling Bridge (`core/tooling/`):** Bridge between Gradle outputs, Termux terminal, and IDE log views, providing interfaces for tooling integration and an implementation coordinating various providers.
- **Convention Plugins:** Custom Gradle plugins with `clbm.android.*` IDs centralize build logic and dependencies.
- **Centralized Resources:** `core/resources` module for shared resources.
- **Centralized Logging:** `core/logger` module with a facade wrapping `android.util.Log` and runtime-toggleable logging.

## External Dependencies
- **Proto DataStore:** User preferences and settings.
- **EncryptedSharedPreferences:** Secure credential storage.
- **AndroidX Navigation Compose:** In-app navigation.
- **Jetpack Compose libraries:** UI development.
- **Material Design 3 libraries:** UI components and styling.
- **Accompanist Permissions:** Runtime permission handling.
- **Sora Editor (io.github.Rosemoe.sora-editor):** Advanced Android code editor library.
- **Android Tree-Sitter:** Java bindings for Tree-sitter parsing library.

## Recent Changes (February 2026)
- **Authentication Removed:** Removed all authentication modules (core/auth, core/firebase/auth, feature/auth) and related UI (login, sign-out, account section in settings)
- **App Flow Simplified:** MainActivity now navigates directly to MainScreen after onboarding (no auth check)
- **Settings Screen Updated:** Removed account section and sign-out functionality
- **Major Module Restructuring:** Removed feature/home submodule; functionality moved to core/project-manager (project creation) and feature/git (clone repository)
- **New core/project-manager Module:** Contains ProjectManager interface, ProjectCreationOptions model, and CreateProjectScreen UI for project creation wizard
- **Enhanced feature/git Module:** Comprehensive Git UI with 7 sections: Changes, History, Branches, Remotes, Stash, Tags, Settings
- **New Git UI Screens:** GitCloneScreen (with recursive submodules support), GitStashScreen, GitTagsScreen, GitDiffScreen
- **GitViewModel Extensions:** Added cloneRepository, addRemote, removeRemote, getDiff methods
- **Navigation Updates:** MainDestination.CloneRepository replaced with MainDestination.GitClone and GitPanel
- **Complete Resource Centralization:** All UI strings in 9 feature modules now use stringResource(R.string.xxx) pattern instead of hardcoded text
- **Extended String Resources:** Added 50+ new string resources to core/resources/src/main/res/values/strings.xml covering German and English text for project management, editor actions, designer, settings, theme builder, asset studio, sub-module maker, and Git UI
- **Resource Access Pattern:** All feature modules access core:resources via FeatureConventionPlugin's api dependency

## Previous Changes (February 2026)
- **Editor Settings Architecture:** Comprehensive EditorSettings system with 17 configurable options (font size, font family, tab size, soft tabs, line numbers, word wrap, highlight current line, auto-indent, whitespace display, bracket matching, auto-close brackets/quotes, editor theme, minimap, sticky scroll, cursor blink rate, smooth scrolling)
- **Settings Module Refactoring:** Moved IDESettingsScreen from feature/main to dedicated feature/settings module with proper ViewModel integration
- **EditorSettingsScreen:** New comprehensive UI for editor preferences with sliders, toggles, and selection dialogs
- **Proto DataStore Extension:** Added EditorSettingsProto message with proper defaults and type-safe persistence
- **UserPreferencesRepository Enhancement:** Added toEditorSettings/toProto conversion functions and individual setter methods for each editor setting
- **SettingsViewModel:** Wraps repository methods with proper coroutine scoping for thread-safe preference updates
- **Navigation Integration:** EditorSettings destination added to MainNavigation, accessible from IDE Settings screen
- **SoraEditor TreeSitter Implementation:** TreeSitterLanguageProvider now properly implements LanguageProvider interface with full support for Kotlin, Java, XML, JSON, C/C++, and Log languages via AndroidIDE tree-sitter grammars
- **TextMate YAML/TOML Support:** Added complete TextMate grammar configurations for YAML and TOML file types (note: tree-sitter grammars not available for these in AndroidIDE library)
- **EditorThemeProvider Enhancement:** Comprehensive color scheme mappings for text visibility including TEXT_NORMAL, LINE_NUMBER, COMPLETION_WND colors, and all syntax highlighting tokens
- **Version Catalog Fixes:** Sora Editor libraries properly referenced with version.ref = "editor" (0.23.5)
- **Dependency Reference Fix:** Fixed libs.androidide.ts to libs.androidide.ts.core in soraeditor build.gradle.kts

## Previous Changes (January 2026)
- **Build Fixes Completed:** Successfully resolved all compilation errors to achieve BUILD SUCCESSFUL
- **Material3 Theme Fix:** Added Google Material library (`com.google.android.material:material:1.12.0`) to `core:resources` module to resolve Theme.Material3.Light.NoActionBar and Material3 color attribute errors
- **MVVMCleanArchitectureTemplate Fix:** Renamed conflicting methods `generateRootBuildGradleKts` and `generateRootBuildGradleGroovy` to `generateMvvmRootBuildGradleKts` and `generateMvvmRootBuildGradleGroovy` to avoid hiding supertype members
- **Kotlin Reserved Keyword Refactoring:** Renamed `when` parameters to `condition` throughout keybinding/action APIs (DefaultKeybindingService, PluginActionIntegration, EditorActionPlugin)
- **Platform Declaration Clash Fix:** Changed EditorThemePlugin from abstract property `themeDefinition` to abstract function `createThemeDefinition()`
- **Smart Cast Fixes:** Resolved smart cast issues in PluginManager, TerminalSession, and DefaultLogViewProvider by caching nullable values in local variables
- **TreeSitter Workaround:** Temporarily simplified TreeSitterLanguageProvider to return EmptyLanguage() until proper dependencies are available
- **APK Generated:** Debug APK successfully built at `app/build/outputs/apk/debug/app-debug.apk`