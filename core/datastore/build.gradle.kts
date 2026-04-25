plugins {
    alias(libs.plugins.audily.android.library)
    alias(libs.plugins.audily.hilt)
}

android {
    namespace = "com.lotusreichhart.audily.core.datastore"
}

dependencies{
    implementation(libs.androidx.dataStore)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}