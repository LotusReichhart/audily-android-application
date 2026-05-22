plugins {
    alias(libs.plugins.audily.android.library)
    alias(libs.plugins.audily.hilt)
}

android {
    namespace = "com.lotusreichhart.audily.core.network"
}

dependencies{
    implementation(projects.core.common)
    implementation(libs.coil.compose)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
}