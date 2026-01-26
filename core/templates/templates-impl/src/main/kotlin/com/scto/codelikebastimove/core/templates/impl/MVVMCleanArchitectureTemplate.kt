package com.scto.codelikebastimove.core.templates.impl

import com.scto.codelikebastimove.core.datastore.ProjectTemplateType
import com.scto.codelikebastimove.core.datastore.VersionCatalog
import com.scto.codelikebastimove.core.datastore.VersionCatalogBundle
import com.scto.codelikebastimove.core.datastore.VersionCatalogEntry
import com.scto.codelikebastimove.core.datastore.VersionCatalogLibrary
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectFile

class MVVMCleanArchitectureTemplate : BaseVersionCatalogTemplate() {
    override val name: String = "MVVM Clean Architecture"
    override val description: String = "Creates a project with MVVM pattern and Clean Architecture layers"
    override val templateId: String = "mvvm-clean-architecture"
    override val templateVersion: String = "1.0.0"
    override val templateType: ProjectTemplateType = ProjectTemplateType.MVVM_CLEAN
    override val features: List<String> = listOf(
        "MVVM Architecture",
        "Clean Architecture",
        "Jetpack Compose",
        "ViewModel",
        "StateFlow",
        "Coroutines",
        "Hilt DI",
        "Repository Pattern"
    )

    override fun getVersionCatalog(): VersionCatalog {
        val base = createBaseComposeVersionCatalog()
        return base.copy(
            versions = base.versions + listOf(
                VersionCatalogEntry("hilt", "2.51.1"),
                VersionCatalogEntry("hiltNavigation", "1.2.0"),
                VersionCatalogEntry("coroutines", "1.8.1"),
                VersionCatalogEntry("navigationCompose", "2.8.5")
            ),
            libraries = base.libraries + listOf(
                VersionCatalogLibrary("hilt-android", "com.google.dagger", "hilt-android", "hilt"),
                VersionCatalogLibrary("hilt-android-compiler", "com.google.dagger", "hilt-android-compiler", "hilt"),
                VersionCatalogLibrary("androidx-hilt-navigation-compose", "androidx.hilt", "hilt-navigation-compose", "hiltNavigation"),
                VersionCatalogLibrary("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core", "coroutines"),
                VersionCatalogLibrary("kotlinx-coroutines-android", "org.jetbrains.kotlinx", "kotlinx-coroutines-android", "coroutines"),
                VersionCatalogLibrary("androidx-navigation-compose", "androidx.navigation", "navigation-compose", "navigationCompose")
            ),
            bundles = base.bundles + listOf(
                VersionCatalogBundle("coroutines", listOf("kotlinx-coroutines-core", "kotlinx-coroutines-android")),
                VersionCatalogBundle("hilt", listOf("hilt-android", "androidx-hilt-navigation-compose"))
            )
        )
    }

    override fun generateProject(config: ProjectConfig): List<ProjectFile> {
        val files = mutableListOf<ProjectFile>()
        val packagePath = config.packageName.replace(".", "/")

        files.add(ProjectFile("app/src/main/java/$packagePath", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/data", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/data/repository", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/domain", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/domain/model", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/domain/repository", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/domain/usecase", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/presentation", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/presentation/home", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/ui/theme", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/di", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/values", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/drawable", "", isDirectory = true))

        files.addAll(generateGradleWrapper(config))
        files.add(generateVersionCatalogToml(config))

        when (config.gradleLanguage) {
            GradleLanguage.KOTLIN_DSL -> {
                files.add(generateSettingsGradleKts(config))
                files.add(generateMvvmRootBuildGradleKts(config))
                files.add(generateAppBuildGradleKts(config))
            }
            GradleLanguage.GROOVY -> {
                files.add(generateSettingsGradleGroovy(config))
                files.add(generateMvvmRootBuildGradleGroovy(config))
                files.add(generateAppBuildGradleGroovy(config))
            }
        }

        files.add(generateAndroidManifest(config))
        files.add(generateMainActivity(config))
        files.add(generateApplication(config))
        files.add(generateAppModule(config))
        files.add(generateHomeScreen(config))
        files.add(generateHomeViewModel(config))
        files.add(generateHomeUiState(config))
        files.add(generateItemRepository(config))
        files.add(generateItemRepositoryImpl(config))
        files.add(generateItem(config))
        files.add(generateGetItemsUseCase(config))
        files.add(generateThemeKt(config))
        files.add(generateColorKt(config))
        files.add(generateTypeKt(config))
        files.add(generateStringsXml(config))
        files.add(generateColorsXml(config))
        files.add(generateThemesXml(config))
        files.add(generateGradleProperties(config))
        files.add(generateGitignore(config))
        files.add(generateProguardRules(config))

        return files
    }

