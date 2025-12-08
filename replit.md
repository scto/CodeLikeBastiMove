# CodeLikeBastiMove

## Overview
CodeLikeBastiMove is an Android mobile application developed with Kotlin and Jetpack Compose, showcasing modern Android development best practices through a highly modular architecture. The app provides a Material Design 3 experience with a comprehensive set of features including advanced navigation, theme management, a robust project template system for creating new Android projects, and an integrated development environment (IDE)-like interface inspired by AndroidIDE. Its purpose is to demonstrate a well-structured, maintainable, and scalable Android application.

## Recent Changes (2025-12-08)
- **Onboarding & Permission Gating Overhaul:**
  - **Live Permission Checking:** MainActivity and OnboardingScreen now use `Environment.isExternalStorageManager()` for real-time permission verification
  - **Permission Sync on Resume:** Both screens sync actual permission state to DataStore on every lifecycle RESUMED event
  - **Gated Onboarding Completion:** SummaryPage disables "Installation starten" button until file access permission is actually granted
  - **Warning Message:** Shows clear warning when permission is missing on summary page
  - **Settings Theme Controls:** IDESettingsScreen now has functional theme mode selection (Light/Dark/System) with dialog
  - **Dynamic Colors Toggle:** Functional toggle for Material You dynamic colors bound to DataStore
  - **Reset Onboarding:** Settings option to reset onboarding, which clears all flags and forces re-running setup
  - **Self-Healing Flow:** If permission is revoked externally, app automatically detects and routes back to onboarding
- **CLBMProjects Directory:** Changed root directory from "CLBM" to "CLBMProjects" in standard Android external storage (alongside Download, DCIM, Music, Documents)
- **feature-explorer Module:** New submodule for file system browsing and project selection
  - **ExplorerScreen:** Full file browser with navigation, sorting, and filtering
  - **ExplorerViewModel:** File system state management with navigation history, selection, sorting
  - **FileItem:** Data model for files with project detection, size display, and metadata
  - **Features:** Navigate up/back/forward, sort by name/date/size, filter by type/projects, show/hide hidden files, project badges
- **Vector Asset Studio System:** Comprehensive icon management and AVD creation tool
  - **Icon Repository System:** Interface-based providers for Material Icons, Feather Icons with extensible architecture
  - **Icon Browser:** Grid/List view toggle, search, category filtering, multi-select support
  - **Provider Switching:** Dropdown to switch between icon providers (Material Icons, Feather Icons)
  - **SVG to AVD Converter:** Convert SVG files to Android Vector Drawable format with path parsing
  - **AVD Editor:** Visual editor with path listing, color preview, zoom controls, path management
  - **Export Options:** Export to AVD XML, SVG, or Jetpack Compose ImageVector code
  - **Create Tab:** Quick templates (Circle, Square, Triangle, Star, Heart) and empty AVD creation
  - **Convert Tab:** SVG import dialog and export format selection
  - New navigation destinations: MainDestination.VectorAssetStudio
  - Home screen actions updated with Asset Studio and Vector Asset Studio quick links
- **CLBM Branding Redesign:** Updated app branding from "Android Code Studio" / "ACS" to "Code Like Basti Move" / "CLBM"
  - New stylish logo with cyan-to-purple gradient (0xFF00D9FF → 0xFF00B4D8 → 0xFF7C3AED → 0xFFA855F7)
  - "CLBM" text with ExtraBold weight and letter spacing for modern look
  - Updated across HomeScreen, OpenProjectScreen, CreateProjectScreen, EditorContent, and strings.xml
- **Feature Designer Fixes:** Fixed compilation errors in designer module (ValidationResult imports, ThemeDescriptor parameters, type references)
- **Scoped Storage Support:** Implemented Android 10+ scoped storage using DocumentFile and Storage Access Framework

