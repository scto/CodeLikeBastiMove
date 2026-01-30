package com.scto.codelikebastimove.core.templates.impl

import com.scto.codelikebastimove.core.datastore.ProjectTemplateType
import com.scto.codelikebastimove.core.datastore.VersionCatalog
import com.scto.codelikebastimove.core.datastore.VersionCatalogBundle
import com.scto.codelikebastimove.core.datastore.VersionCatalogEntry
import com.scto.codelikebastimove.core.datastore.VersionCatalogLibrary
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectFile

class NavigationDrawerTemplate : BaseVersionCatalogTemplate() {
  override val name: String = "Navigation Drawer Activity"
  override val description: String = "Creates a project with a Navigation Drawer"
  override val templateId: String = "navigation-drawer"
  override val templateVersion: String = "1.0.0"
  override val templateType: ProjectTemplateType = ProjectTemplateType.NAVIGATION_DRAWER
  override val features: List<String> =
    listOf(
      "Navigation Drawer",
      "Navigation Component",
      "ViewBinding",
      "LiveData",
      "ViewModel",
      "FAB",
    )

  override fun getVersionCatalog(): VersionCatalog {
    val base = createBaseViewVersionCatalog()
    return base.copy(
      versions =
        base.versions +
          listOf(
            VersionCatalogEntry("navigation", "2.8.5"),
            VersionCatalogEntry("drawerlayout", "1.2.0"),
          ),
      libraries =
        base.libraries +
          listOf(
            VersionCatalogLibrary(
              "androidx-navigation-fragment-ktx",
              "androidx.navigation",
              "navigation-fragment-ktx",
              "navigation",
            ),
            VersionCatalogLibrary(
              "androidx-navigation-ui-ktx",
              "androidx.navigation",
              "navigation-ui-ktx",
              "navigation",
            ),
            VersionCatalogLibrary(
              "androidx-drawerlayout",
              "androidx.drawerlayout",
              "drawerlayout",
              "drawerlayout",
            ),
          ),
      bundles =
        base.bundles +
          listOf(
            VersionCatalogBundle(
              "navigation",
              listOf("androidx-navigation-fragment-ktx", "androidx-navigation-ui-ktx"),
            )
          ),
    )
  }

