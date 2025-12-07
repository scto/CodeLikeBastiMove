import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("codelikebastimove.android.library")
                apply("codelikebastimove.android.library.compose")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            dependencies {
                add("implementation", project(":core:core-ui"))
                add("implementation", project(":core:core-resources"))
                
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
