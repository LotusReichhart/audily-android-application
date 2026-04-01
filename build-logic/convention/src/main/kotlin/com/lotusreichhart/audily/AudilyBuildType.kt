package com.lotusreichhart.audily

enum class AudilyBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}