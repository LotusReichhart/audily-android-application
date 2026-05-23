plugins {
    alias(libs.plugins.audily.jvm.library)
    alias(libs.plugins.audily.hilt)
}

dependencies{
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}