import com.android.build.api.dsl.LibraryExtension
import com.lotusreichhart.audily.configureFlavors
import com.lotusreichhart.audily.configureKotlinAndroid
import com.lotusreichhart.audily.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                testOptions.targetSdk = 36
                lint.targetSdk = 36
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                configureFlavors(this)

                resourcePrefix = path.split("""\W""".toRegex())
                    .drop(1)
                    .distinct()
                    .joinToString(separator = "_")
                    .lowercase() + "_"
            }

            dependencies {
                "implementation"(libs.findLibrary("androidx-core-ktx").get())
                "implementation"(libs.findLibrary("kotlinx.coroutines.android").get())
                "implementation"(libs.findLibrary("timber").get())
                "testImplementation"(libs.findLibrary("kotlin.test").get())
                "androidTestImplementation"(libs.findLibrary("kotlin.test").get())
            }
        }
    }
}