plugins {
    alias(libs.plugins.audily.android.library)
    alias(libs.plugins.audily.hilt)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.lotusreichhart.audily.core.datastore"

    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
    }
}

dependencies{
    implementation(libs.androidx.dataStore)
    api(libs.protobuf.kotlin.lite)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin")
            }
        }
    }
}