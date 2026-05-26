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
        versionCode = 4
        versionName = "1.0.2"

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
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.core.playback)
    implementation(projects.core.palette)
    implementation(projects.core.network)
    implementation(libs.coil.compose)

    implementation(projects.feature.home.api)
    implementation(projects.feature.home.impl)
    implementation(projects.feature.focus.api)
    implementation(projects.feature.settings.api)
    implementation(projects.feature.settings.impl)
    implementation(projects.feature.songs.api)
    implementation(projects.feature.songs.impl)
    implementation(projects.feature.playlists.api)
    implementation(projects.feature.playlists.impl)
    implementation(projects.feature.favorites.api)
    implementation(projects.feature.favorites.impl)
    implementation(projects.feature.search.api)
    implementation(projects.feature.search.impl)
    implementation(projects.feature.nowplaying)
    implementation(projects.feature.edittag.api)
    implementation(projects.feature.edittag.impl)
    implementation(projects.feature.albums.api)
    implementation(projects.feature.albums.impl)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.material3.window.size)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}