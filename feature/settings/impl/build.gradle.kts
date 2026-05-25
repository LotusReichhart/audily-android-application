import java.util.Properties

plugins {
    alias(libs.plugins.audily.android.feature.impl)
    alias(libs.plugins.audily.android.library.compose)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

val privacyUrl = System.getenv("AUDILY_PRIVACY_URL")
    ?: localProperties.getProperty("AUDILY_PRIVACY_URL")
    ?: "https://audily.com/privacy"

val termsUrl = System.getenv("AUDILY_TERMS_URL")
    ?: localProperties.getProperty("AUDILY_TERMS_URL")
    ?: "https://audily.com/terms"

val feedbackEmail = System.getenv("AUDILY_FEEDBACK_EMAIL")
    ?: localProperties.getProperty("AUDILY_FEEDBACK_EMAIL")
    ?: "lotusreichhart@gmail.com"

android {
    namespace = "com.lotusreichhart.audily.feature.settings.impl"

    defaultConfig {
        buildConfigField("String", "PRIVACY_URL", "\"$privacyUrl\"")
        buildConfigField("String", "TERMS_URL", "\"$termsUrl\"")
        buildConfigField("String", "FEEDBACK_EMAIL", "\"$feedbackEmail\"")
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(projects.feature.settings.api)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
}