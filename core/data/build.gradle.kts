plugins {
    alias(libs.plugins.audily.android.library)
    alias(libs.plugins.audily.hilt)
}

android {
    namespace = "com.lotusreichhart.audily.core.data"
}

dependencies{
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.mediastore)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.network)

    implementation(libs.androidx.paging.runtime)
}
