plugins {
    alias(libs.plugins.audily.android.library)
    alias(libs.plugins.audily.android.library.compose)
    alias(libs.plugins.audily.hilt)
}

android {
    namespace = "com.lotusreichhart.audily.core.ui"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.compose.material3.window.size)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}