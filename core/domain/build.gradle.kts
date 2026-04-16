plugins {
    alias(libs.plugins.audily.android.library)
}

android {
    namespace = "com.lotusreichhart.audily.core.domain"
}

dependencies{
    implementation(projects.core.model)

    implementation(libs.javax.inject)
    implementation(libs.androidx.paging.runtime)
}