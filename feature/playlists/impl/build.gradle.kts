plugins {
    alias(libs.plugins.audily.android.feature.impl)
    alias(libs.plugins.audily.android.library.compose)
}

android {
    namespace = "com.lotusreichhart.audily.feature.playlists.impl"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(projects.feature.playlists.api)
    implementation(projects.feature.search.api)
    implementation(projects.feature.songs.api)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.compose.animation)

    implementation(libs.drag.reorder)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
}