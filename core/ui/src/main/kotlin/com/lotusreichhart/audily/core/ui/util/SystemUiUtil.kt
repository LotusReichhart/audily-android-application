package com.lotusreichhart.audily.core.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun StatusBarColorEffect(isLightIcon: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current

    if (!view.isInEditMode) {
        SideEffect {
            val activity = context.findActivity()
            activity?.window?.let { window ->
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    !isLightIcon
            }
        }
    }
}