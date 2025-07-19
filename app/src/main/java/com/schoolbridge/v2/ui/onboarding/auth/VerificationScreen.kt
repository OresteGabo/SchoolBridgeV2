package com.schoolbridge.v2.ui.onboarding.auth

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.ui.onboarding.auth.verification.OtpInputField
import com.schoolbridge.v2.ui.onboarding.auth.verification.StepProgressIndicator
import com.schoolbridge.v2.ui.onboarding.auth.verification.VerificationHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VerificationScreen(
    phoneNumber: String,
    onVerificationSuccess: () -> Unit,
    onResendCode: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var inputCode by remember { mutableStateOf("") }
    var showIncorrectCodeError by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableIntStateOf(60) }
    var showResend by remember { mutableStateOf(false) }
    var isLocked by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }

    // Shake animation for incorrect input
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000)
            remainingTime--
        } else {
            showResend = true
        }
    }

    fun verifyCode() {
        isVerifying = true
        coroutineScope.launch {
            delay(1000)
            if (inputCode == "1234") {  // Replace with real verification logic
                onVerificationSuccess()
            } else {
                showIncorrectCodeError = true
                // Shake animation
                launch {
                    shakeOffset.snapTo(0f)
                    val shake = keyframes<Float> {
                        durationMillis = 400
                        0f at 0
                        -16f at 50
                        16f at 100
                        -16f at 150
                        16f at 200
                        -16f at 250
                        16f at 300
                        0f at 400
                    }
                    shakeOffset.animateTo(0f, animationSpec = shake)
                }
                keyboardController?.hide()
                inputCode = ""
                delay(5000)
                showIncorrectCodeError = false
            }
            isVerifying = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .offset(x = shakeOffset.value.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    StepProgressIndicator(currentStep = 1, totalSteps = 3)
                    Spacer(modifier = Modifier.height(32.dp))

                    Icon(
                        imageVector = Icons.Default.Sms,
                        contentDescription = "SMS Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )

                    Text(
                        text = "Please check your phone number",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    VerificationHeader(phoneNumber)
                    Spacer(modifier = Modifier.height(24.dp))

                    OtpInputField(
                        value = inputCode,
                        onValueChange = {
                            if (!isLocked && !isVerifying && it.length <= 4) {
                                inputCode = it
                                if (it.length == 4) {
                                    verifyCode()
                                }
                            }
                        },
                        isError = showIncorrectCodeError,
                        isEnabled = !isLocked && !isVerifying
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLocked) {
                        Text(
                            "Too many attempts. Try again later.",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (!showResend) {
                        LinearProgressIndicator(
                            progress = remainingTime / 60f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (!isLocked && inputCode.length == 4 && !isVerifying) {
                                verifyCode()
                            }
                        },
                        enabled = !isLocked && inputCode.length == 4 && !isVerifying,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Verify")
                        }
                    }

                    if (showResend) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    Toast.makeText(context, "Resending code...", Toast.LENGTH_SHORT).show()
                                    delay(1000)
                                    remainingTime = 60
                                    showResend = false
                                    Toast.makeText(context, "Code resent.", Toast.LENGTH_SHORT).show()
                                    onResendCode()
                                }
                            }
                        ) {
                            Text("Didn’t receive code? Resend")
                        }
                    }
                }
            }
        }
    }
}
