plugins {
    alias(libs.plugins.audily.android.feature.api)
}

android {
    namespace = "com.lotusreichhart.audily.feature.home.api"
}

dependencies {
    implementation(projects.core.navigation)
}