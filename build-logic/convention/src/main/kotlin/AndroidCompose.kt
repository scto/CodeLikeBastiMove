import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
                excludes += "META-INF/kotlinx_coroutines_core.version"
                pickFirsts += "nonJvmMain/default/linkdata/package_androidx/0_androidx.knm"
                pickFirsts += "nonJvmMain/default/linkdata/root_package/0_.knm"
                pickFirsts += "nonJvmMain/default/linkdata/module"
                pickFirsts += "nativeMain/default/linkdata/root_package/0_.knm"
                pickFirsts += "nativeMain/default/linkdata/module"
                pickFirsts += "commonMain/default/linkdata/root_package/0_.knm"
                pickFirsts += "commonMain/default/linkdata/module"
                pickFirsts += "commonMain/default/linkdata/package_androidx/0_androidx.knm"
                pickFirsts += "META-INF/kotlin-project-structure-metadata.json"
                merges += "commonMain/default/manifest"
                merges += "nonJvmMain/default/manifest"
                merges += "nativeMain/default/manifest"
            }
        }
    }
}
