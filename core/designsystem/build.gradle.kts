plugins {
    alias(libs.plugins.audily.android.library)
    alias(libs.plugins.audily.android.library.compose)
    alias(libs.plugins.audily.hilt)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.lotusreichhart.audily.core.designsystem"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

roborazzi {
    outputDir.set(file("src/test/screenshots"))
}

dependencies {
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}