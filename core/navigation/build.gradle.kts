plugins {
    alias(libs.plugins.audily.android.library)
    alias(libs.plugins.audily.android.library.compose)
}

android {
    namespace = "com.lotusreichhart.audily.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}