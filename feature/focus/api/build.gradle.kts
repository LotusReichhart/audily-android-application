plugins {
    alias(libs.plugins.audily.android.feature.api)
}

android {
    namespace = "com.lotusreichhart.audily.feature.focus.api"
}

dependencies {
    implementation(projects.core.navigation)
}