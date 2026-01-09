import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

import com.scto.convention.configureAndroidCompose

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            
            pluginManager.withPlugin("com.android.application") {
                val extension = extensions.getByType<ApplicationExtension>()
                configureAndroidCompose(extension)
            }

            pluginManager.withPlugin("com.android.library") {
                val extension = extensions.getByType<LibraryExtension>()
                configureAndroidCompose(extension)
            }
        }
    }
}
