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
- **Code Editor:** Advanced Sora Editor integration with TextMate and TreeSitter syntax highlighting, supporting Java, Kotlin, XML, Gradle, C/C++, Makefile, and more. Features tabbed editing, undo/redo, find/replace, and multiple color themes.
- **File System Interaction:** Hierarchical file structure display (Android, Project, Packages views) with file operations, comprehensive file system browsing and project selection.
- **Asset Studio:** Comprehensive Vector Asset Studio with icon repository system (Material, Feather), SVG to AVD conversion, AVD editor, and export options.
- **Theming:** Full Material Theme Builder with interactive color picker, tonal palettes, font selection, platform selector, dynamic color toggle, schema style selector, and export options (Jetpack Compose, Android XML, Web/CSS, JSON).
- **Onboarding:** Multi-page flow for initial setup, including permission handling (File Access, Usage Stats, Battery Optimization) with live permission checking and self-healing mechanisms.
- **Scoped Storage:** Implemented Android 10+ scoped storage using DocumentFile and Storage Access Framework.

**System Design Choices:**
- **Modular Architecture:** Core modules (`core-ui`, `core-resources`, `core-datastore`, `core-logger`, `templates-api`, `templates-impl`, `actions-api`, `actions-impl`, `plugin-api`, `plugin-impl`) and distinct feature modules for clear separation of concerns and maintainability.
- **Theme Builder Module (`feature-themebuilder`):** Dedicated feature module for Material Theme Builder functionality, organized into:
  - `model/` - Data classes (ThemeColors)
  - `util/` - Color utilities, presets, and helper functions
  - `components/` - Reusable UI components (ColorPickerDialog, FontComponents, ColorSections, PreviewComponents, BottomActionBar, ThemeHeader)
  - `export/` - Theme export generators for multiple platforms (Jetpack Compose, Android XML, Web/CSS, JSON)
- **Sub-Module Maker (`feature-submodulemaker`):** Dedicated feature module for creating new Gradle sub-modules, organized into:
  - `model/` - Data classes (ModuleConfig, ProgrammingLanguage, ModuleType)
  - `components/` - Reusable UI components (LanguageSelector, ModuleTypeSelector, ModulePathInput, ComposeToggle, ModulePreviewCard)
  - `generator/` - ModuleGenerator for file creation (build.gradle.kts, AndroidManifest.xml, source directories, settings.gradle.kts updates)
- **Asset Studio (`feature-assetstudio`):** Dedicated feature module for Vector Asset Studio functionality, organized into:
  - `model/` - Data classes (VectorAsset, AVDDocument, VectorPath, VectorGroup, ExportConfig, IconProvider)
  - `converter/` - SvgToAvdConverter for SVG to Android Vector Drawable conversion
  - `repository/` - IconRepository system with MaterialIconsRepository, FeatherIconsRepository, and IconRepositoryManager
  - `screen/` - AssetStudioScreen (launcher) and VectorAssetStudioScreen (full editor with Browse, Create, Edit, Convert tabs)
- **Sora Editor (`feature-soraeditor`):** Advanced code editor module using Rosemoe's sora-editor library, organized into:
  - `model/` - Data classes (EditorLanguageType, EditorConfig, EditorTheme, EditorFile, EditorTab)
  - `language/` - LanguageRegistry, TextMateLanguageProvider, TreeSitterLanguageProvider for syntax highlighting
  - `theme/` - EditorThemeProvider with multiple color schemes (Dark Modern, Light Modern, Dracula, Monokai Pro, One Dark Pro)
  - `widget/` - SoraEditorView wrapping CodeEditor with full API
  - `compose/` - SoraEditor Composable and SoraEditorState for Jetpack Compose integration
  - `screen/` - SoraEditorScreen with tab support, status bar, and context menus
  - `viewmodel/` - SoraEditorViewModel for state management
  - `plugin/` - VS Code/Android Studio-style plugin system:
    - `theme/` - EditorThemePlugin interface for custom editor themes
    - `language/` - LanguagePackPlugin for language support (syntax, snippets, completion)
    - `action/` - EditorActionPlugin for editor actions with keybindings
    - `contribution/` - EditorPluginManager and contribution system
  - `assets/` - TextMate grammars/themes and TreeSitter query files
  - **Supported languages:** Java, Kotlin, XML, Gradle (Groovy & Kotlin DSL), AIDL, C, C++, Makefile, JSON, Log, Properties
  - **Highlighting modes:** TextMate (regex-based), TreeSitter (AST-based), Simple
