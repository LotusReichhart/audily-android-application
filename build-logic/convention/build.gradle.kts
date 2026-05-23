import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.lotusreichhart.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        // Android Application ( Sử dụng cho Android Activity )
        register("androidApplication") {
            id = libs.plugins.audily.android.application.asProvider().get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        // Android Application Compose ( Sử dụng cho Android Activity )
        register("androidApplicationCompose") {
            id = libs.plugins.audily.android.application.compose.get().pluginId
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        // Android Application Flavors ( Sử dụng cho Android Activity )
        register("androidApplicationFlavors") {
            id = libs.plugins.audily.android.application.flavors.get().pluginId
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }
        // Jvm Library ( Sử dụng cho module Kotlin thuần túy )
        register("jvmLibrary") {
            id = libs.plugins.audily.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        // Android Library ( Sử dụng cho các module Library )
        register("androidLibrary") {
            id = libs.plugins.audily.android.library.asProvider().get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        // Android Library Compose ( Sử dụng cho các module Library )
        register("androidLibraryCompose") {
            id = libs.plugins.audily.android.library.compose.get().pluginId
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        // Hilt
        register("hilt") {
            id = libs.plugins.audily.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        // Room
        register("androidRoom") {
            id = libs.plugins.audily.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        // Android Feature ( Sử dụng cho các Feature Module như Home, Auth, ... )
        register("androidFeatureApi") {
            id = libs.plugins.audily.android.feature.api.get().pluginId
            implementationClass = "AndroidFeatureApiConventionPlugin"
        }
        register("androidFeatureImpl") {
            id = libs.plugins.audily.android.feature.impl.get().pluginId
            implementationClass = "AndroidFeatureImplConventionPlugin"
        }
    }
}