plugins {
    alias(libs.plugins.audily.android.library)
    alias(libs.plugins.audily.hilt)
    alias(libs.plugins.audily.android.room)
}

android {
    namespace = "com.lotusreichhart.audily.core.database"
}

dependencies {
    implementation(libs.androidx.paging.runtime)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}