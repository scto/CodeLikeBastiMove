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
- **Build System:** Gradle with Kotlin DSL and custom Convention Plugins.
- **SDKs:** Compile SDK 36, Target SDK 35, Minimum SDK 29.
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
- **Modular Architecture:** Core modules (`core-ui`, `core-resources`, `core-datastore`, `core-logger`, `templates-api`, `templates-impl`, `actions-api`, `actions-impl`, `plugin-api`, `plugin-impl`) and distinct feature modules.
- **Git Module (`features/git`):** Comprehensive Git version control integration with AndroidIDE-inspired UI, including GitOperations interface, data models, repository for binary interaction, ViewModel, and dedicated UI screens for changes, history, branches, remotes, and settings.
- **Theme Builder Module (`feature-themebuilder`):** Dedicated module for Material Theme Builder functionality with data models, color utilities, UI components, and theme export generators.
- **Sub-Module Maker (`feature-submodulemaker`):** Dedicated module for creating new Gradle sub-modules with data models, UI components, and a module generator.
- **Asset Studio (`feature-assetstudio`):** Dedicated module for Vector Asset Studio functionality with data models, SVG to AVD converter, icon repository system, and editor screens.
- **Sora Editor (`feature-soraeditor`):** Advanced code editor module using Rosemoe's sora-editor library, featuring language support, theming, UI widgets, Jetpack Compose integration, and a plugin system for extensions.
- **Action System (`actions-api` and `actions-impl`):** VS Code-style command/action system for event-driven architecture, including interfaces for actions, events, registry, keybinding, and plugin contributions.
- **Plugin System (`plugin-api` and `plugin-impl`):** Core modules for Android Studio/VS Code style extensibility, defining plugin descriptors, lifecycle, context, extension points, event system, and annotations. Implementation includes a plugin manager, dynamic loader, and security manager.
- **Termux Terminal System (`features/termux/`):** Termux-inspired terminal emulator modules providing core shared utilities, terminal emulation engine, and UI components for integration.
- **Tooling Bridge (`tooling-api` and `tooling-impl`):** Bridge between Gradle outputs, Termux terminal, and IDE log views, providing interfaces for tooling integration and an implementation coordinating various providers.
- **Convention Plugins:** Custom Gradle plugins centralize build logic and dependencies.
- **Centralized Resources:** `core-resources` module for shared resources.
- **Centralized Logging:** `core-logger` module with a facade wrapping `android.util.Log` and runtime-toggleable logging.

## External Dependencies
- **Proto DataStore:** User preferences and settings.
- **EncryptedSharedPreferences:** Secure credential storage.
- **AndroidX Navigation Compose:** In-app navigation.
- **Jetpack Compose libraries:** UI development.
- **Material Design 3 libraries:** UI components and styling.
- **Accompanist Permissions:** Runtime permission handling.
- **Sora Editor (io.github.Rosemoe.sora-editor):** Advanced Android code editor library.
- **Android Tree-Sitter:** Java bindings for Tree-sitter parsing library.