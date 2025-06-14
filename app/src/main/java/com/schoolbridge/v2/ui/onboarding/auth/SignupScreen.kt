package com.schoolbridge.v2.ui.onboarding.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.schoolbridge.v2.R
import com.schoolbridge.v2.domain.user.Gender
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.onboarding.legal.LegalText


//@OptIn(ExperimentalMaterial3Api::class) // For LocalSoftwareKeyboardController in Material 3
@Composable
fun SignUpScreen(
    navController: NavController,
    onContinueAsGuest: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onNext: (firstName: String, lastName: String, phoneNumber: String, selectedGender: Gender?) -> Unit,
    onPrivacyPolicyClick: @Composable () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }

    val lastNameFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }
    val genderFocusRequester = remember { FocusRequester() } // Still useful for initial focus if needed

    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current // KEY CHANGE 1

    val isFormValid = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            phoneNumber.length >= 9 && // Assuming 9 is the minimum required digits for a valid Rwandan number
            selectedGender != null

    fun normalizePhone(input: String): String {
        val digits = input.filter { it.isDigit() }
        var cleaned = digits.removePrefix("0")
        if (cleaned.startsWith("250")) {
            cleaned = cleaned.removePrefix("250")
        }
        return "250$cleaned"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = t(R.string.create_account),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // First Name
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(t(R.string.first_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                lastNameFocusRequester.requestFocus()
            })
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Last Name
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(t(R.string.last_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(lastNameFocusRequester),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                phoneFocusRequester.requestFocus()
            })
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it.filter { char -> char.isDigit() } },
            label = { Text(t(R.string.phone_number)) },
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🇷🇼 +250", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(phoneFocusRequester),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide() // KEY CHANGE 2: Hide the keyboard!
                    // Optionally, you could still request focus to the gender section
                    // after hiding the keyboard to ensure it's visible and active.
                    genderFocusRequester.requestFocus()
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Gender Selection ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(genderFocusRequester) // Still useful for setting focus to this section
        ) {
            Text(
                text = t(R.string.select_gender),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .selectableGroup(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GenderOption(Gender.MALE, selectedGender) { selectedGender = it }
                GenderOption(Gender.FEMALE, selectedGender) { selectedGender = it }
                GenderOption(Gender.OTHER, selectedGender) { selectedGender = it }
                // GenderOption(Gender.PREFER_NOT_TO_SAY, selectedGender) { selectedGender = it }
            }
        }
        // --- End Gender Selection ---

        Spacer(modifier = Modifier.height(24.dp))

        LegalText(
            onTermsClick = onTermsClick,
            onPrivacyClick = onPrivacyClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onNext(firstName, lastName, normalizePhone(phoneNumber), selectedGender)
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(t(R.string.next))
        }

        TextButton(
            onClick = onContinueAsGuest,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(t(R.string.continue_as_guest), color = MaterialTheme.colorScheme.primary)
        }

        TextButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(t(R.string.go_to_login), color = MaterialTheme.colorScheme.primary)
        }
    }
}

// GenderOption composable (remains the same)
@Composable
fun GenderOption(
    genderOption: Gender,
    selectedGender: Gender?,
    onSelected: (Gender) -> Unit
) {
    Row(
        modifier = Modifier
            .selectable(
                selected = (selectedGender == genderOption),
                onClick = { onSelected(genderOption) },
                role = Role.RadioButton
            )
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (selectedGender == genderOption),
            onClick = null
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = when (genderOption) {
                Gender.MALE -> t(R.string.gender_male)
                Gender.FEMALE -> t(R.string.gender_female)
                Gender.OTHER -> t(R.string.gender_other)
                Gender.PREFER_NOT_TO_SAY -> "Prefer not to say"
            },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}