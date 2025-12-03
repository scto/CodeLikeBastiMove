package com.scto.codelikebastimove.core.templates.impl

import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectFile
import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectTemplate

class EmptyActivityTemplate : ProjectTemplate {
    override val name: String = "Empty Activity"
    override val description: String = "Creates a new project with an empty Activity"

    override fun generateProject(config: ProjectConfig): List<ProjectFile> {
        val files = mutableListOf<ProjectFile>()
        val packagePath = config.packageName.replace(".", "/")

        files.add(ProjectFile("app/src/main/java/$packagePath", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/layout", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/values", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/drawable", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/mipmap-hdpi", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/mipmap-mdpi", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/mipmap-xhdpi", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/mipmap-xxhdpi", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/mipmap-xxxhdpi", "", isDirectory = true))
        files.add(ProjectFile("gradle/wrapper", "", isDirectory = true))

        files.add(generateSettingsGradle(config))
        files.add(generateRootBuildGradle(config))
        files.add(generateAppBuildGradle(config))
        files.add(generateAndroidManifest(config))
        files.add(generateMainActivity(config))
        files.add(generateActivityLayout(config))
        files.add(generateStringsXml(config))
        files.add(generateColorsXml(config))
        files.add(generateThemesXml(config))
        files.add(generateGradleProperties(config))
        files.add(generateGradleWrapper(config))
        files.add(generateGradleWrapperProperties(config))
        files.add(generateLocalProperties(config))
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
    id("com.android.application") version "8.2.0" apply false
    ${if (config.language == ProjectLanguage.KOTLIN) """id("org.jetbrains.kotlin.android") version "1.9.0" apply false""" else ""}
}
""".trimIndent()
            GradleLanguage.GROOVY -> """
plugins {
    id 'com.android.application' version '8.2.0' apply false
    ${if (config.language == ProjectLanguage.KOTLIN) "id 'org.jetbrains.kotlin.android' version '1.9.0' apply false" else ""}
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
    ${if (config.language == ProjectLanguage.KOTLIN) """id("org.jetbrains.kotlin.android")""" else ""}
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
    ${if (config.language == ProjectLanguage.KOTLIN) """
    kotlinOptions {
        jvmTarget = "17"
    }""" else ""}
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    ${if (config.language == ProjectLanguage.KOTLIN) """implementation("androidx.core:core-ktx:1.12.0")""" else ""}
}
""".trimIndent()
            GradleLanguage.GROOVY -> """
plugins {
    id 'com.android.application'
    ${if (config.language == ProjectLanguage.KOTLIN) "id 'org.jetbrains.kotlin.android'" else ""}
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
    ${if (config.language == ProjectLanguage.KOTLIN) """
    kotlinOptions {
        jvmTarget = '17'
    }""" else ""}
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    ${if (config.language == ProjectLanguage.KOTLIN) "implementation 'androidx.core:core-ktx:1.12.0'" else ""}
}
""".trimIndent()
        }
        val extension = config.gradleLanguage.extension
        return ProjectFile("app/build.$extension", content)
    }

    private fun generateAndroidManifest(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.${config.projectName.replace(" ", "")}">
        <activity
            android:name=".MainActivity"
            android:exported="true">
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
        val content = when (config.language) {
            ProjectLanguage.KOTLIN -> """
package ${config.packageName}

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
""".trimIndent()
            ProjectLanguage.JAVA -> """
package ${config.packageName};

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
""".trimIndent()
        }
        val extension = config.language.extension
        return ProjectFile("app/src/main/java/$packagePath/MainActivity.$extension", content)
    }

    private fun generateActivityLayout(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
""".trimIndent()
        return ProjectFile("app/src/main/res/layout/activity_main.xml", content)
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
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.$themeName" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
    </style>
</resources>
""".trimIndent()
        return ProjectFile("app/src/main/res/values/themes.xml", content)
    }

    private fun generateGradleProperties(config: ProjectConfig): ProjectFile {
        val content = """
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
${if (config.language == ProjectLanguage.KOTLIN) "kotlin.code.style=official" else ""}
android.nonTransitiveRClass=true
""".trimIndent()
        return ProjectFile("gradle.properties", content)
    }

    private fun generateGradleWrapper(config: ProjectConfig): ProjectFile {
        val content = """
#!/usr/bin/env sh
exec gradle "$@"
""".trimIndent()
        return ProjectFile("gradlew", content)
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

    private fun generateLocalProperties(config: ProjectConfig): ProjectFile {
        val content = """
sdk.dir=/path/to/android/sdk
""".trimIndent()
        return ProjectFile("local.properties", content)
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
