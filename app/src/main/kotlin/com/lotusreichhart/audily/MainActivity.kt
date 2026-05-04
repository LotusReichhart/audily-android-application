package com.lotusreichhart.audily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.AudilyTheme
import com.lotusreichhart.audily.core.domain.util.NetworkMonitor
import com.lotusreichhart.audily.core.ui.permission.PermissionHandler
import com.lotusreichhart.audily.core.ui.permission.PermissionScreen
import com.lotusreichhart.audily.ui.AudilyApp
import com.lotusreichhart.audily.ui.rememberAudilyAppState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private var shouldExpandPlayer by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
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

            AudilyTheme {
                PermissionHandler(
                    onPermissionGranted = {
                        AudilyApp(
                            appState = appState
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
