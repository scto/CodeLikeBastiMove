package com.scto.codelikebastimove.core.templates.impl

import com.scto.codelikebastimove.core.datastore.ProjectTemplateType
import com.scto.codelikebastimove.core.datastore.VersionCatalog
import com.scto.codelikebastimove.core.datastore.VersionCatalogEntry
import com.scto.codelikebastimove.core.datastore.VersionCatalogLibrary
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectFile

class TabbedActivityTemplate : BaseVersionCatalogTemplate() {
    override val name: String = "Tabbed Activity"
    override val description: String = "Creates a project with ViewPager2 and TabLayout"
    override val templateId: String = "tabbed-activity"
    override val templateVersion: String = "1.0.0"
    override val templateType: ProjectTemplateType = ProjectTemplateType.TABBED
    override val features: List<String> = listOf("ViewPager2", "TabLayout", "ViewBinding", "LiveData", "ViewModel")

    override fun getVersionCatalog(): VersionCatalog {
        val base = createBaseViewVersionCatalog()
        return base.copy(
            versions = base.versions + listOf(
                VersionCatalogEntry("viewpager2", "1.1.0")
            ),
            libraries = base.libraries + listOf(
                VersionCatalogLibrary("androidx-viewpager2", "androidx.viewpager2", "viewpager2", "viewpager2")
            )
        )
    }

    override fun generateProject(config: ProjectConfig): List<ProjectFile> {
        val files = mutableListOf<ProjectFile>()
        val packagePath = config.packageName.replace(".", "/")

        files.add(ProjectFile("app/src/main/java/$packagePath/ui/main", "", isDirectory = true))
        files.add(ProjectFile("app/src/main/res/layout", "", isDirectory = true))
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
        files.add(generateSectionsPagerAdapter(config))
        files.add(generatePlaceholderFragment(config))
        files.add(generatePageViewModel(config))
        files.add(generateFragmentMainLayout(config))
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
        val content = """
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
""".trimIndent()
        return ProjectFile("build.gradle.kts", content)
    }

    private fun generateRootBuildGradleGroovyView(config: ProjectConfig): ProjectFile {
        val content = """
plugins {
    alias libs.plugins.android.application apply false
    alias libs.plugins.kotlin.android apply false
}
""".trimIndent()
        return ProjectFile("build.gradle", content)
    }

    private fun generateAppBuildGradleKts(config: ProjectConfig): ProjectFile {
        val content = """
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
    implementation(libs.androidx.viewpager2)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
""".trimIndent()
        return ProjectFile("app/build.gradle.kts", content)
    }

    private fun generateAppBuildGradleGroovy(config: ProjectConfig): ProjectFile {
        val content = """
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
    implementation libs.androidx.viewpager2
    
    testImplementation libs.junit
    androidTestImplementation libs.androidx.junit
    androidTestImplementation libs.androidx.espresso.core
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
            android:label="@string/app_name"
            android:theme="@style/Theme.$themeName.NoActionBar">
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.appcompat.app.AppCompatActivity
import ${config.packageName}.ui.main.SectionsPagerAdapter
import ${config.packageName}.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = "TAB ${'$'}{position + 1}"
        }.attach()
        
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/MainActivity.kt", content)
    }

    private fun generateActivityMainLayout(config: ProjectConfig): ProjectFile {
        val themeName = config.projectName.replace(" ", "").replace("-", "").replace("_", "")
        val content = """
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

        <TextView
            android:id="@+id/title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:minHeight="?actionBarSize"
            android:padding="@dimen/appbar_padding"
            android:text="@string/app_name"
            android:textAppearance="@style/TextAppearance.Widget.AppCompat.Toolbar.Title" />

        <com.google.android.material.tabs.TabLayout
            android:id="@+id/tabs"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="?attr/colorPrimary" />
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/view_pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior" />

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_marginEnd="@dimen/fab_margin"
        android:layout_marginBottom="16dp"
        app:srcCompat="@android:drawable/ic_dialog_email" />
</androidx.coordinatorlayout.widget.CoordinatorLayout>
""".trimIndent()
        return ProjectFile("app/src/main/res/layout/activity_main.xml", content)
    }

    private fun generateSectionsPagerAdapter(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val TAB_COUNT = 3

class SectionsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        return PlaceholderFragment.newInstance(position + 1)
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/main/SectionsPagerAdapter.kt", content)
    }

    private fun generatePlaceholderFragment(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ${config.packageName}.databinding.FragmentMainBinding

class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[PageViewModel::class.java].apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        val textView: TextView = binding.sectionLabel
        pageViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/main/PlaceholderFragment.kt", content)
    }

    private fun generatePageViewModel(config: ProjectConfig): ProjectFile {
        val packagePath = config.packageName.replace(".", "/")
        val content = """
package ${config.packageName}.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class PageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = _index.map {
        "Hello world from section: ${'$'}it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }
}
""".trimIndent()
        return ProjectFile("app/src/main/java/$packagePath/ui/main/PageViewModel.kt", content)
    }

    private fun generateFragmentMainLayout(config: ProjectConfig): ProjectFile {
        val content = """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/constraintLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.main.PlaceholderFragment">

    <TextView
        android:id="@+id/section_label"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="@dimen/activity_horizontal_margin"
        android:layout_marginTop="@dimen/activity_vertical_margin"
        android:layout_marginEnd="@dimen/activity_horizontal_margin"
        android:layout_marginBottom="@dimen/activity_vertical_margin"
        android:textSize="24sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
""".trimIndent()
        return ProjectFile("app/src/main/res/layout/fragment_main.xml", content)
    }

    private fun generateStringsXml(config: ProjectConfig): ProjectFile {
        val content = """
<resources>
    <string name="app_name">${config.projectName}</string>
    <string name="tab_text_1">Tab 1</string>
    <string name="tab_text_2">Tab 2</string>
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
""".trimIndent()
        return ProjectFile("app/src/main/res/values/themes.xml", content)
    }

    private fun generateDimensXml(config: ProjectConfig): ProjectFile {
        val content = """
<resources>
    <dimen name="fab_margin">16dp</dimen>
    <dimen name="activity_horizontal_margin">16dp</dimen>
    <dimen name="activity_vertical_margin">16dp</dimen>
    <dimen name="appbar_padding">16dp</dimen>
</resources>
""".trimIndent()
        return ProjectFile("app/src/main/res/values/dimens.xml", content)
    }
}