    private fun generateMvvmRootBuildGradleKts(config: ProjectConfig): ProjectFile {
        val content = """
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
""".trimIndent()
        return ProjectFile("build.gradle.kts", content)
    }

    private fun generateMvvmRootBuildGradleGroovy(config: ProjectConfig): ProjectFile {
        val content = """
plugins {
    alias libs.plugins.android.application apply false
    alias libs.plugins.kotlin.android apply false
    alias libs.plugins.kotlin.compose apply false
    alias libs.plugins.hilt apply false
    alias libs.plugins.ksp apply false
}
""".trimIndent()
        return ProjectFile("build.gradle", content)
    }

    private fun generateAppBuildGradleKts(config: ProjectConfig): ProjectFile {
        val content = """
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "${config.packageName}"
    compileSdk = ${config.compileSdk}

    defaultConfig {
        applicationId = "${config.packageName}"
        minSdk = ${config.minSdk}
        targetSdk = ${config.targetSdk}
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.hilt)
    implementation(libs.androidx.navigation.compose)
    
    ksp(libs.hilt.android.compiler)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.bundles.compose.debug)
}
""".trimIndent()
        return ProjectFile("app/build.gradle.kts", content)
    }

    private fun generateAppBuildGradleGroovy(config: ProjectConfig): ProjectFile {
        val content = """
plugins {
    alias libs.plugins.android.application
    alias libs.plugins.kotlin.android
    alias libs.plugins.kotlin.compose
    alias libs.plugins.hilt
    alias libs.plugins.ksp
}

android {
    namespace '${config.packageName}'
    compileSdk ${config.compileSdk}

    defaultConfig {
        applicationId "${config.packageName}"
        minSdk ${config.minSdk}
        targetSdk ${config.targetSdk}
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = '17'
    }

    buildFeatures {
        compose true
    }
}

dependencies {
    implementation libs.androidx.core.ktx
    implementation libs.androidx.lifecycle.runtime.ktx
    implementation libs.androidx.activity.compose
    implementation platform(libs.androidx.compose.bom)
    implementation libs.bundles.compose
    implementation libs.bundles.coroutines
    implementation libs.bundles.hilt
    implementation libs.androidx.navigation.compose
    
    ksp libs.hilt.android.compiler
    
    testImplementation libs.junit
    androidTestImplementation libs.androidx.junit
    androidTestImplementation libs.androidx.espresso.core
    androidTestImplementation platform(libs.androidx.compose.bom)
    androidTestImplementation libs.androidx.ui.test.junit4
    debugImplementation libs.bundles.compose.debug
}
""".trimIndent()
        return ProjectFile("app/build.gradle", content)
    }

    private fun generateAndroidManifest(config: ProjectConfig): ProjectFile {
        val themeName = config.projectName.replace(" ", "").replace("-", "").replace("_", "")
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:name=".${themeName}Application"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.$themeName">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.$themeName">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
""".trimIndent()
        return ProjectFile("app/src/main/AndroidManifest.xml", content)
    }

    private fun generateMainActivity(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val themeName = config.projectName.replace(" ", "").replace("-", "").replace("_", "")
        val content = """
package ${config.packageName}

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ${config.packageName}.presentation.home.HomeScreen
import ${config.packageName}.ui.theme.${themeName}Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ${themeName}Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/MainActivity.kt", content)
    }

