import com.android.build.api.dsl.LibraryExtension
import com.lotusreichhart.audily.configureAndroidCompose
import com.lotusreichhart.audily.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "audily.android.library")
            apply(plugin = "audily.hilt")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
                configureAndroidCompose(this)
            }

            dependencies {
                "implementation"(project(":core:model"))
                "implementation"(project(":core:common"))
                "implementation"(project(":core:domain"))
                "implementation"(project(":core:data"))
                "implementation"(project(":core:ui"))

                "implementation"(libs.findLibrary("timber").get())
                "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                "implementation"(libs.findLibrary("androidx.navigation.compose").get())

                "testImplementation"(libs.findLibrary("androidx.navigation.testing").get())
            }
        }
    }
}