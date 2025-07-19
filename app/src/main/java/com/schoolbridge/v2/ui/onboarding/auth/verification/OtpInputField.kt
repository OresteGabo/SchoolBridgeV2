package com.schoolbridge.v2.ui.onboarding.auth.verification

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun OtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    isEnabled: Boolean = true,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val focusRequesters = remember { List(4) { FocusRequester() } }

    // Track which box is currently focused
    var focusedIndex by remember { mutableIntStateOf(0) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (0 until 4).forEach { index ->
            val char = value.getOrNull(index)?.toString() ?: ""
            val hasInput = char.isNotEmpty()
            val isActive = focusedIndex == index && isEnabled

            val borderColor by animateColorAsState(
                when {
                    isError -> MaterialTheme.colorScheme.error
                    isActive -> MaterialTheme.colorScheme.primary
                    hasInput -> MaterialTheme.colorScheme.outlineVariant
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                }, label = "otpBorderColor"
            )

            val backgroundColor by animateColorAsState(
                targetValue = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                label = "otpBackgroundColor"
            )

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
                    .border(
                        width = if (isActive || isError) 2.dp else 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .focusRequester(focusRequesters[index])
                    .semantics { contentDescription = "OTP Digit ${index + 1}" }
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (isEnabled) {
                                focusRequesters[index].requestFocus()
                                focusedIndex = index
                            }
                        },
                        onLongClick = {
                            if (isEnabled) {
                                val clipText = clipboardManager.getText()?.text
                                if (!clipText.isNullOrEmpty() && clipText.length == 4 && clipText.all(Char::isDigit)) {
                                    onValueChange(clipText)
                                    Toast.makeText(context, "Code pasted.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char,
                    style = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                )
            }
        }
    }

    // Hidden transparent TextField to catch keyboard input
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (!isEnabled) return@OutlinedTextField

            // Only digits, max length 4
            val filtered = newValue.filter { it.isDigit() }.take(4)
            onValueChange(filtered)

            // Update focused index after input
            focusedIndex = filtered.length.coerceAtMost(3)

            if (filtered.length == 4) {
                focusManager.clearFocus()
            } else {
                focusRequesters[focusedIndex].requestFocus()
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = true,
        modifier = Modifier
            .size(1.dp)
            .alpha(0f)
    )
}