  override fun generateProject(config: ProjectConfig): List<ProjectFile> {
    val files = mutableListOf<ProjectFile>()
    val packagePath = config.packageName.replace(".", "/")

    files.add(ProjectFile("app/src/main/java/$packagePath/ui/home", "", isDirectory = true))
    files.add(ProjectFile("app/src/main/java/$packagePath/ui/gallery", "", isDirectory = true))
    files.add(ProjectFile("app/src/main/java/$packagePath/ui/slideshow", "", isDirectory = true))
    files.add(ProjectFile("app/src/main/res/layout", "", isDirectory = true))
    files.add(ProjectFile("app/src/main/res/navigation", "", isDirectory = true))
    files.add(ProjectFile("app/src/main/res/menu", "", isDirectory = true))
    files.add(ProjectFile("app/src/main/res/values", "", isDirectory = true))
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
    files.add(generateActivityMainLayout(config))
    files.add(generateAppBarMain(config))
    files.add(generateContentMain(config))
    files.add(generateNavHeader(config))
    files.add(generateHomeFragment(config))
    files.add(generateHomeViewModel(config))
    files.add(generateFragmentHomeLayout(config))
    files.add(generateGalleryFragment(config))
    files.add(generateGalleryViewModel(config))
    files.add(generateFragmentGalleryLayout(config))
    files.add(generateSlideshowFragment(config))
    files.add(generateSlideshowViewModel(config))
    files.add(generateFragmentSlideshowLayout(config))
    files.add(generateDrawerMenu(config))
    files.add(generateMainMenu(config))
    files.add(generateMobileNavigation(config))
    files.add(generateStringsXml(config))
    files.add(generateColorsXml(config))
    files.add(generateThemesXml(config))
    files.add(generateDimensXml(config))
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
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.navigation)
    implementation(libs.androidx.drawerlayout)
    
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
    implementation libs.bundles.lifecycle
    implementation libs.bundles.navigation
    implementation libs.androidx.drawerlayout
    
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
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.$themeName.NoActionBar">
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
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import ${config.packageName}.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
"""
        .trimIndent()
    return ProjectFile("app/src/main/java/$packagePath/MainActivity.kt", content)
  }

  private fun generateActivityMainLayout(config: ProjectConfig): ProjectFile {
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:openDrawer="start">

    <include
        android:id="@+id/app_bar_main"
        layout="@layout/app_bar_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:fitsSystemWindows="true"
        app:headerLayout="@layout/nav_header_main"
        app:menu="@menu/activity_main_drawer" />

</androidx.drawerlayout.widget.DrawerLayout>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/layout/activity_main.xml", content)
  }

  private fun generateAppBarMain(config: ProjectConfig): ProjectFile {
    val themeName = config.projectName.replace(" ", "").replace("-", "").replace("_", "")
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/Theme.$themeName.AppBarOverlay">

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/Theme.$themeName.PopupOverlay" />

    </com.google.android.material.appbar.AppBarLayout>

    <include layout="@layout/content_main" />

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_marginEnd="@dimen/fab_margin"
        android:layout_marginBottom="16dp"
        app:srcCompat="@android:drawable/ic_dialog_email" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/layout/app_bar_main.xml", content)
  }

  private fun generateContentMain(config: ProjectConfig): ProjectFile {
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:showIn="@layout/app_bar_main">

    <fragment
        android:id="@+id/nav_host_fragment_content_main"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:defaultNavHost="true"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/mobile_navigation" />
</androidx.constraintlayout.widget.ConstraintLayout>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/layout/content_main.xml", content)
  }

  private fun generateNavHeader(config: ProjectConfig): ProjectFile {
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="@dimen/nav_header_height"
    android:background="@color/purple_500"
    android:gravity="bottom"
    android:orientation="vertical"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:theme="@style/ThemeOverlay.AppCompat.Dark">

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:contentDescription="@string/nav_header_desc"
        android:paddingTop="@dimen/nav_header_vertical_spacing"
        app:srcCompat="@mipmap/ic_launcher_round" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingTop="@dimen/nav_header_vertical_spacing"
        android:text="@string/nav_header_title"
        android:textAppearance="@style/TextAppearance.AppCompat.Body1" />

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/nav_header_subtitle" />

</LinearLayout>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/layout/nav_header_main.xml", content)
  }

  private fun generateHomeFragment(config: ProjectConfig): ProjectFile {
    val packagePath = config.packageName.replace(".", "/")
    val content =
      """
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
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

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
"""
        .trimIndent()
    return ProjectFile("app/src/main/java/$packagePath/ui/home/HomeFragment.kt", content)
  }

  private fun generateHomeViewModel(config: ProjectConfig): ProjectFile {
    val packagePath = config.packageName.replace(".", "/")
    val content =
      """
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
"""
        .trimIndent()
    return ProjectFile("app/src/main/java/$packagePath/ui/home/HomeViewModel.kt", content)
  }

  private fun generateFragmentHomeLayout(config: ProjectConfig): ProjectFile {
    val content =
      """
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
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/layout/fragment_home.xml", content)
  }

  private fun generateGalleryFragment(config: ProjectConfig): ProjectFile {
    val packagePath = config.packageName.replace(".", "/")
    val content =
      """
package ${config.packageName}.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ${config.packageName}.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel = ViewModelProvider(this)[GalleryViewModel::class.java]

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
"""
        .trimIndent()
    return ProjectFile("app/src/main/java/$packagePath/ui/gallery/GalleryFragment.kt", content)
  }

  private fun generateGalleryViewModel(config: ProjectConfig): ProjectFile {
    val packagePath = config.packageName.replace(".", "/")
    val content =
      """
package ${config.packageName}.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text
}
"""
        .trimIndent()
    return ProjectFile("app/src/main/java/$packagePath/ui/gallery/GalleryViewModel.kt", content)
  }

  private fun generateFragmentGalleryLayout(config: ProjectConfig): ProjectFile {
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.gallery.GalleryFragment">

    <TextView
        android:id="@+id/text_gallery"
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
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/layout/fragment_gallery.xml", content)
  }

  private fun generateSlideshowFragment(config: ProjectConfig): ProjectFile {
    val packagePath = config.packageName.replace(".", "/")
    val content =
      """
package ${config.packageName}.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ${config.packageName}.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel = ViewModelProvider(this)[SlideshowViewModel::class.java]

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
"""
        .trimIndent()
    return ProjectFile("app/src/main/java/$packagePath/ui/slideshow/SlideshowFragment.kt", content)
  }

  private fun generateSlideshowViewModel(config: ProjectConfig): ProjectFile {
    val packagePath = config.packageName.replace(".", "/")
    val content =
      """
package ${config.packageName}.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
}
"""
        .trimIndent()
    return ProjectFile("app/src/main/java/$packagePath/ui/slideshow/SlideshowViewModel.kt", content)
  }

  private fun generateFragmentSlideshowLayout(config: ProjectConfig): ProjectFile {
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.slideshow.SlideshowFragment">

    <TextView
        android:id="@+id/text_slideshow"
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
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/layout/fragment_slideshow.xml", content)
  }

  private fun generateDrawerMenu(config: ProjectConfig): ProjectFile {
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    tools:showIn="navigation_view">

    <group android:checkableBehavior="single">
        <item
            android:id="@+id/nav_home"
            android:icon="@android:drawable/ic_menu_today"
            android:title="@string/menu_home" />
        <item
            android:id="@+id/nav_gallery"
            android:icon="@android:drawable/ic_menu_gallery"
            android:title="@string/menu_gallery" />
        <item
            android:id="@+id/nav_slideshow"
            android:icon="@android:drawable/ic_menu_slideshow"
            android:title="@string/menu_slideshow" />
    </group>
</menu>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/menu/activity_main_drawer.xml", content)
  }

  private fun generateMainMenu(config: ProjectConfig): ProjectFile {
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_settings"
        android:orderInCategory="100"
        android:title="@string/action_settings"
        app:showAsAction="never" />
</menu>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/menu/main.xml", content)
  }

  private fun generateMobileNavigation(config: ProjectConfig): ProjectFile {
    val content =
      """
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/mobile_navigation"
    app:startDestination="@+id/nav_home">

    <fragment
        android:id="@+id/nav_home"
        android:name="${config.packageName}.ui.home.HomeFragment"
        android:label="@string/menu_home"
        tools:layout="@layout/fragment_home" />

    <fragment
        android:id="@+id/nav_gallery"
        android:name="${config.packageName}.ui.gallery.GalleryFragment"
        android:label="@string/menu_gallery"
        tools:layout="@layout/fragment_gallery" />

    <fragment
        android:id="@+id/nav_slideshow"
        android:name="${config.packageName}.ui.slideshow.SlideshowFragment"
        android:label="@string/menu_slideshow"
        tools:layout="@layout/fragment_slideshow" />
</navigation>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/navigation/mobile_navigation.xml", content)
  }

  private fun generateStringsXml(config: ProjectConfig): ProjectFile {
    val content =
      """
<resources>
    <string name="app_name">${config.projectName}</string>
    <string name="menu_home">Home</string>
    <string name="menu_gallery">Gallery</string>
    <string name="menu_slideshow">Slideshow</string>
    <string name="nav_header_title">Android Studio</string>
    <string name="nav_header_subtitle">android.studio@android.com</string>
    <string name="nav_header_desc">Navigation header</string>
    <string name="action_settings">Settings</string>
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
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.$themeName" parent="Theme.Material3.DayNight.DarkActionBar">
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
    </style>

    <style name="Theme.$themeName.NoActionBar">
        <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
    </style>

    <style name="Theme.$themeName.AppBarOverlay" parent="ThemeOverlay.AppCompat.Dark.ActionBar" />

    <style name="Theme.$themeName.PopupOverlay" parent="ThemeOverlay.AppCompat.Light" />
</resources>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/values/themes.xml", content)
  }

  private fun generateDimensXml(config: ProjectConfig): ProjectFile {
    val content =
      """
<resources>
    <dimen name="fab_margin">16dp</dimen>
    <dimen name="activity_horizontal_margin">16dp</dimen>
    <dimen name="activity_vertical_margin">16dp</dimen>
    <dimen name="nav_header_height">176dp</dimen>
    <dimen name="nav_header_vertical_spacing">8dp</dimen>
</resources>
"""
        .trimIndent()
    return ProjectFile("app/src/main/res/values/dimens.xml", content)
  }
}
