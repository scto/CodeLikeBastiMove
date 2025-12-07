# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application developed with Kotlin and Jetpack Compose, showcasing modern Android development best practices through a highly modular architecture. The app provides a Material Design 3 experience with a comprehensive set of features including advanced navigation, theme management, a robust project template system for creating new Android projects, and an integrated development environment (IDE)-like interface inspired by AndroidIDE. Its purpose is to demonstrate a well-structured, maintainable, and scalable Android application.

## Recent Changes (2025-12-07)
- **AndroidIDE-Style Redesign:** Completely redesigned the UI to match Android Code Studio/AndroidIDE's interface
- **New Home Screen:** Landing page with logo, action buttons (Create Project, Open Project, Clone Repo, Console, Settings, IDE Config, Documentation)
- **Open Project Screen:** ACS-style project picker with search bar, recent projects list with "Recent" badges, and folder browser button
- **Navigation System:** Implemented sealed class MainDestination for type-safe screen navigation with ViewModel state management
- **IDE Settings Screen:** Categorized settings (Konfigurieren, Datenschutz, Entwickleroptionen, Über)
- **AI Agent Screen:** Model selector dropdown (GPT-5, GPT-4, Claude-3, Gemini) with chat input
- **Asset Studio Screen:** Launch Studio button with Quick Actions (Create Drawable, Create Icon, Import Image)
- **Build Variants Screen:** Module variant selection with empty state handling
- **Sub-Module Maker Screen:** Create sub-modules with Kotlin/Java language selection
- **Console Screen:** Termux-style terminal with keyboard shortcuts bar
- **IDE Workspace Screen:** Project editor with hamburger menu, file tree drawer, bottom sheet for build output
- **Welcome State:** Empty editor displays "Android Code Studio" with bilingual instructions (English/German)
- **File Tree Drawer:** Navigation drawer with project file structure (.acside, .git, .github, .gradle, .kotlin, app, attached_assets, build, build-logic, core, features, gradle)
- **Editor Improvements:** Long-press context menu with Cut, Copy, Paste, Select All, Delete, Format Code, Find, Find & Replace, Undo, Redo
- **Project View Improvements:** Three view modes (Android, Project, Packages) with file operations context menu

## User Preferences
I prefer iterative development and clear, concise explanations. Ask before making major architectural changes or significant modifications to existing features. I value well-documented code and a logical, modular structure.

## System Architecture
The application is an Android mobile application leveraging Jetpack Compose for the UI and a multi-module Gradle structure.

**UI/UX Decisions:**
- **Material Design 3:** Utilizes the latest Material You design language for a modern aesthetic.
- **AndroidIDE-Inspired Interface:** Home screen with action cards, IDE workspace with drawer navigation, bottom sheet panels.
- **Centralized Theming (core-ui):** Manages app-wide themes, colors (light/dark schemes), typography, icons, and gradients.
- **Dynamic Colors:** Supports Material You dynamic color feature with a toggle.
- **Navigation:** Implements sealed class MainDestination with AnimatedContent transitions between screens.
- **Main Screen Flow:** Home → IDE Workspace (with file tree drawer, NavigationRail, BottomSheetBar) → Settings/AI Agent/Asset Studio/etc.
- **IDE Workspace:** Features TopAppBar (menu, stop, undo, more actions), NavigationRail for content switching (Editor, Project, Git, Assets, Theme, Layout), and BottomSheetBar for development panels (Terminal, Build Output, Logcat, Problems, TODO).

**Technical Implementations:**
- **Language:** Kotlin 2.2.20
- **UI Framework:** Jetpack Compose
- **Build System:** Gradle 8.14.3 with Kotlin DSL and custom Convention Plugins for consistent module configuration.
- **SDKs:** Compile SDK 36, Target SDK 35, Minimum SDK 29.
- **Architecture:** MVVM (Model-View-ViewModel) with ViewModel and StateFlow for reactive UI, emphasizing a multi-module design.
- **State Management:** MainViewModel manages navigation destination, project state, content selection, and bottom sheet visibility.
- **Data Persistence:** Proto DataStore for user preferences (e.g., theme settings) and EncryptedSharedPreferences for secure credential storage (e.g., Git credentials).
- **Project Templates:** Provides 5 distinct project templates (Empty Activity, Empty Compose Activity, Bottom Navigation Activity, Navigation Drawer Activity, Tabbed Activity), configurable by project name, package, minimum SDK, and language (Java/Kotlin, Groovy/Kotlin DSL).
- **Code Editor:** Features a tabbed code editor with monospace font, line numbers, editable BasicTextField, and long-press context menu.
- **File TreeView:** Hierarchical file structure display with expand/collapse, three view modes (Android/Project/Packages), and file operations.
- **Git Module:** Comprehensive UI for Git commands categorized by function (Setup, Basic Snapshotting, Branching, Sharing, Inspection, Patching, Administration).
- **Onboarding:** A multi-page onboarding flow to guide users through initial setup with permissions (File Access, Usage Stats, Battery Optimization).

**System Design Choices:**
- **Modular Architecture:** Core modules (`core-ui`, `core-resources`, `core-datastore`, `templates-api`, `templates-impl`) and feature modules (`feature-home`, `feature-gallery`, `feature-slideshow`, `feature-settings`, `feature-editor`, `feature-git`, `feature-onboarding`, `feature-main`, `feature-treeview`) are used for clear separation of concerns and improved maintainability.
- **Convention Plugins:** Custom Gradle convention plugins (`codelikebastimove.android.application`, `codelikebastimove.android.library`, `codelikebastimove.android.feature`, etc.) centralize build logic, SDK versions, and common dependencies, ensuring consistency across modules.
- **Centralized Resources:** Dedicated `core-resources` module for shared strings, dimensions, and colors.
- **Screen Organization:** New `screens` package in feature-main for destination screens (HomeScreen, IDESettingsScreen, AIAgentScreen, AssetStudioScreen, BuildVariantsScreen, SubModuleMakerScreen, ConsoleScreen, IDEWorkspaceScreen).

## External Dependencies
- **Proto DataStore:** For user preferences and settings persistence.
- **EncryptedSharedPreferences:** For securely storing sensitive data like Git credentials.
- **AndroidX Navigation Compose:** For managing in-app navigation.
- **Jetpack Compose libraries:** For UI development.
- **Material Design 3 libraries:** For UI components and styling.
- **Accompanist Permissions:** For runtime permission handling in onboarding.
