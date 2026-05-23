plugins {
    alias(libs.plugins.audily.android.feature.impl)
    alias(libs.plugins.audily.android.library.compose)
}

android {
    namespace = "com.lotusreichhart.audily.feature.albums.impl"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)

    implementation(projects.feature.albums.api)
    implementation(projects.feature.search.api)
    implementation(projects.feature.playlists.api)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
}