    private fun generateApplication(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val themeName = config.projectName.replace(" ", "").replace("-", "").replace("_", "")
        val content = """
package ${config.packageName}

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ${themeName}Application : Application()
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/${themeName}Application.kt", content)
    }

    private fun generateAppModule(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.di

import ${config.packageName}.data.repository.ItemRepositoryImpl
import ${config.packageName}.domain.repository.ItemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindItemRepository(
        itemRepositoryImpl: ItemRepositoryImpl
    ): ItemRepository
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/di/AppModule.kt", content)
    }

    private fun generateHomeScreen(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ${config.packageName}.domain.model.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.items) { item ->
                        ItemCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemCard(item: Item) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/presentation/home/HomeScreen.kt", content)
    }

    private fun generateHomeViewModel(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ${config.packageName}.domain.usecase.GetItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val items = getItemsUseCase()
                _uiState.update { it.copy(items = items, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun refresh() {
        loadItems()
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/presentation/home/HomeViewModel.kt", content)
    }

    private fun generateHomeUiState(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.presentation.home

import ${config.packageName}.domain.model.Item

data class HomeUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/presentation/home/HomeUiState.kt", content)
    }

    private fun generateItemRepository(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.domain.repository

import ${config.packageName}.domain.model.Item

interface ItemRepository {
    suspend fun getItems(): List<Item>
    suspend fun getItemById(id: String): Item?
    suspend fun saveItem(item: Item)
    suspend fun deleteItem(id: String)
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/domain/repository/ItemRepository.kt", content)
    }

    private fun generateItemRepositoryImpl(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.data.repository

import ${config.packageName}.domain.model.Item
import ${config.packageName}.domain.repository.ItemRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor() : ItemRepository {

    private val items = mutableListOf(
        Item("1", "First Item", "This is the first item description"),
        Item("2", "Second Item", "This is the second item description"),
        Item("3", "Third Item", "This is the third item description")
    )

    override suspend fun getItems(): List<Item> {
        delay(500)
        return items.toList()
    }

    override suspend fun getItemById(id: String): Item? {
        delay(100)
        return items.find { it.id == id }
    }

    override suspend fun saveItem(item: Item) {
        delay(100)
        val index = items.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            items[index] = item
        } else {
            items.add(item)
        }
    }

    override suspend fun deleteItem(id: String) {
        delay(100)
        items.removeAll { it.id == id }
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/data/repository/ItemRepositoryImpl.kt", content)
    }

    private fun generateItem(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.domain.model

data class Item(
    val id: String,
    val title: String,
    val description: String
)
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/domain/model/Item.kt", content)
    }

    private fun generateGetItemsUseCase(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.domain.usecase

import ${config.packageName}.domain.model.Item
import ${config.packageName}.domain.repository.ItemRepository
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(): List<Item> {
        return repository.getItems()
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/domain/usecase/GetItemsUseCase.kt", content)
    }

    private fun generateThemeKt(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val themeName = config.projectName.replace(" ", "").replace("-", "").replace("_", "")
        val content = """
package ${config.packageName}.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun ${themeName}Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/theme/Theme.kt", content)
    }

    private fun generateColorKt(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/theme/Color.kt", content)
    }

    private fun generateTypeKt(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/theme/Type.kt", content)
    }

    private fun generateStringsXml(config: ProjectConfig): ProjectFile {
        val content = """
<resources>
    <string name="app_name">${config.projectName}</string>
</resources>
""".trimIndent()
        return ProjectFile("app/src/main/res/values/strings.xml", content)
    }

    private fun generateColorsXml(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
""".trimIndent()
        return ProjectFile("app/src/main/res/values/colors.xml", content)
    }

    private fun generateThemesXml(config: ProjectConfig): ProjectFile {
        val themeName = config.projectName.replace(" ", "").replace("-", "").replace("_", "")
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.$themeName" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
""".trimIndent()
        return ProjectFile("app/src/main/res/values/themes.xml", content)
    }
}
