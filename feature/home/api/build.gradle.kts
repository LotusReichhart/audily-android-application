plugins {
    alias(libs.plugins.audily.android.feature.api)
    alias(libs.plugins.audily.android.library.compose)
}

android {
    namespace = "com.lotusreichhart.audily.feature.home.api"
}

dependencies {
    implementation(projects.core.navigation)
}