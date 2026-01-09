package com.scto.codelikebastimove.core.templates.impl

import com.scto.codelikebastimove.core.datastore.ProjectTemplateType
import com.scto.codelikebastimove.core.datastore.VersionCatalog
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectFile

class EmptyComposeActivityTemplate : BaseVersionCatalogTemplate() {
    override val name: String = "Empty Compose Activity"
    override val description: String = "Creates a new project with Jetpack Compose"
    override val templateId: String = "empty-compose-activity"
    override val templateVersion: String = "1.0.0"
    override val templateType: ProjectTemplateType = ProjectTemplateType.EMPTY_COMPOSE
    override val features: List<String> = listOf("Jetpack Compose", "Material 3", "Dynamic Colors")

    override fun getVersionCatalog(): VersionCatalog = createBaseComposeVersionCatalog()

    override fun generateProject(config: ProjectConfig): List<ProjectFile> {
        val files = mutableListOf<ProjectFile>()
        val packagePath = config.packageName.replace(".", "/")

        files.add(ProjectFile("app/src/main/java/$packagePath", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/ui/theme", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/values", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/drawable", "", isDirectory = true))

        files.addAll(generateGradleWrapper(config))
        files.add(generateVersionCatalogToml(config))

        when (config.gradleLanguage) {
            GradleLanguage.KOTLIN_DSL -> {
                files.add(generateSettingsGradleKts(config))
                files.add(generateRootBuildGradleKts(config))
                files.add(generateAppBuildGradleKts(config))
            }
            GradleLanguage.GROOVY -> {
                files.add(generateSettingsGradleGroovy(config))
                files.add(generateRootBuildGradleGroovy(config))
                files.add(generateAppBuildGradleGroovy(config))
            }
        }

        files.add(generateAndroidManifest(config))
        files.add(generateMainActivity(config))
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

    private fun generateAppBuildGradleKts(config: ProjectConfig): ProjectFile {
        val content = """
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ${config.packageName}.ui.theme.${themeName}Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ${themeName}Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello ${'$'}name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ${themeName}Theme {
        Greeting("Android")
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/MainActivity.kt", content)
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
