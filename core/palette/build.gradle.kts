plugins {
    alias(libs.plugins.audily.android.library)
    alias(libs.plugins.audily.hilt)
}

android {
    namespace = "com.lotusreichhart.audily.core.palette"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.domain)

    implementation(libs.coil.compose)
    implementation(libs.androidx.palette)
}