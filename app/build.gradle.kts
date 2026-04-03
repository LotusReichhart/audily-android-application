import com.lotusreichhart.audily.AudilyBuildType
import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile = rootProject.file("local.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

plugins {
    alias(libs.plugins.audily.android.application)
    alias(libs.plugins.audily.android.application.compose)
    alias(libs.plugins.audily.android.application.flavors)
    alias(libs.plugins.audily.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.lotusreichhart.audily"
    defaultConfig {
        applicationId = "com.lotusreichhart.audily"
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePathFromEnv = System.getenv("KEYSTORE_PATH")

            if (!keystorePathFromEnv.isNullOrEmpty()) {
                storeFile = file(keystorePathFromEnv)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
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
            isMinifyEnabled = providers.gradleProperty("minifyWithR8")
                .map(String::toBooleanStrict).getOrElse(true)
            applicationIdSuffix = AudilyBuildType.RELEASE.applicationIdSuffix
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(projects.core.designsystem)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.core.splashscreen)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}