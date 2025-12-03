package com.scto.codelikebastimove.core.templates.impl

import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectFile
import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectTemplate

class EmptyComposeActivityTemplate : ProjectTemplate {
    override val name: String = "Empty Compose Activity"
    override val description: String = "Creates a new project with Jetpack Compose"

    override fun generateProject(config: ProjectConfig): List<ProjectFile> {
        val files = mutableListOf<ProjectFile>()
        val packagePath = config.packageName.replace(".", "/")

        files.add(ProjectFile("app/src/main/java/$packagePath", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/ui/theme", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/values", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/drawable", "", isDirectory = true))
        files.add(ProjectFile("gradle/wrapper", "", isDirectory = true))

        files.add(generateSettingsGradle(config))
        files.add(generateRootBuildGradle(config))
        files.add(generateAppBuildGradle(config))
        files.add(generateAndroidManifest(config))
        files.add(generateMainActivity(config))
        files.add(generateThemeKt(config))
        files.add(generateColorKt(config))
        files.add(generateTypeKt(config))
        files.add(generateStringsXml(config))
        files.add(generateColorsXml(config))
        files.add(generateThemesXml(config))
        files.add(generateGradleProperties(config))
        files.add(generateGradleWrapperProperties(config))
        files.add(generateGitignore(config))

        return files
    }

    private fun generateSettingsGradle(config: ProjectConfig): ProjectFile {
        val content = when (config.gradleLanguage) {
            GradleLanguage.KOTLIN_DSL -> """
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "${config.projectName}"
include(":app")
""".trimIndent()
            GradleLanguage.GROOVY -> """
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "${config.projectName}"
include ':app'
""".trimIndent()
        }
        val extension = config.gradleLanguage.extension
        return ProjectFile("settings.$extension", content)
    }

    private fun generateRootBuildGradle(config: ProjectConfig): ProjectFile {
        val content = when (config.gradleLanguage) {
            GradleLanguage.KOTLIN_DSL -> """
plugins {
    id("com.android.application") apply false version "8.2.0"
    id("org.jetbrains.kotlin.android") apply false version "2.0.0"
    id("org.jetbrains.kotlin.plugin.compose") apply false version "2.0.0"
}
""".trimIndent()
            GradleLanguage.GROOVY -> """
plugins {
    id 'com.android.application' version '8.2.0' apply false
    id 'org.jetbrains.kotlin.android' version '2.0.0' apply false
    id 'org.jetbrains.kotlin.plugin.compose' version '2.0.0' apply false
}
""".trimIndent()
        }
        val extension = config.gradleLanguage.extension
        return ProjectFile("build.$extension", content)
    }

    private fun generateAppBuildGradle(config: ProjectConfig): ProjectFile {
        val content = when (config.gradleLanguage) {
            GradleLanguage.KOTLIN_DSL -> """
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
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
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
}
""".trimIndent()
            GradleLanguage.GROOVY -> """
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.plugin.compose'
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
    def composeBom = platform('androidx.compose:compose-bom:2024.06.00')
    implementation composeBom
    
    implementation 'androidx.core:core-ktx:1.13.1'
    implementation 'androidx.activity:activity-compose:1.9.0'
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    
    debugImplementation 'androidx.compose.ui:ui-tooling'
}
""".trimIndent()
        }
        val extension = config.gradleLanguage.extension
        return ProjectFile("app/build.$extension", content)
    }

    private fun generateAndroidManifest(config: ProjectConfig): ProjectFile {
        val themeName = config.projectName.replace(" ", "")
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
        val themeName = config.projectName.replace(" ", "")
        val content = """
package ${config.packageName}

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ${config.packageName}.ui.theme.${themeName}Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ${themeName}Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
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
        val themeName = config.projectName.replace(" ", "")
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
        val themeName = config.projectName.replace(" ", "")
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.$themeName" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
""".trimIndent()
        return ProjectFile("app/src/main/res/values/themes.xml", content)
    }

    private fun generateGradleProperties(config: ProjectConfig): ProjectFile {
        val content = """
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
""".trimIndent()
        return ProjectFile("gradle.properties", content)
    }

    private fun generateGradleWrapperProperties(config: ProjectConfig): ProjectFile {
        val content = """
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
""".trimIndent()
        return ProjectFile("gradle/wrapper/gradle-wrapper.properties", content)
    }

    private fun generateGitignore(config: ProjectConfig): ProjectFile {
        val content = """
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
""".trimIndent()
        return ProjectFile(".gitignore", content)
    }
}
