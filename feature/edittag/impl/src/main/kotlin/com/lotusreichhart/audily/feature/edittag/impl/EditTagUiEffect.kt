package com.lotusreichhart.audily.feature.edittag.impl

internal sealed interface EditTagUiEffect {
    object EditTagSaved : EditTagUiEffect
}