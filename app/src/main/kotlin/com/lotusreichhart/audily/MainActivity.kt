package com.lotusreichhart.audily

import android.os.Bundle
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
import com.lotusreichhart.audily.core.model.prefs.AppTheme
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.lotusreichhart.audily.core.model.prefs.AppLanguage

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
                    val appLanguage = userPrefs?.uiSettings?.appLanguage ?: AppLanguage.FOLLOW_SYSTEM
                    val currentLocales = AppCompatDelegate.getApplicationLocales()
                    val targetLocales = when (appLanguage) {
                        AppLanguage.FOLLOW_SYSTEM -> LocaleListCompat.getEmptyLocaleList()
                        AppLanguage.ENG_LIST -> LocaleListCompat.forLanguageTags("en")
                        AppLanguage.VIETNAMESE -> LocaleListCompat.forLanguageTags("vi")
                    }
                    // So sánh tag ngôn ngữ thay vì so sánh đối tượng LocaleListCompat trực tiếp
                    val currentTag = currentLocales.toLanguageTags()
                    val targetTag = targetLocales.toLanguageTags()
                    Timber.d("Language sync: currentTag='$currentTag', targetTag='$targetTag'")
                    if (currentTag != targetTag) {
                        AppCompatDelegate.setApplicationLocales(targetLocales)
                    }
                }
            }

            val appTheme = uiSettings?.appTheme ?: AppTheme.FOLLOW_SYSTEM
            val darkTheme = when (appTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
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
                    deniedContent = { isNotification, shouldShowRationale, onRequestPermission ->
                        PermissionScreen(
                            isNotification = isNotification,
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
