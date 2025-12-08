# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application built with Kotlin and Jetpack Compose, showcasing modern Android development practices through a highly modular architecture. It provides a Material Design 3 experience with features like advanced navigation, theme management, a robust project template system, and an integrated development environment (IDE)-like interface inspired by AndroidIDE. The project aims to demonstrate a well-structured, maintainable, and scalable Android application, focusing on mobile-first development and an intuitive user experience.

## User Preferences
I prefer iterative development and clear, concise explanations. Ask before making major architectural changes or significant modifications to existing features. I value well-documented code and a logical, modular structure.

## System Architecture
The application is an Android mobile application leveraging Jetpack Compose for the UI and a multi-module Gradle structure.

**UI/UX Decisions:**
- **Material Design 3:** Utilizes the latest Material You design language with dynamic colors for a modern, adaptive aesthetic.
- **AndroidIDE-Inspired Interface:** Features a home screen with action cards, an IDE workspace with drawer navigation, bottom sheet panels, and a NavigationRail for content switching.
- **Theming:** Centralized theme management (colors, typography, icons, gradients) supporting light/dark schemes and dynamic colors.
- **Branding:** Custom "CLBM" launcher icon with cyan-to-purple gradient background matching the app's theme identity.
- **Navigation:** Type-safe screen navigation using sealed classes and AnimatedContent transitions.
- **IDE Workspace:** Includes a TopAppBar, NavigationRail for feature switching (Editor, Project, Git, Assets, Theme, Layout), and a BottomSheetBar for development panels (Terminal, Build Output, Logcat, Problems, TODO).

**Technical Implementations:**
- **Language & Frameworks:** Kotlin 2.2.20, Jetpack Compose.
- **Build System:** Gradle 8.14.3 with Kotlin DSL and custom Convention Plugins for consistent module configuration.
- **SDKs:** Compile SDK 36, Target SDK 35, Minimum SDK 29.
- **Architecture:** MVVM with ViewModel and StateFlow for reactive UI, emphasizing a multi-module design.
- **State Management:** MainViewModel manages navigation, project state, content selection, and UI component visibility.
- **Data Persistence:** Proto DataStore for user preferences and EncryptedSharedPreferences for secure data.
- **Project Management:** Provides 5 distinct project templates, project creation wizard, project list management, and Git repository cloning.
- **Code Editor:** Tabbed editor with monospace font, line numbers, BasicTextField, and a long-press context menu.
- **File System Interaction:** Hierarchical file structure display (Android, Project, Packages views) with file operations, comprehensive file system browsing and project selection.
- **Asset Studio:** Comprehensive Vector Asset Studio with icon repository system (Material, Feather), SVG to AVD conversion, AVD editor, and export options.
- **Theming:** Full Material Theme Builder with interactive color picker, tonal palettes, font selection, platform selector, dynamic color toggle, schema style selector, and export options (Jetpack Compose, Android XML, Web/CSS, JSON).
- **Onboarding:** Multi-page flow for initial setup, including permission handling (File Access, Usage Stats, Battery Optimization) with live permission checking and self-healing mechanisms.
- **Scoped Storage:** Implemented Android 10+ scoped storage using DocumentFile and Storage Access Framework.

**System Design Choices:**
- **Modular Architecture:** Core modules (`core-ui`, `core-resources`, `core-datastore`, `templates-api`, `templates-impl`) and distinct feature modules for clear separation of concerns and maintainability.
- **Convention Plugins:** Custom Gradle plugins centralize build logic, SDK versions, and common dependencies across modules.
- **Centralized Resources:** `core-resources` module for shared strings, dimensions, and Material 3 color palettes.

## External Dependencies
- **Proto DataStore:** User preferences and settings.
- **EncryptedSharedPreferences:** Secure credential storage.
- **AndroidX Navigation Compose:** In-app navigation.
- **Jetpack Compose libraries:** UI development.
- **Material Design 3 libraries:** UI components and styling.
- **Accompanist Permissions:** Runtime permission handling.