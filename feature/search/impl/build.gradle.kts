plugins {
    alias(libs.plugins.audily.android.feature.impl)
    alias(libs.plugins.audily.android.library.compose)
}


android {
    namespace = "com.lotusreichhart.audily.feature.search.impl"
}

dependencies {
    implementation(projects.feature.search.api)
    implementation(projects.feature.songs.api)
    implementation(projects.feature.playlists.api)
    implementation(projects.feature.albums.api)

    implementation(projects.core.model)
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
}