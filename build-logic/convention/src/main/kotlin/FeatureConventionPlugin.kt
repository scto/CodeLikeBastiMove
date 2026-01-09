import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

import com.scto.convention.libs

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("codelikebastimove.android.library")
                apply("codelikebastimove.android.library.compose")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            dependencies {
                add("api", project(":core:core-ui"))
                add("api", project(":core:core-resources"))

                add("api", libs.findLibrary("androidx-core-ktx").get())
                add("api", libs.findLibrary("coroutines-android").get())
                
                add("api", platform(libs.findLibrary("compose-bom").get()))
                add("api", libs.findBundle("compose").get())
                add("api", libs.findBundle("lifecycle").get())
                
                add("api", libs.findLibrary("activity-compose").get())
                
                add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())
            }
        }
    }
}
