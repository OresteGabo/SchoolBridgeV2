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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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

@Preview(showBackground = true)
@Composable
private fun pp() {
    CredentialsSetupScreen(
        onComplete = {
            email, password ->
        },
        onNext = {}
    ) 
    //PasswordStrengthIndicator(strength = PasswordStrength.FAIR, modifier = Modifier)
}

/**
 * Represents the different levels of password strength.
 */
enum class PasswordStrength {
    NONE, // No input or extremely weak
    WEAK,
    FAIR,
    GOOD,
    STRONG
}

/**
 * Calculates the strength of a given password based on predefined rules.
 * This is a pure function that can be easily tested.
 *
 * @param password The password string to evaluate.
 * @return The [PasswordStrength] level of the password.
 */
private fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) {
        return PasswordStrength.NONE
    }

    var score = 0
    val hasLowercase = password.any { it.isLowerCase() }
    val hasUppercase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }

    // Score based on length
    if (password.length >= 8) score++
    if (password.length >= 12) score++
    if (password.length >= 16) score++

    // Score based on character types
    val charTypeCount = listOf(hasLowercase, hasUppercase, hasDigit, hasSpecialChar).count { it }

    when (charTypeCount) {
        1 -> score += 0 // Only one type of character, minimal strength
        2 -> score += 1
        3 -> score += 2
        4 -> score += 3
    }

    // Map score to PasswordStrength enum
    return when {
        password.length < 6 -> PasswordStrength.NONE // Too short to be considered even weak
        score < 3 -> PasswordStrength.WEAK
        score < 5 -> PasswordStrength.FAIR
        score < 7 -> PasswordStrength.GOOD
        else -> PasswordStrength.STRONG
    }
}

/**
 * A Composable that visualizes the strength of a password using colored bars and textual feedback.
 *
 * @param strength The [PasswordStrength] level to display.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun PasswordStrengthIndicator(
    strength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    val inactiveColor = MaterialTheme.colorScheme.outline
    val weakColor = MaterialTheme.colorScheme.error
    val fairColor = MaterialTheme.colorScheme.tertiary
    val strongColor = MaterialTheme.colorScheme.primary

    // Now, we destructure from the BarColors data class
    val (bar1Color, bar2Color, bar3Color, bar4Color) = remember(strength) {
        when (strength) {
            PasswordStrength.NONE ->
                BarColors(inactiveColor, inactiveColor, inactiveColor, inactiveColor)
            PasswordStrength.WEAK ->
                BarColors(weakColor, inactiveColor, inactiveColor, inactiveColor)
            PasswordStrength.FAIR ->
                BarColors(fairColor, fairColor, inactiveColor, inactiveColor)
            PasswordStrength.GOOD ->
                BarColors(strongColor, strongColor, strongColor, inactiveColor)
            PasswordStrength.STRONG ->
                BarColors(strongColor, strongColor, strongColor, strongColor)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ... (Your Box Composables remain the same, using bar1Color, etc.) ...
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(bar1Color)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(bar2Color)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(bar3Color)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(bar4Color)
            )
        }

        // ... (Your Textual feedback remains the same) ...
        val strengthText = when (strength) {
            PasswordStrength.NONE -> "Enter a password"
            PasswordStrength.WEAK -> "Weak password"
            PasswordStrength.FAIR -> "Fair password"
            PasswordStrength.GOOD -> "Good password"
            PasswordStrength.STRONG -> "Strong password"
        }

        val textColor = when (strength) {
            PasswordStrength.NONE -> MaterialTheme.colorScheme.onSurfaceVariant
            PasswordStrength.WEAK -> weakColor
            PasswordStrength.FAIR -> fairColor
            PasswordStrength.GOOD, PasswordStrength.STRONG -> strongColor
        }

        Text(
            text = strengthText,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
data class BarColors(
    val bar1: Color,
    val bar2: Color,
    val bar3: Color,
    val bar4: Color
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialsSetupScreen(
    onComplete: (email: String?, password: String) -> Unit,
    onNext: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Strength Indicator integrated here
        PasswordStrengthIndicator(strength = passwordStrength)

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password Input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    if (canSubmit) {
                        onComplete(if (email.isNotBlank()) email else null, password)
                        onNext()
                    }
                }
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Error message for password mismatch
        if (!isMatch && confirmPassword.isNotEmpty()) {
            Text("Passwords do not match", color = Color.Red, fontSize = 12.sp)
        }
        // The password strength indicator now handles the "too short" feedback,
        // so you might not need this redundant check here.
        // if (!isPasswordValid && password.isNotEmpty()) {
        //     Text("Password should be at least 6 characters", color = Color.Red, fontSize = 12.sp)
        // }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onComplete(
                    if (email.isNotBlank()) email else null,
                    password
                )
                onNext()
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}