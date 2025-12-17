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
                add("api", project(":core:core-ui"))
                add("api", project(":core:core-resources"))
                
                add("api", libs.androidx.core.ktx)
                add("api", libs.coroutines.android)
                
                add("api", libs.coil.compose)
                add("api", platform(libs.compose.bom))
                add("api", libs.bundles.compose)
                add("api", libs.bundles.lifecycle)
                
                add("api", libs.activity.compose)
                
                add("debugImplementation", libs.compose.ui.tooling)
            }
        }
    }
}
