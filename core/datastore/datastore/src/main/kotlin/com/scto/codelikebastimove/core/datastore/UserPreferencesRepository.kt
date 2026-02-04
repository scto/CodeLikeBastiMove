package com.scto.codelikebastimove.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.scto.codelikebastimove.core.datastore.proto.BuildToolsVersion as ProtoBuildToolsVersion
import com.scto.codelikebastimove.core.datastore.proto.ClonedRepositoryProto
import com.scto.codelikebastimove.core.datastore.proto.EditorSettingsProto
import com.scto.codelikebastimove.core.datastore.proto.GitConfigProto
import com.scto.codelikebastimove.core.datastore.proto.GradleInfoProto
import com.scto.codelikebastimove.core.datastore.proto.OnboardingConfigProto
import com.scto.codelikebastimove.core.datastore.proto.OpenJdkVersion as ProtoOpenJdkVersion
import com.scto.codelikebastimove.core.datastore.proto.ProjectProto
import com.scto.codelikebastimove.core.datastore.proto.ProjectTemplateType as ProtoProjectTemplateType
import com.scto.codelikebastimove.core.datastore.proto.TemplateInfoProto
import com.scto.codelikebastimove.core.datastore.proto.TemplateRegistryProto
import com.scto.codelikebastimove.core.datastore.proto.ThemeMode as ProtoThemeMode
import com.scto.codelikebastimove.core.datastore.proto.UserPreferencesProto
import com.scto.codelikebastimove.core.datastore.proto.VersionCatalogBundleProto
import com.scto.codelikebastimove.core.datastore.proto.VersionCatalogEntryProto
import com.scto.codelikebastimove.core.datastore.proto.VersionCatalogLibraryProto
import com.scto.codelikebastimove.core.datastore.proto.VersionCatalogPluginProto
import com.scto.codelikebastimove.core.datastore.proto.VersionCatalogProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.userPreferencesStore: DataStore<UserPreferencesProto> by
  dataStore(fileName = "user_preferences.pb", serializer = UserPreferencesSerializer)

class UserPreferencesRepository(private val context: Context) {

  val userPreferences: Flow<UserPreferences> =
    context.userPreferencesStore.data.map { proto ->
      UserPreferences(
        themeMode = proto.themeMode.toThemeMode(),
        dynamicColorsEnabled = proto.dynamicColorsEnabled,
        gitConfig = proto.gitConfig.toGitConfig(),
        clonedRepositories = proto.clonedRepositoriesList.map { it.toClonedRepository() },
        onboardingConfig = proto.onboardingConfig.toOnboardingConfig(),
        rootDirectory = proto.rootDirectory,
        projects = proto.projectsList.map { it.toStoredProject() },
        currentProjectPath = proto.currentProjectPath,
        loggingEnabled = proto.loggingEnabled,
        loggingInitialized = proto.loggingInitialized,
        editorSettings = proto.editorSettings.toEditorSettings(),
      )
    }

  val loggingEnabled: Flow<Boolean> =
    context.userPreferencesStore.data.map { proto -> proto.loggingEnabled }

  suspend fun getLoggingEnabledOnce(): Boolean {
    return loggingEnabled.first()
  }

