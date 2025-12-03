package com.scto.codelikebastimove.core.templates.impl

import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectFile
import com.scto.codelikebastimove.core.templates.api.ProjectTemplate

class BottomNavigationTemplate : ProjectTemplate {
    override val name: String = "Bottom Navigation Activity"
    override val description: String = "Creates a project with Bottom Navigation and Fragments"

    override fun generateProject(config: ProjectConfig): List<ProjectFile> {
        val files = mutableListOf<ProjectFile>()
        val packagePath = config.packageName.replace(".", "/")

        files.add(ProjectFile("app/src/main/java/$packagePath/ui/home", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/ui/dashboard", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/java/$packagePath/ui/notifications", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/layout", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/navigation", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/menu", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/values", "", isDirectory = true))
        files.add(ProjectFile("gradle/wrapper", "", isDirectory = true))

        files.add(generateSettingsGradle(config))
        files.add(generateRootBuildGradle(config))
        files.add(generateAppBuildGradle(config))
        files.add(generateAndroidManifest(config))
        files.add(generateMainActivity(config))
        files.add(generateActivityMainLayout(config))
        files.add(generateHomeFragment(config))
        files.add(generateHomeViewModel(config))
        files.add(generateFragmentHomeLayout(config))
        files.add(generateDashboardFragment(config))
        files.add(generateDashboardViewModel(config))
        files.add(generateFragmentDashboardLayout(config))
        files.add(generateNotificationsFragment(config))
        files.add(generateNotificationsViewModel(config))
        files.add(generateFragmentNotificationsLayout(config))
        files.add(generateBottomNavMenu(config))
        files.add(generateMobileNavigation(config))
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

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
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
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "${config.projectName}"
include ':app'
""".trimIndent()
        }
        return ProjectFile("settings.${config.gradleLanguage.extension}", content)
    }

    private fun generateRootBuildGradle(config: ProjectConfig): ProjectFile {
        val content = when (config.gradleLanguage) {
            GradleLanguage.KOTLIN_DSL -> """
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
""".trimIndent()
            GradleLanguage.GROOVY -> """
plugins {
    id 'com.android.application' version '8.2.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.0' apply false
}
""".trimIndent()
        }
        return ProjectFile("build.${config.gradleLanguage.extension}", content)
    }

    private fun generateAppBuildGradle(config: ProjectConfig): ProjectFile {
        val content = when (config.gradleLanguage) {
            GradleLanguage.KOTLIN_DSL -> """
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
}
""".trimIndent()
            GradleLanguage.GROOVY -> """
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
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
        viewBinding true
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.7'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.7'
}
""".trimIndent()
        }
        return ProjectFile("app/build.${config.gradleLanguage.extension}", content)
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
            android:label="@string/app_name">
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
        val content = """
package ${config.packageName}

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ${config.packageName}.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/MainActivity.kt", content)
    }

    private fun generateActivityMainLayout(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingTop="?attr/actionBarSize">

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/nav_view"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="0dp"
        android:layout_marginEnd="0dp"
        android:background="?android:attr/windowBackground"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:menu="@menu/bottom_nav_menu" />

    <fragment
        android:id="@+id/nav_host_fragment_activity_main"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:defaultNavHost="true"
        app:layout_constraintBottom_toTopOf="@id/nav_view"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/mobile_navigation" />

</androidx.constraintlayout.widget.ConstraintLayout>
""".trimIndent()
        return ProjectFile("app/src/main/res/layout/activity_main.xml", content)
    }

    private fun generateHomeFragment(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ${config.packageName}.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/home/HomeFragment.kt", content)
    }

    private fun generateHomeViewModel(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/home/HomeViewModel.kt", content)
    }

    private fun generateFragmentHomeLayout(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.home.HomeFragment">

    <TextView
        android:id="@+id/text_home"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="8dp"
        android:textAlignment="center"
        android:textSize="20sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
""".trimIndent()
        return ProjectFile("app/src/main/res/layout/fragment_home.xml", content)
    }

    private fun generateDashboardFragment(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ${config.packageName}.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/dashboard/DashboardFragment.kt", content)
    }

    private fun generateDashboardViewModel(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/dashboard/DashboardViewModel.kt", content)
    }

    private fun generateFragmentDashboardLayout(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.dashboard.DashboardFragment">

    <TextView
        android:id="@+id/text_dashboard"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="8dp"
        android:textAlignment="center"
        android:textSize="20sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
""".trimIndent()
        return ProjectFile("app/src/main/res/layout/fragment_dashboard.xml", content)
    }

    private fun generateNotificationsFragment(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ${config.packageName}.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/notifications/NotificationsFragment.kt", content)
    }

    private fun generateNotificationsViewModel(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/notifications/NotificationsViewModel.kt", content)
    }

    private fun generateFragmentNotificationsLayout(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.notifications.NotificationsFragment">

    <TextView
        android:id="@+id/text_notifications"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="8dp"
        android:textAlignment="center"
        android:textSize="20sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
""".trimIndent()
        return ProjectFile("app/src/main/res/layout/fragment_notifications.xml", content)
    }

    private fun generateBottomNavMenu(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/navigation_home"
        android:icon="@drawable/ic_home_black_24dp"
        android:title="@string/title_home" />

    <item
        android:id="@+id/navigation_dashboard"
        android:icon="@drawable/ic_dashboard_black_24dp"
        android:title="@string/title_dashboard" />

    <item
        android:id="@+id/navigation_notifications"
        android:icon="@drawable/ic_notifications_black_24dp"
        android:title="@string/title_notifications" />

</menu>
""".trimIndent()
        return ProjectFile("app/src/main/res/menu/bottom_nav_menu.xml", content)
    }

    private fun generateMobileNavigation(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/mobile_navigation"
    app:startDestination="@+id/navigation_home">

    <fragment
        android:id="@+id/navigation_home"
        android:name="${config.packageName}.ui.home.HomeFragment"
        android:label="@string/title_home"
        tools:layout="@layout/fragment_home" />

    <fragment
        android:id="@+id/navigation_dashboard"
        android:name="${config.packageName}.ui.dashboard.DashboardFragment"
        android:label="@string/title_dashboard"
        tools:layout="@layout/fragment_dashboard" />

    <fragment
        android:id="@+id/navigation_notifications"
        android:name="${config.packageName}.ui.notifications.NotificationsFragment"
        android:label="@string/title_notifications"
        tools:layout="@layout/fragment_notifications" />
</navigation>
""".trimIndent()
        return ProjectFile("app/src/main/res/navigation/mobile_navigation.xml", content)
    }

    private fun generateStringsXml(config: ProjectConfig): ProjectFile {
        val content = """
<resources>
    <string name="app_name">${config.projectName}</string>
    <string name="title_home">Home</string>
    <string name="title_dashboard">Dashboard</string>
    <string name="title_notifications">Notifications</string>
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
