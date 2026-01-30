package com.scto.codelikebastimove.core.templates.impl

import com.scto.codelikebastimove.core.datastore.ProjectTemplateType
import com.scto.codelikebastimove.core.datastore.VersionCatalog
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectFile

class EmptyActivityTemplate : BaseVersionCatalogTemplate() {
  override val name: String = "Empty Activity"
  override val description: String = "Creates a new project with an empty Activity using Views"
  override val templateId: String = "empty-activity"
  override val templateVersion: String = "1.0.0"
  override val templateType: ProjectTemplateType = ProjectTemplateType.EMPTY_ACTIVITY
  override val features: List<String> =
    listOf("View Binding", "Material Design 3", "ConstraintLayout")

  override fun getVersionCatalog(): VersionCatalog = createBaseViewVersionCatalog()

  override fun generateProject(config: ProjectConfig): List<ProjectFile> {
    val files = mutableListOf<ProjectFile>()
    val packagePath = config.packageName.replace(".", "/")

    files.add(ProjectFile("app/src/main/java/$packagePath", "", isDirectory = true))
    files.add(ProjectFile("app/src/main/res/values", "", isDirectory = true))
    files.add(ProjectFile("app/src/main/res/layout", "", isDirectory = true))
    files.add(ProjectFile("app/src/main/res/drawable", "", isDirectory = true))

    files.addAll(generateGradleWrapper(config))
    files.add(generateVersionCatalogToml(config))

    when (config.gradleLanguage) {
      GradleLanguage.KOTLIN_DSL -> {
        files.add(generateSettingsGradleKts(config))
        files.add(generateRootBuildGradleKtsView(config))
        files.add(generateAppBuildGradleKts(config))
      }
      GradleLanguage.GROOVY -> {
        files.add(generateSettingsGradleGroovy(config))
        files.add(generateRootBuildGradleGroovyView(config))
        files.add(generateAppBuildGradleGroovy(config))
      }
    }

    files.add(generateAndroidManifest(config))
    files.add(generateMainActivity(config))
    files.add(generateActivityMainXml(config))
    files.add(generateStringsXml(config))
    files.add(generateColorsXml(config))
    files.add(generateThemesXml(config))
    files.add(generateGradleProperties(config))
    files.add(generateGitignore(config))
    files.add(generateProguardRules(config))

    return files
  }

  private fun generateRootBuildGradleKtsView(config: ProjectConfig): ProjectFile {
    val content =
      """
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
"""
        .trimIndent()
    return ProjectFile("build.gradle.kts", content)
  }

  private fun generateRootBuildGradleGroovyView(config: ProjectConfig): ProjectFile {
    val content =
      """
plugins {
    alias libs.plugins.android.application apply false
    alias libs.plugins.kotlin.android apply false
}
"""
        .trimIndent()
    return ProjectFile("build.gradle", content)
  }

  private fun generateAppBuildGradleKts(config: ProjectConfig): ProjectFile {
    val content =
      """
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.bundles.android.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
"""
        .trimIndent()
    return ProjectFile("app/build.gradle.kts", content)
  }

  private fun generateAppBuildGradleGroovy(config: ProjectConfig): ProjectFile {
    val content =
      """
plugins {
    alias libs.plugins.android.application
    alias libs.plugins.kotlin.android
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
        viewBinding true
    }
}

dependencies {
    implementation libs.bundles.android.core
    implementation libs.androidx.lifecycle.runtime.ktx
    
    testImplementation libs.junit
    androidTestImplementation libs.androidx.junit
    androidTestImplementation libs.androidx.espresso.core
}
"""
        .trimIndent()
    return ProjectFile("app/build.gradle", content)
  }

  private fun generateAndroidManifest(config: ProjectConfig): ProjectFile {
    val themeName = config.projectName.replace(" ", "").replace("-", "").replace("_", "")
    val content =
      """
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
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
"""
        .trimIndent()
    return ProjectFile("app/src/main/AndroidManifest.xml", content)
  }

  private fun generateMainActivity(config: ProjectConfig): ProjectFile {
    val packagePath = config.packageName.replace(".", "/")
    val content =
      """
package ${config.packageName}

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ${config.packageName}.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
"""
        .trimIndent()
    return ProjectFile("app/src/main/java/$packagePath/MainActivity.kt", content)
  }

  private fun generateActivityMainXml(config: ProjectConfig): ProjectFile {
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
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
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/layout/activity_main.xml", content)
  }

  private fun generateStringsXml(config: ProjectConfig): ProjectFile {
    val content =
      """
<resources>
    <string name="app_name">${config.projectName}</string>
</resources>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/values/strings.xml", content)
  }

  private fun generateColorsXml(config: ProjectConfig): ProjectFile {
    val content =
      """
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
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/values/colors.xml", content)
  }

  private fun generateThemesXml(config: ProjectConfig): ProjectFile {
    val themeName = config.projectName.replace(" ", "").replace("-", "").replace("_", "")
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.$themeName" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
    </style>
</resources>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/values/themes.xml", content)
  }
}
