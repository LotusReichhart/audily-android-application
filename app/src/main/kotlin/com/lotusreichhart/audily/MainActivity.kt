package com.lotusreichhart.audily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import android.content.Intent
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import com.lotusreichhart.audily.core.designsystem.theme.AudilyTheme
import com.lotusreichhart.audily.core.domain.util.NetworkMonitor
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.permission.PermissionHandler
import com.lotusreichhart.audily.core.ui.permission.PermissionScreen
import com.lotusreichhart.audily.ui.AudilyApp
import com.lotusreichhart.audily.ui.rememberAudilyAppState
import com.lotusreichhart.audily.core.domain.usecase.playback.state.RestorePlaybackSessionUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import androidx.compose.runtime.collectAsState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var restorePlaybackSessionUseCase: RestorePlaybackSessionUseCase
    
    @Inject
    lateinit var globalUiEventBus: GlobalUiEventBus

    @Inject
    lateinit var getUserPreferencesUseCase: GetUserPreferencesUseCase

    private var shouldExpandPlayer by mutableStateOf(false)
    private var isPreferencesLoaded by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isPreferencesLoaded }
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            val userPrefs by getUserPreferencesUseCase().collectAsState(initial = null)
            val uiSettings = userPrefs?.uiSettings

            LaunchedEffect(userPrefs) {
                if (userPrefs != null) {
                    isPreferencesLoaded = true
                }
            }

            val appTheme = uiSettings?.appTheme ?: com.lotusreichhart.audily.core.model.prefs.AppTheme.FOLLOW_SYSTEM
            val darkTheme = when (appTheme) {
                com.lotusreichhart.audily.core.model.prefs.AppTheme.LIGHT -> false
                com.lotusreichhart.audily.core.model.prefs.AppTheme.DARK -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            val dynamicColor = uiSettings?.dynamicColor ?: false
            val accentColor = uiSettings?.accentColor

            val appState = rememberAudilyAppState(
                networkMonitor = networkMonitor,
            )

            // Tự động mở NowPlaying nếu Intent yêu cầu
            LaunchedEffect(shouldExpandPlayer) {
                if (shouldExpandPlayer) {
                    appState.expandPanel()
                    shouldExpandPlayer = false
                }
            }

            AudilyTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor,
                accentColor = accentColor
            ) {
                PermissionHandler(
                    onPermissionGranted = {
                        AudilyApp(
                            modifier = Modifier.navigationBarsPadding(),
                            appState = appState,
                            globalUiEventBus = globalUiEventBus
                        )
                    },
                    deniedContent = { shouldShowRationale, onRequestPermission ->
                        PermissionScreen(
                            shouldShowRationale = shouldShowRationale,
                            onRequestPermission = onRequestPermission
                        )
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Khôi phục session mỗi khi quay lại app (nếu trình phát đã bị kill)
        lifecycleScope.launch {
            Timber.d("Audily Service Kill - Bắt đầy khôi phục Playback State...")
            restorePlaybackSessionUseCase()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == "com.lotusreichhart.audily.action.OPEN_PLAYER") {
            shouldExpandPlayer = true
        }
    }
}