- **Action System (`actions-api` and `actions-impl`):** VS Code-style command/action system for event-driven architecture:
  - `actions-api/` - Pure Kotlin contracts (no Android dependencies):
    - `action/` - Action, ActionContext, ActionResult, ActionCategory, Keybinding, ActionWhen
    - `event/` - ActionEvent, ActionEventBus, ActionEventListener for pub/sub event system
    - `registry/` - ActionRegistry interface, BuiltinActions constants
    - `keybinding/` - KeybindingService, KeyEvent, ResolvedKeybinding for keyboard shortcuts
    - `contribution/` - ActionContributor, EditorActionContribution, MenuContribution for plugin contributions
  - `actions-impl/` - Android implementation layer:
    - `registry/` - DefaultActionRegistry, DefaultContributorRegistry
    - `event/` - DefaultActionEventBus with SharedFlow-based event distribution
    - `executor/` - ActionExecutor, ActionInvoker for action execution with cancellation support
    - `keybinding/` - DefaultKeybindingService, DefaultKeybindingHandler
    - `integration/` - ActionSystem singleton, PluginActionIntegration for plugin-api bridge
- **Plugin System (`plugin-api` and `plugin-impl`):** Core modules for Android Studio/VS Code style extensibility, organized into:
  - `plugin-api/` - Pure Kotlin contracts with no Android dependencies:
    - `descriptor/` - PluginDescriptor, PluginState, PluginPermission, PluginCategory
    - `lifecycle/` - Plugin interface, AbstractPlugin, PluginLifecycleListener
    - `context/` - PluginContext, PluginLogger, PluginDataStore
    - `extension/` - ExtensionPoint abstractions (EditorAction, ToolWindowProvider, ThemeProvider, ProjectWizardExtension, CodeAnalyzer, CommandContribution, BackgroundTask)
    - `event/` - PluginEvent, PluginEventBus, PluginEventListener
    - `annotations/` - Declarative annotations (@PluginInfo, @RequiresPermission, @DependsOn, @ExtensionContribution)
  - `plugin-impl/` - Android implementation layer:
    - `manager/` - PluginManager with state machine, dependency resolution, host service registry
    - `loader/` - PluginLoader using DexClassLoader for dynamic plugin loading
    - `registry/` - DefaultExtensionRegistry, DefaultPluginEventBus
    - `security/` - PluginSecurityManager with signature verification, permission management, compatibility checks
    - `storage/` - PluginStorage for persisting enabled plugins and plugin settings
- **Convention Plugins:** Custom Gradle plugins centralize build logic, SDK versions, and common dependencies across modules.
- **Centralized Resources:** `core-resources` module for shared strings, dimensions, and Material 3 color palettes.
- **Centralized Logging:** `core-logger` module provides CLBMLogger facade that wraps android.util.Log with runtime-toggleable logging controlled via Settings and persisted in DataStore. Logging defaults to BuildConfig.LOGGING_DEFAULT_ENABLED (true for debug, false for release builds) and uses a `loggingInitialized` guard to properly seed defaults for both fresh installs and upgrades.

## External Dependencies
- **Proto DataStore:** User preferences and settings.
- **EncryptedSharedPreferences:** Secure credential storage.
- **AndroidX Navigation Compose:** In-app navigation.
- **Jetpack Compose libraries:** UI development.
- **Material Design 3 libraries:** UI components and styling.
- **Accompanist Permissions:** Runtime permission handling.
- **Sora Editor (io.github.Rosemoe.sora-editor):** Advanced Android code editor library with TextMate and TreeSitter support.
- **Android Tree-Sitter:** Java bindings for Tree-sitter parsing library, providing AST-based syntax highlighting.