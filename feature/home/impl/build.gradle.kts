plugins {
    alias(libs.plugins.audily.android.feature.impl)
    alias(libs.plugins.audily.android.library.compose)
}

android {
    namespace = "com.lotusreichhart.audily.feature.home.impl"
}

dependencies {
    implementation(projects.core.navigation)
    implementation(projects.core.model)

    implementation(projects.feature.home.api)

    implementation(projects.feature.songs.api)
    implementation(projects.feature.playlists.api)
    implementation(projects.feature.albums.api)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
}