## Recent Changes (2025-12-07)
- **Navigation Stack & Back Handling:** MainViewModel now maintains a navigation stack for proper Android back button behavior. Back swipe navigates within the app, only closing when on Home screen.
- **CLBM Root Directory:** App creates a CLBM folder in external storage (or app files fallback) on first start, stored in DataStore preferences.
- **CreateProjectScreen:** Full project creation wizard with template selection (5 templates), project name, package name, minimum SDK dropdown, Kotlin/Java toggle, and Kotlin DSL toggle.
- **CloneRepositoryScreen:** Git repository cloning UI with URL, branch, shallow clone, and single branch options.
- **Real Project List:** OpenProjectScreen now displays projects from DataStore with search, "Recent" badges, delete button, and last-opened timestamp.
- **Project CRUD in ViewModel:** createProject(), onOpenProject(), deleteProject() methods with DataStore persistence.
- **AndroidIDE-Style Redesign:** Completely redesigned the UI to match Android Code Studio/AndroidIDE's interface
- **New Home Screen:** Landing page with logo, action buttons (Create Project, Open Project, Clone Repo, Console, Settings, IDE Config, Documentation)
- **Open Project Screen:** CLBM-style project picker with search bar, recent projects list with "Recent" badges, and folder browser button
- **Navigation System:** Implemented sealed class MainDestination for type-safe screen navigation with ViewModel state management
- **IDE Settings Screen:** Categorized settings (Konfigurieren, Datenschutz, Entwickleroptionen, Über)
- **AI Agent Screen:** Model selector dropdown (GPT-5, GPT-4, Claude-3, Gemini) with chat input
- **Asset Studio Screen:** Launch Studio button with Quick Actions (Create Drawable, Create Icon, Import Image)
- **Build Variants Screen:** Module variant selection with empty state handling
- **Sub-Module Maker Screen:** Create sub-modules with Kotlin/Java language selection
- **Console Screen:** Termux-style terminal with keyboard shortcuts bar
- **IDE Workspace Screen:** Project editor with hamburger menu, file tree drawer, bottom sheet for build output
- **Welcome State:** Empty editor displays "Code Like Basti Move" with bilingual instructions (English/German)
- **Tabbed Drawer Navigation:** Left swipeable drawer now contains 5 tabs:
  - **Files Tab:** Project file tree with expand/collapse
  - **Build Tab:** Build Variants with module and variant selection
  - **Module Tab:** Sub-Module Maker with name, language (Kotlin/Java), and type selection
  - **Assets Tab:** Asset Studio with quick actions (Create Drawable, Icon, Import Image)
  - **Terminal Tab:** Embedded terminal with "Full Terminal" button to open BottomSheet
- **Editor Improvements:** Long-press context menu with Cut, Copy, Paste, Select All, Delete, Format Code, Find, Find & Replace, Undo, Redo
- **Project View Improvements:** Three view modes (Android, Project, Packages) with file operations context menu
- **Theme Builder:** Full Material Theme Builder with:
  - Preview/Edit tabs with page navigation (1 of 2, 2 of 2)
  - Interactive color picker dialog with HSL sliders and hex input
  - Clickable color scheme grids (Light/Dark) for editing colors
  - Tonal palettes (Primary, Secondary, Tertiary, Neutral, Neutral Variant)
  - Font selection dropdowns (Display/Body)
  - Platform selector (Android, Windows, Web, Linux)
  - Seed color selector with custom color picker
  - **Dynamic Color toggle** for Android 12+ wallpaper-based colors
  - **Schema Style selector** with 9 options: Tonal Spot, Neutral, Vibrant, Expressive, Fidelity, Content, Monochromatic, Rainbow, Fruit Salad
  - Export as ZIP functionality with 4 formats: **Jetpack Compose** (Color.kt, Theme.kt, Type.kt), Android XML, Web/CSS, JSON
  - FileProvider configured for secure file sharing
- **Module Dependencies Refactored:**
  - core-ui and core-resources now exposed via `api` for transitive access across all modules
  - All feature modules automatically get core-ui and core-resources through convention plugin
  - core-datastore, templates-api, and templates-impl now include core-resources
  - features aggregation module explicitly includes core-ui and core-resources
- **Enhanced core-resources:**
  - Complete Material 3 color palette (light and dark themes) in colors.xml
  - Comprehensive string resources for IDE functionality (80+ new strings)
  - Full dimension resources for spacing, corners, icons, and layout

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