  suspend fun setLoggingEnabled(enabled: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      currentPrefs.toBuilder().setLoggingEnabled(enabled).setLoggingInitialized(true).build()
    }
  }

  suspend fun isLoggingInitialized(): Boolean {
    return context.userPreferencesStore.data.first().loggingInitialized
  }

  suspend fun initializeLoggingIfNeeded(defaultEnabled: Boolean) {
    val isInitialized = isLoggingInitialized()
    if (!isInitialized) {
      setLoggingEnabled(defaultEnabled)
    }
  }

  val onboardingConfig: Flow<OnboardingConfig> =
    context.userPreferencesStore.data.map { proto -> proto.onboardingConfig.toOnboardingConfig() }

  suspend fun isOnboardingCompleted(): Boolean {
    return onboardingConfig.first().onboardingCompleted
  }

  val gitConfig: Flow<GitConfig> =
    context.userPreferencesStore.data.map { proto -> proto.gitConfig.toGitConfig() }

  suspend fun getGitConfigOnce(): GitConfig {
    return gitConfig.first()
  }

  suspend fun setThemeMode(themeMode: ThemeMode) {
    context.userPreferencesStore.updateData { currentPrefs ->
      currentPrefs.toBuilder().setThemeMode(themeMode.toProtoThemeMode()).build()
    }
  }

  suspend fun setDynamicColorsEnabled(enabled: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      currentPrefs.toBuilder().setDynamicColorsEnabled(enabled).build()
    }
  }

  suspend fun setGitConfig(userName: String, userEmail: String) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val gitConfigProto =
        GitConfigProto.newBuilder().setUserName(userName).setUserEmail(userEmail).build()
      currentPrefs.toBuilder().setGitConfig(gitConfigProto).build()
    }
  }

  suspend fun addClonedRepository(repository: ClonedRepository) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val repoProto =
        ClonedRepositoryProto.newBuilder()
          .setPath(repository.path)
          .setUrl(repository.url)
          .setBranch(repository.branch)
          .setClonedAt(repository.clonedAt)
          .build()
      currentPrefs.toBuilder().addClonedRepositories(repoProto).build()
    }
  }

  suspend fun removeClonedRepository(path: String) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val updatedRepos = currentPrefs.clonedRepositoriesList.filter { it.path != path }
      currentPrefs
        .toBuilder()
        .clearClonedRepositories()
        .addAllClonedRepositories(updatedRepos)
        .build()
    }
  }

  suspend fun setOnboardingCompleted(completed: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        currentPrefs.onboardingConfig.toBuilder().setOnboardingCompleted(completed).build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setFileAccessPermissionGranted(granted: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        currentPrefs.onboardingConfig.toBuilder().setFileAccessPermissionGranted(granted).build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setUsageAnalyticsPermissionGranted(granted: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        currentPrefs.onboardingConfig
          .toBuilder()
          .setUsageAnalyticsPermissionGranted(granted)
          .build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setBatteryOptimizationDisabled(disabled: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        currentPrefs.onboardingConfig.toBuilder().setBatteryOptimizationDisabled(disabled).build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setSelectedOpenJdkVersion(version: OpenJdkVersion) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        currentPrefs.onboardingConfig
          .toBuilder()
          .setSelectedOpenjdkVersion(version.toProtoOpenJdkVersion())
          .build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setSelectedBuildToolsVersion(version: BuildToolsVersion) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        currentPrefs.onboardingConfig
          .toBuilder()
          .setSelectedBuildToolsVersion(version.toProtoBuildToolsVersion())
          .build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setGitEnabled(enabled: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto = currentPrefs.onboardingConfig.toBuilder().setGitEnabled(enabled).build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setGitLfsEnabled(enabled: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        currentPrefs.onboardingConfig.toBuilder().setGitLfsEnabled(enabled).build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setSshEnabled(enabled: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto = currentPrefs.onboardingConfig.toBuilder().setSshEnabled(enabled).build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setInstallationStarted(started: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        currentPrefs.onboardingConfig.toBuilder().setInstallationStarted(started).build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun setInstallationCompleted(completed: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        currentPrefs.onboardingConfig.toBuilder().setInstallationCompleted(completed).build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  suspend fun updateOnboardingConfig(config: OnboardingConfig) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val onboardingProto =
        OnboardingConfigProto.newBuilder()
          .setOnboardingCompleted(config.onboardingCompleted)
          .setFileAccessPermissionGranted(config.fileAccessPermissionGranted)
          .setUsageAnalyticsPermissionGranted(config.usageAnalyticsPermissionGranted)
          .setBatteryOptimizationDisabled(config.batteryOptimizationDisabled)
          .setSelectedOpenjdkVersion(config.selectedOpenJdkVersion.toProtoOpenJdkVersion())
          .setSelectedBuildToolsVersion(config.selectedBuildToolsVersion.toProtoBuildToolsVersion())
          .setGitEnabled(config.gitEnabled)
          .setGitLfsEnabled(config.gitLfsEnabled)
          .setSshEnabled(config.sshEnabled)
          .setInstallationStarted(config.installationStarted)
          .setInstallationCompleted(config.installationCompleted)
          .build()
      currentPrefs.toBuilder().setOnboardingConfig(onboardingProto).build()
    }
  }

  private fun ProtoThemeMode.toThemeMode(): ThemeMode =
    when (this) {
      ProtoThemeMode.THEME_MODE_LIGHT -> ThemeMode.LIGHT
      ProtoThemeMode.THEME_MODE_DARK -> ThemeMode.DARK
      ProtoThemeMode.THEME_MODE_FOLLOW_SYSTEM -> ThemeMode.FOLLOW_SYSTEM
      ProtoThemeMode.THEME_MODE_UNSPECIFIED,
      ProtoThemeMode.UNRECOGNIZED -> ThemeMode.FOLLOW_SYSTEM
    }

  private fun ThemeMode.toProtoThemeMode(): ProtoThemeMode =
    when (this) {
      ThemeMode.LIGHT -> ProtoThemeMode.THEME_MODE_LIGHT
      ThemeMode.DARK -> ProtoThemeMode.THEME_MODE_DARK
      ThemeMode.FOLLOW_SYSTEM -> ProtoThemeMode.THEME_MODE_FOLLOW_SYSTEM
    }

  private fun GitConfigProto?.toGitConfig(): GitConfig {
    return if (this == null) {
      GitConfig()
    } else {
      GitConfig(userName = userName ?: "", userEmail = userEmail ?: "")
    }
  }

  private fun ClonedRepositoryProto.toClonedRepository(): ClonedRepository {
    return ClonedRepository(path = path, url = url, branch = branch, clonedAt = clonedAt)
  }

  private fun OnboardingConfigProto?.toOnboardingConfig(): OnboardingConfig {
    return if (this == null) {
      OnboardingConfig()
    } else {
      OnboardingConfig(
        onboardingCompleted = onboardingCompleted,
        fileAccessPermissionGranted = fileAccessPermissionGranted,
        usageAnalyticsPermissionGranted = usageAnalyticsPermissionGranted,
        batteryOptimizationDisabled = batteryOptimizationDisabled,
        selectedOpenJdkVersion = selectedOpenjdkVersion.toOpenJdkVersion(),
        selectedBuildToolsVersion = selectedBuildToolsVersion.toBuildToolsVersion(),
        gitEnabled = gitEnabled,
        gitLfsEnabled = gitLfsEnabled,
        sshEnabled = sshEnabled,
        installationStarted = installationStarted,
        installationCompleted = installationCompleted,
      )
    }
  }

  private fun ProtoOpenJdkVersion.toOpenJdkVersion(): OpenJdkVersion =
    when (this) {
      ProtoOpenJdkVersion.OPENJDK_17 -> OpenJdkVersion.OPENJDK_17
      ProtoOpenJdkVersion.OPENJDK_22 -> OpenJdkVersion.OPENJDK_22
      ProtoOpenJdkVersion.OPENJDK_VERSION_UNSPECIFIED,
      ProtoOpenJdkVersion.UNRECOGNIZED -> OpenJdkVersion.OPENJDK_17
    }

  private fun OpenJdkVersion.toProtoOpenJdkVersion(): ProtoOpenJdkVersion =
    when (this) {
      OpenJdkVersion.OPENJDK_17 -> ProtoOpenJdkVersion.OPENJDK_17
      OpenJdkVersion.OPENJDK_22 -> ProtoOpenJdkVersion.OPENJDK_22
    }

  private fun ProtoBuildToolsVersion.toBuildToolsVersion(): BuildToolsVersion =
    when (this) {
      ProtoBuildToolsVersion.BUILD_TOOLS_35_0_1 -> BuildToolsVersion.BUILD_TOOLS_35_0_1
      ProtoBuildToolsVersion.BUILD_TOOLS_34_0_2 -> BuildToolsVersion.BUILD_TOOLS_34_0_2
      ProtoBuildToolsVersion.BUILD_TOOLS_33_0_1 -> BuildToolsVersion.BUILD_TOOLS_33_0_1
      ProtoBuildToolsVersion.BUILD_TOOLS_VERSION_UNSPECIFIED,
      ProtoBuildToolsVersion.UNRECOGNIZED -> BuildToolsVersion.BUILD_TOOLS_35_0_1
    }

  private fun BuildToolsVersion.toProtoBuildToolsVersion(): ProtoBuildToolsVersion =
    when (this) {
      BuildToolsVersion.BUILD_TOOLS_35_0_1 -> ProtoBuildToolsVersion.BUILD_TOOLS_35_0_1
      BuildToolsVersion.BUILD_TOOLS_34_0_2 -> ProtoBuildToolsVersion.BUILD_TOOLS_34_0_2
      BuildToolsVersion.BUILD_TOOLS_33_0_1 -> ProtoBuildToolsVersion.BUILD_TOOLS_33_0_1
    }

  private fun ProjectProto.toStoredProject(): StoredProject {
    return StoredProject(
      name = name,
      path = path,
      packageName = packageName,
      templateType = templateType.toProjectTemplateType(),
      createdAt = createdAt,
      lastOpenedAt = lastOpenedAt,
    )
  }

  private fun ProtoProjectTemplateType.toProjectTemplateType(): ProjectTemplateType =
    when (this) {
      ProtoProjectTemplateType.PROJECT_TEMPLATE_EMPTY_ACTIVITY -> ProjectTemplateType.EMPTY_ACTIVITY
      ProtoProjectTemplateType.PROJECT_TEMPLATE_EMPTY_COMPOSE -> ProjectTemplateType.EMPTY_COMPOSE
      ProtoProjectTemplateType.PROJECT_TEMPLATE_BOTTOM_NAVIGATION ->
        ProjectTemplateType.BOTTOM_NAVIGATION
      ProtoProjectTemplateType.PROJECT_TEMPLATE_NAVIGATION_DRAWER ->
        ProjectTemplateType.NAVIGATION_DRAWER
      ProtoProjectTemplateType.PROJECT_TEMPLATE_TABBED -> ProjectTemplateType.TABBED
      ProtoProjectTemplateType.PROJECT_TEMPLATE_MULTI_MODULE -> ProjectTemplateType.MULTI_MODULE
      ProtoProjectTemplateType.PROJECT_TEMPLATE_MVVM_CLEAN -> ProjectTemplateType.MVVM_CLEAN
      ProtoProjectTemplateType.PROJECT_TEMPLATE_WEAR_OS -> ProjectTemplateType.WEAR_OS
      ProtoProjectTemplateType.PROJECT_TEMPLATE_RESPONSIVE_FOLDABLE ->
        ProjectTemplateType.RESPONSIVE_FOLDABLE
      ProtoProjectTemplateType.PROJECT_TEMPLATE_UNSPECIFIED,
      ProtoProjectTemplateType.UNRECOGNIZED -> ProjectTemplateType.EMPTY_ACTIVITY
    }

  private fun ProjectTemplateType.toProtoProjectTemplateType(): ProtoProjectTemplateType =
    when (this) {
      ProjectTemplateType.EMPTY_ACTIVITY -> ProtoProjectTemplateType.PROJECT_TEMPLATE_EMPTY_ACTIVITY
      ProjectTemplateType.EMPTY_COMPOSE -> ProtoProjectTemplateType.PROJECT_TEMPLATE_EMPTY_COMPOSE
      ProjectTemplateType.BOTTOM_NAVIGATION ->
        ProtoProjectTemplateType.PROJECT_TEMPLATE_BOTTOM_NAVIGATION
      ProjectTemplateType.NAVIGATION_DRAWER ->
        ProtoProjectTemplateType.PROJECT_TEMPLATE_NAVIGATION_DRAWER
      ProjectTemplateType.TABBED -> ProtoProjectTemplateType.PROJECT_TEMPLATE_TABBED
      ProjectTemplateType.MULTI_MODULE -> ProtoProjectTemplateType.PROJECT_TEMPLATE_MULTI_MODULE
      ProjectTemplateType.MVVM_CLEAN -> ProtoProjectTemplateType.PROJECT_TEMPLATE_MVVM_CLEAN
      ProjectTemplateType.WEAR_OS -> ProtoProjectTemplateType.PROJECT_TEMPLATE_WEAR_OS
      ProjectTemplateType.RESPONSIVE_FOLDABLE ->
        ProtoProjectTemplateType.PROJECT_TEMPLATE_RESPONSIVE_FOLDABLE
    }

  private fun TemplateRegistryProto?.toTemplateRegistry(): TemplateRegistry {
    return if (this == null) {
      TemplateRegistry()
    } else {
      TemplateRegistry(
        totalTemplateCount = totalTemplateCount,
        registryLastUpdated = registryLastUpdated,
        registryVersion = registryVersion,
        templates = templatesList.map { it.toTemplateInfo() },
      )
    }
  }

  private fun TemplateInfoProto.toTemplateInfo(): TemplateInfo {
    return TemplateInfo(
      id = id,
      name = name,
      description = description,
      version = version,
      lastUpdated = lastUpdated,
      templateType = templateType.toProjectTemplateType(),
      gradleInfo = gradleInfo.toGradleInfo(),
      versionCatalog = versionCatalog.toVersionCatalog(),
      supportedLanguages = supportedLanguagesList.toList(),
      features = featuresList.toList(),
      minSdk = minSdk,
      targetSdk = targetSdk,
      compileSdk = compileSdk,
    )
  }

  private fun GradleInfoProto?.toGradleInfo(): GradleInfo {
    return if (this == null) {
      GradleInfo()
    } else {
      GradleInfo(
        gradleVersion = gradleVersion,
        distributionUrl = distributionUrl,
        agpVersion = agpVersion,
        kotlinVersion = kotlinVersion,
        composeBomVersion = composeBomVersion,
        usesVersionCatalog = usesVersionCatalog,
      )
    }
  }

  private fun VersionCatalogProto?.toVersionCatalog(): VersionCatalog {
    return if (this == null) {
      VersionCatalog()
    } else {
      VersionCatalog(
        versions = versionsList.map { VersionCatalogEntry(it.name, it.version) },
        libraries =
          librariesList.map { VersionCatalogLibrary(it.alias, it.group, it.name, it.versionRef) },
        plugins = pluginsList.map { VersionCatalogPlugin(it.alias, it.id, it.versionRef) },
        bundles = bundlesList.map { VersionCatalogBundle(it.alias, it.librariesList.toList()) },
      )
    }
  }

  private fun TemplateRegistry.toProto(): TemplateRegistryProto {
    return TemplateRegistryProto.newBuilder()
      .setTotalTemplateCount(totalTemplateCount)
      .setRegistryLastUpdated(registryLastUpdated)
      .setRegistryVersion(registryVersion)
      .addAllTemplates(templates.map { it.toProto() })
      .build()
  }

  private fun TemplateInfo.toProto(): TemplateInfoProto {
    return TemplateInfoProto.newBuilder()
      .setId(id)
      .setName(name)
      .setDescription(description)
      .setVersion(version)
      .setLastUpdated(lastUpdated)
      .setTemplateType(templateType.toProtoProjectTemplateType())
      .setGradleInfo(gradleInfo.toProto())
      .setVersionCatalog(versionCatalog.toProto())
      .addAllSupportedLanguages(supportedLanguages)
      .addAllFeatures(features)
      .setMinSdk(minSdk)
      .setTargetSdk(targetSdk)
      .setCompileSdk(compileSdk)
      .build()
  }

  private fun GradleInfo.toProto(): GradleInfoProto {
    return GradleInfoProto.newBuilder()
      .setGradleVersion(gradleVersion)
      .setDistributionUrl(distributionUrl)
      .setAgpVersion(agpVersion)
      .setKotlinVersion(kotlinVersion)
      .setComposeBomVersion(composeBomVersion)
      .setUsesVersionCatalog(usesVersionCatalog)
      .build()
  }

  private fun VersionCatalog.toProto(): VersionCatalogProto {
    return VersionCatalogProto.newBuilder()
      .addAllVersions(
        versions.map {
          VersionCatalogEntryProto.newBuilder().setName(it.name).setVersion(it.version).build()
        }
      )
      .addAllLibraries(
        libraries.map {
          VersionCatalogLibraryProto.newBuilder()
            .setAlias(it.alias)
            .setGroup(it.group)
            .setName(it.name)
            .setVersionRef(it.versionRef)
            .build()
        }
      )
      .addAllPlugins(
        plugins.map {
          VersionCatalogPluginProto.newBuilder()
            .setAlias(it.alias)
            .setId(it.id)
            .setVersionRef(it.versionRef)
            .build()
        }
      )
      .addAllBundles(
        bundles.map {
          VersionCatalogBundleProto.newBuilder()
            .setAlias(it.alias)
            .addAllLibraries(it.libraries)
            .build()
        }
      )
      .build()
  }

  val rootDirectory: Flow<String> =
    context.userPreferencesStore.data.map { proto -> proto.rootDirectory }

  val projects: Flow<List<StoredProject>> =
    context.userPreferencesStore.data.map { proto ->
      proto.projectsList.map { it.toStoredProject() }
    }

  suspend fun getRootDirectoryOnce(): String {
    return rootDirectory.first()
  }

  suspend fun getProjectsOnce(): List<StoredProject> {
    return projects.first()
  }

  suspend fun setRootDirectory(path: String) {
    context.userPreferencesStore.updateData { currentPrefs ->
      currentPrefs.toBuilder().setRootDirectory(path).build()
    }
  }

  suspend fun addProject(project: StoredProject) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val projectProto =
        ProjectProto.newBuilder()
          .setName(project.name)
          .setPath(project.path)
          .setPackageName(project.packageName)
          .setTemplateType(project.templateType.toProtoProjectTemplateType())
          .setCreatedAt(project.createdAt)
          .setLastOpenedAt(project.lastOpenedAt)
          .build()
      currentPrefs.toBuilder().addProjects(projectProto).build()
    }
  }

  suspend fun updateProjectLastOpened(path: String, timestamp: Long) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val updatedProjects =
        currentPrefs.projectsList.map { project ->
          if (project.path == path) {
            project.toBuilder().setLastOpenedAt(timestamp).build()
          } else {
            project
          }
        }
      currentPrefs.toBuilder().clearProjects().addAllProjects(updatedProjects).build()
    }
  }

  suspend fun removeProject(path: String) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val updatedProjects = currentPrefs.projectsList.filter { it.path != path }
      currentPrefs.toBuilder().clearProjects().addAllProjects(updatedProjects).build()
    }
  }

  suspend fun setCurrentProjectPath(path: String) {
    context.userPreferencesStore.updateData { currentPrefs ->
      currentPrefs.toBuilder().setCurrentProjectPath(path).build()
    }
  }

  val templateRegistry: Flow<TemplateRegistry> =
    context.userPreferencesStore.data.map { proto -> proto.templateRegistry.toTemplateRegistry() }

  suspend fun getTemplateRegistryOnce(): TemplateRegistry {
    return templateRegistry.first()
  }

  suspend fun updateTemplateRegistry(registry: TemplateRegistry) {
    context.userPreferencesStore.updateData { currentPrefs ->
      currentPrefs.toBuilder().setTemplateRegistry(registry.toProto()).build()
    }
  }

  suspend fun addTemplateInfo(templateInfo: TemplateInfo) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val currentRegistry = currentPrefs.templateRegistry.toTemplateRegistry()
      val updatedTemplates =
        currentRegistry.templates.toMutableList().apply {
          removeAll { it.id == templateInfo.id }
          add(templateInfo)
        }
      val updatedRegistry =
        currentRegistry.copy(
          totalTemplateCount = updatedTemplates.size,
          registryLastUpdated = System.currentTimeMillis(),
          templates = updatedTemplates,
        )
      currentPrefs.toBuilder().setTemplateRegistry(updatedRegistry.toProto()).build()
    }
  }

  suspend fun removeTemplateInfo(templateId: String) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val currentRegistry = currentPrefs.templateRegistry.toTemplateRegistry()
      val updatedTemplates = currentRegistry.templates.filter { it.id != templateId }
      val updatedRegistry =
        currentRegistry.copy(
          totalTemplateCount = updatedTemplates.size,
          registryLastUpdated = System.currentTimeMillis(),
          templates = updatedTemplates,
        )
      currentPrefs.toBuilder().setTemplateRegistry(updatedRegistry.toProto()).build()
    }
  }

  suspend fun getTemplateInfo(templateId: String): TemplateInfo? {
    return getTemplateRegistryOnce().templates.find { it.id == templateId }
  }

  val editorSettings: Flow<EditorSettings> =
    context.userPreferencesStore.data.map { proto -> proto.editorSettings.toEditorSettings() }

  suspend fun getEditorSettingsOnce(): EditorSettings {
    return editorSettings.first()
  }

  suspend fun updateEditorSettings(settings: EditorSettings) {
    context.userPreferencesStore.updateData { currentPrefs ->
      currentPrefs.toBuilder().setEditorSettings(settings.toProto()).build()
    }
  }

  suspend fun setEditorFontSize(fontSize: Float) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setFontSize(fontSize).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorFontFamily(fontFamily: String) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setFontFamily(fontFamily).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorTabSize(tabSize: Int) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setTabSize(tabSize).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorUseSoftTabs(useSoftTabs: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setUseSoftTabs(useSoftTabs).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorShowLineNumbers(showLineNumbers: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setShowLineNumbers(showLineNumbers).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorWordWrap(wordWrap: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setWordWrap(wordWrap).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorHighlightCurrentLine(highlightCurrentLine: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setHighlightCurrentLine(highlightCurrentLine).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorAutoIndent(autoIndent: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setAutoIndent(autoIndent).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorShowWhitespace(showWhitespace: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setShowWhitespace(showWhitespace).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorBracketMatching(bracketMatching: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setBracketMatching(bracketMatching).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorAutoCloseBrackets(autoCloseBrackets: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setAutoCloseBrackets(autoCloseBrackets).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorAutoCloseQuotes(autoCloseQuotes: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setAutoCloseQuotes(autoCloseQuotes).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorTheme(editorTheme: String) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setEditorTheme(editorTheme).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorMinimapEnabled(minimapEnabled: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setMinimapEnabled(minimapEnabled).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorStickyScroll(stickyScroll: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setStickyScroll(stickyScroll).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  suspend fun setEditorSmoothScrolling(smoothScrolling: Boolean) {
    context.userPreferencesStore.updateData { currentPrefs ->
      val editorProto = currentPrefs.editorSettings.toBuilder().setSmoothScrolling(smoothScrolling).build()
      currentPrefs.toBuilder().setEditorSettings(editorProto).build()
    }
  }

  private fun EditorSettingsProto?.toEditorSettings(): EditorSettings {
    return if (this == null || this == EditorSettingsProto.getDefaultInstance()) {
      EditorSettings()
    } else {
      EditorSettings(
        fontSize = if (fontSize > 0) fontSize else 14f,
        fontFamily = fontFamily.ifEmpty { "JetBrains Mono" },
        tabSize = if (tabSize > 0) tabSize else 4,
        useSoftTabs = useSoftTabs,
        showLineNumbers = showLineNumbers,
        wordWrap = wordWrap,
        highlightCurrentLine = highlightCurrentLine,
        autoIndent = autoIndent,
        showWhitespace = showWhitespace,
        bracketMatching = bracketMatching,
        autoCloseBrackets = autoCloseBrackets,
        autoCloseQuotes = autoCloseQuotes,
        editorTheme = editorTheme.ifEmpty { "Darcula" },
        minimapEnabled = minimapEnabled,
        stickyScroll = stickyScroll,
        cursorBlinkRate = if (cursorBlinkRate > 0) cursorBlinkRate else 530,
        smoothScrolling = smoothScrolling,
      )
    }
  }

  private fun EditorSettings.toProto(): EditorSettingsProto {
    return EditorSettingsProto.newBuilder()
      .setFontSize(fontSize)
      .setFontFamily(fontFamily)
      .setTabSize(tabSize)
      .setUseSoftTabs(useSoftTabs)
      .setShowLineNumbers(showLineNumbers)
      .setWordWrap(wordWrap)
      .setHighlightCurrentLine(highlightCurrentLine)
      .setAutoIndent(autoIndent)
      .setShowWhitespace(showWhitespace)
      .setBracketMatching(bracketMatching)
      .setAutoCloseBrackets(autoCloseBrackets)
      .setAutoCloseQuotes(autoCloseQuotes)
      .setEditorTheme(editorTheme)
      .setMinimapEnabled(minimapEnabled)
      .setStickyScroll(stickyScroll)
      .setCursorBlinkRate(cursorBlinkRate)
      .setSmoothScrolling(smoothScrolling)
      .build()
  }
}
