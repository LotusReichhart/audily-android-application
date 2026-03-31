import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile = rootProject.file("local.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.lotusreichhart.audily"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.lotusreichhart.audily"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val envPath = System.getenv("KEYSTORE_PATH")
            val envStorePass = System.getenv("KEYSTORE_PASSWORD")
            val envAlias = System.getenv("KEY_ALIAS")
            val envKeyPass = System.getenv("KEY_PASSWORD")

            if (!envPath.isNullOrEmpty()) {
                storeFile = file(envPath)
                storePassword = envStorePass
                keyAlias = envAlias
                keyPassword = envKeyPass
            } else if (keystorePropertiesFile.exists() && keystoreProperties.containsKey("KEYSTORE_PATH")) {
                storeFile = file(keystoreProperties.getProperty("KEYSTORE_PATH") as String)
                storePassword = keystoreProperties.getProperty("KEYSTORE_PASSWORD") as String
                keyAlias = keystoreProperties.getProperty("KEY_ALIAS") as String
                keyPassword = keystoreProperties.getProperty("KEY_PASSWORD") as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}