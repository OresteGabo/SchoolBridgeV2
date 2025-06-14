package com.schoolbridge.v2.ui.onboarding.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.VisualTransformation

@Preview(showBackground = true)
@Composable
private fun pp() {
    CredentialsSetupScreen(
        onContinue = TODO(),
        modifier = TODO()
    ) 
    //PasswordStrengthIndicator(strength = PasswordStrength.FAIR, modifier = Modifier)
}

@Composable
fun CredentialsSetupScreen(
    onContinue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val passwordsMatch = password == confirmPassword && password.isNotEmpty()
    val isValid = passwordsMatch && calculatePasswordStrength(password) >= PasswordStrength.FAIR

    var email by remember { mutableStateOf("") }


    // Calculate password strength in real-time as the password changes
    val passwordStrength by remember(password) {
        mutableStateOf(calculatePasswordStrength(password))
    }

    val isEmailValid = email.isBlank() || isValidEmail(email)
    val showError = email.isNotBlank() && !isEmailValid
    val isValidEmailOrBlank = isEmailValid || email.isBlank()

    // Password is considered "valid" for submission if it's not "NONE" (i.e., at least WEAK)
    val isPasswordSufficient = passwordStrength != PasswordStrength.NONE
    val isMatch = password == confirmPassword

    // Can submit only if password is sufficient, passwords match, and email is valid/blank
    val canSubmit = isPasswordSufficient && isMatch && isValidEmailOrBlank

    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Secure Your Account", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = "250788000000",
            onValueChange = {},
            label = { Text("Phone Number (Username)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email (optional)") },
            isError = showError,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            supportingText = {
                if (showError) {
                    Text("Invalid email address", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        PasswordStrengthIndicatorWithTips(password = password)

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(icon, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (confirmPassword.isNotEmpty() && !passwordsMatch) {
            Text(
                "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onContinue(password) },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun PasswordStrengthIndicatorWithTips(
    password: String,
    modifier: Modifier = Modifier
) {
    val strength = remember(password) { calculatePasswordStrength(password) }

    // Choose color palette
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    val weakColor = MaterialTheme.colorScheme.error
    val fairColor = MaterialTheme.colorScheme.tertiary
    val strongColor = MaterialTheme.colorScheme.primary

    val barColors = when (strength) {
        PasswordStrength.NONE -> List(4) { inactiveColor }
        PasswordStrength.WEAK -> listOf(weakColor, inactiveColor, inactiveColor, inactiveColor)
        PasswordStrength.FAIR -> listOf(fairColor, fairColor, inactiveColor, inactiveColor)
        PasswordStrength.GOOD -> listOf(strongColor, strongColor, strongColor, inactiveColor)
        PasswordStrength.STRONG -> List(4) { strongColor }
    }

    Column(modifier = modifier.fillMaxWidth(),) {

        // Strength bars
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            barColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(color, RoundedCornerShape(2.dp))
                )
            }
        }

        // Label
        val label = when (strength) {
            PasswordStrength.NONE -> "Too short"
            PasswordStrength.WEAK -> "Weak"
            PasswordStrength.FAIR -> "Fair"
            PasswordStrength.GOOD -> "Good"
            PasswordStrength.STRONG -> "Strong"
        }

        val labelColor = when (strength) {
            PasswordStrength.WEAK -> weakColor
            PasswordStrength.FAIR -> fairColor
            else -> strongColor
        }

        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Tips
        if (password.isNotEmpty()) {
            val hasUppercase = password.any { it.isUpperCase() }
            val hasDigit = password.any { it.isDigit() }
            val hasSpecial = password.any { !it.isLetterOrDigit() }

            val tips = buildList {
                if (password.length < 8) add("Increase length")
                if (!hasUppercase) add("Add uppercase")
                if (!hasDigit) add("Add a number")
                if (!hasSpecial) add("Add symbols (!@#\$...)")
            }

            if (tips.isNotEmpty()) {
                Text(
                    text = "Tips: ${tips.joinToString(", ")}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Strength levels
enum class PasswordStrength {
    NONE, WEAK, FAIR, GOOD, STRONG
}

// Strength calculation
fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.length < 6) return PasswordStrength.NONE

    val lengthScore = when {
        password.length >= 16 -> 4
        password.length >= 12 -> 3
        password.length >= 8 -> 2
        else -> 1
    }

    val diversity = listOf(
        password.any { it.isLowerCase() },
        password.any { it.isUpperCase() },
        password.any { it.isDigit() },
        password.any { !it.isLetterOrDigit() }
    ).count { it }

    val totalScore = lengthScore + diversity + if (diversity >= 3 && password.length >= 12) 1 else 0

    return when (totalScore) {
        in 0..2 -> PasswordStrength.WEAK
        in 3..4 -> PasswordStrength.FAIR
        in 5..6 -> PasswordStrength.GOOD
        else -> PasswordStrength.STRONG
    }
}


fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}