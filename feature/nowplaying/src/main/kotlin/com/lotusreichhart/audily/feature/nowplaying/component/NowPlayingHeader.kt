package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark
import com.lotusreichhart.audily.feature.nowplaying.R

@Composable
internal fun NowPlayingHeader(
    onCloseClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AudilyIconButton(
            onClick = onCloseClick,
            painter = painterResource(id = AudilyIcons.ArrowDown),
            contentDescription = "Close",
            modifier = Modifier.offset(x = (-8).dp),
            containerSize = 32.dp,
            iconSize = 24.dp,
            tint = OnSurfaceDark
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.feature_nowplaying_title),
            style = MaterialTheme.typography.titleMedium,
            color = OnSurfaceDark,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        AudilyIconButton(
            onClick = onMenuClick,
            painter = painterResource(id = AudilyIcons.VerticalDot),
            contentDescription = "Menu",
            modifier = Modifier.offset(x = 12.dp),
            containerSize = 32.dp,
            iconSize = 24.dp,
            tint = OnSurfaceDark
        )
    }
}
