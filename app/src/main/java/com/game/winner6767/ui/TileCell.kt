package com.game.winner6767.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.winner6767.ui.theme.getTileBackground
import com.game.winner6767.ui.theme.getTileTextColor
import kotlinx.coroutines.delay

@Composable
fun TileCell(value: Int, modifier: Modifier = Modifier) {
    var targetScale by remember { mutableFloatStateOf(1f) }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 100),
        label = "tile_scale"
    )

    LaunchedEffect(value) {
        if (value != 0) {
            targetScale = 1.15f
            delay(100)
            targetScale = 1f
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(getTileBackground(value))
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        if (value != 0) {
            Text(
                text = value.toString(),
                color = getTileTextColor(value),
                fontSize = when {
                    value < 100 -> 32.sp
                    value < 1000 -> 24.sp
                    value < 10000 -> 18.sp
                    else -> 14.sp
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}
