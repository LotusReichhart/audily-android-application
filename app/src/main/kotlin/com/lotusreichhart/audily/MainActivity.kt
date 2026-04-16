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

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = rememberAudilyAppState(
                networkMonitor = networkMonitor,
            )
            AudilyTheme {
                PermissionHandler(
                    onPermissionGranted = {
                        AudilyApp(
                            appState = appState,
                            miniPlayer = { alpha ->
                                FakeMiniPlayer(
                                    alpha = alpha,
                                    onClick = {
                                        appState.expandPanel()
                                    }
                                )
                            },
                            fullPlayer = { alpha ->
                                FakeFullPlayer(
                                    alpha = alpha,
                                    onClose = {
                                        appState.collapsePanel()
                                    }
                                )
                            }
                        ) {
                            FakeContent()
                        }
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
}

@Composable
fun FakeMiniPlayer(alpha: Float, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .alpha(alpha)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text("Mini Player (Tap to expand)", color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun FakeFullPlayer(alpha: Float, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Full Player", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClose) {
                Text("Close")
            }
        }
    }
}

@Composable
fun FakeContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text("Main Content (Songs/Playlists)", color = MaterialTheme.colorScheme.onBackground)
    }
}
