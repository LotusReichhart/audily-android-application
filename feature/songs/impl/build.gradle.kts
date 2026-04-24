plugins {
    alias(libs.plugins.audily.android.feature.impl)
    alias(libs.plugins.audily.android.library.compose)
}

android {
    namespace = "com.lotusreichhart.audily.feature.songs.impl"
}

dependencies {
    implementation(projects.feature.songs.api)
    implementation(projects.core.model)
    
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.coil.compose)
}