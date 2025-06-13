package com.schoolbridge.v2.ui.onboarding.legal

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun LegalText(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    val annotatedText = buildAnnotatedString {
        append("By continuing, you agree to our ")

        pushStringAnnotation("TERMS", "terms")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
            append("Terms of Service")
        }
        pop()

        append(" and ")

        pushStringAnnotation("PRIVACY", "privacy")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
            append("Privacy Policy")
        }
        pop()

        append(".")
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset).firstOrNull()?.let {
                onTermsClick()
            }
            annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset).firstOrNull()?.let {
                onPrivacyClick()
            }
        },
        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, textAlign = TextAlign.Center),
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}