package com.schoolbridge.v2.ui.common.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp,
    endIndent: Dp = 0.dp,
    verticalPadding: Dp = 8.dp
) {
    HorizontalDivider(
        modifier = modifier
            .padding(vertical = verticalPadding)
            .padding(start = startIndent, end = endIndent),
        color = color,
        thickness = thickness
    )
}

@Composable
fun AppSectionDivider(
    modifier: Modifier = Modifier
) {
    AppDivider(
        modifier = modifier,
        thickness = 2.dp,
        verticalPadding = 16.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )
}

@Composable
fun AppSubSectionDivider(
    modifier: Modifier = Modifier
) {
    AppDivider(
        modifier = modifier,
        thickness = 1.dp,
        verticalPadding = 12.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
fun AppInsetDivider(
    modifier: Modifier = Modifier,
    startIndent: Dp = 72.dp
) {
    AppDivider(
        modifier = modifier,
        thickness = 1.dp,
        verticalPadding = 8.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
        startIndent = startIndent
    )
}

@Composable
fun AppAlertDivider(
    modifier: Modifier = Modifier
) {
    AppDivider(
        modifier = modifier,
        thickness = 2.dp,
        verticalPadding = 12.dp,
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
    )
}


@Preview(showBackground = true)
@Composable
private fun prev() {
    Column {
        AppHeader("Section Title")
        AppSectionDivider()

        AppSubHeader("Subsection")
        AppSubSectionDivider()

        Text("Item 1")
        AppInsetDivider()

        Text("Item 2")
        AppDivider()  // default thin separator
    }

}