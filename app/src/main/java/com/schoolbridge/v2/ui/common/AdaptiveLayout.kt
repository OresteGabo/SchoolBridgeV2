package com.schoolbridge.v2.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun isExpandedLayout(): Boolean = LocalConfiguration.current.screenWidthDp >= 840

@Composable
fun isWideLandscapeLayout(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= 1100 &&
        configuration.screenWidthDp > configuration.screenHeightDp
}

@Composable
fun AdaptivePageFrame(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
    maxContentWidth: androidx.compose.ui.unit.Dp = 1240.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = maxContentWidth)
        ) {
            content()
        }
    }
}
