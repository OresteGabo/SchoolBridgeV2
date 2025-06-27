package com.schoolbridge.v2.ui.home.common



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle

@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 120.dp,
    height: Dp = 100.dp,
    iconSize: Dp = 28.dp,
    backgroundIcon: ImageVector = icon,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    Card(
        modifier = modifier
            .width(width)
            .height(height)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = shape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(Modifier.fillMaxSize()) {

            /* ——— faint oversized background icon ——— */
            Icon(
                imageVector = backgroundIcon,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.05f),
                modifier = Modifier
                    .size(160.dp)              // even larger for stronger crop
                    .align(Alignment.TopEnd)
                    .offset(x = 28.dp, y = (-36).dp)
            )

            /* ——— TOP-LEFT small icon (drawn AFTER bg) ——— */
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,        // high-contrast tint
                modifier = Modifier
                    .padding(12.dp)     // ← padding first
                    .size(iconSize)     // ← then the fixed 28 dp size
                    .align(Alignment.TopStart)
            )

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,   // high-contrast tint
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )

            /* ——— BOTTOM-CENTER title ——— */
            Text(
                text = title,
                style = textStyle,
                color = contentColor,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }
    }
}



