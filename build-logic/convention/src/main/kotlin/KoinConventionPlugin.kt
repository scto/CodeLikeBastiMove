import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

import com.scto.convention.libs

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("koin-android").get())
                add("implementation", libs.findLibrary("koin-compose").get())
            }
        }
    }
}
