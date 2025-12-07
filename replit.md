# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application developed with Kotlin and Jetpack Compose, showcasing modern Android development best practices through a highly modular architecture. The app provides a Material Design 3 experience with a comprehensive set of features including advanced navigation, theme management, a robust project template system for creating new Android projects, and an integrated development environment (IDE)-like interface. Its purpose is to demonstrate a well-structured, maintainable, and scalable Android application.

## User Preferences
I prefer iterative development and clear, concise explanations. Ask before making major architectural changes or significant modifications to existing features. I value well-documented code and a logical, modular structure.

## System Architecture
The application is an Android mobile application leveraging Jetpack Compose for the UI and a multi-module Gradle structure.

**UI/UX Decisions:**
- **Material Design 3:** Utilizes the latest Material You design language for a modern aesthetic.
- **Centralized Theming (core-ui):** Manages app-wide themes, colors (light/dark schemes), typography, icons, and gradients.
- **Dynamic Colors:** Supports Material You dynamic color feature with a toggle.
- **Navigation:** Implements a Material Design 3 modal navigation drawer and AndroidX Navigation Compose for screen navigation.
- **Main Screen:** Features a central IDE-style container with a TopAppBar (save, run, debug actions), a NavigationRail for content switching (Editor, Project, Git, Assets, Theme, Layout), and a BottomSheetBar for development panels (Terminal, Build Output, Logcat, Problems, TODO).

**Technical Implementations:**
- **Language:** Kotlin 2.2.20
- **UI Framework:** Jetpack Compose
- **Build System:** Gradle 8.14.3 with Kotlin DSL and custom Convention Plugins for consistent module configuration.
- **SDKs:** Compile SDK 36, Target SDK 35, Minimum SDK 29.
- **Architecture:** MVVM (Model-View-ViewModel) with ViewModel and StateFlow for reactive UI, emphasizing a multi-module design.
- **Data Persistence:** Proto DataStore for user preferences (e.g., theme settings) and EncryptedSharedPreferences for secure credential storage (e.g., Git credentials).
- **Project Templates:** Provides 5 distinct project templates (Empty Activity, Empty Compose Activity, Bottom Navigation Activity, Navigation Drawer Activity, Tabbed Activity), configurable by project name, package, minimum SDK, and language (Java/Kotlin, Groovy/Kotlin DSL).
- **Code Editor:** Features a tabbed code editor with monospace font, line numbers, and smooth animations.
- **File TreeView:** Hierarchical file structure display with expand/collapse and slideable functionality.
- **Git Module:** Comprehensive UI for Git commands categorized by function (Setup, Basic Snapshotting, Branching, Sharing, Inspection, Patching, Administration).
- **Onboarding:** A multi-page onboarding flow to guide users through initial setup.

**System Design Choices:**
- **Modular Architecture:** Core modules (`core-ui`, `core-resources`, `core-datastore`, `templates-api`, `templates-impl`) and feature modules (`feature-home`, `feature-gallery`, `feature-slideshow`, `feature-settings`, `feature-editor`, `feature-git`, `feature-onboarding`, `feature-main`, `feature-treeview`) are used for clear separation of concerns and improved maintainability.
- **Convention Plugins:** Custom Gradle convention plugins (`codelikebastimove.android.application`, `codelikebastimove.android.library`, `codelikebastimove.android.feature`, etc.) centralize build logic, SDK versions, and common dependencies, ensuring consistency across modules.
- **Centralized Resources:** Dedicated `core-resources` module for shared strings, dimensions, and colors.

## External Dependencies
- **Proto DataStore:** For user preferences and settings persistence.
- **EncryptedSharedPreferences:** For securely storing sensitive data like Git credentials.
- **AndroidX Navigation Compose:** For managing in-app navigation.
- **Jetpack Compose libraries:** For UI development.
- **Material Design 3 libraries:** For UI components and styling.