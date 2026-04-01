package com.lotusreichhart.audily

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.kotlin.dsl.invoke

@Suppress("EnumEntryName")
enum class FlavorDimension {
    environment
}

@Suppress("EnumEntryName")
enum class AudilyFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    dev(FlavorDimension.environment, applicationIdSuffix = ".dev"),
    prod(FlavorDimension.environment)
}

fun configureFlavors(
    commonExtension: CommonExtension,
    flavorConfigurationBlock: ProductFlavor.(flavor: AudilyFlavor) -> Unit = {},
) {
    commonExtension.apply {
       FlavorDimension.entries.forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            AudilyFlavor.entries.forEach { AudilyFlavor ->
                register(AudilyFlavor.name) {
                    dimension = AudilyFlavor.dimension.name
                    flavorConfigurationBlock(this, AudilyFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (AudilyFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = AudilyFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}