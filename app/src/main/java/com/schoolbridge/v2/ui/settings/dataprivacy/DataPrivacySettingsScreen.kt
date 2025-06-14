package com.schoolbridge.v2.ui.settings.dataprivacy


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.schoolbridge.v2.ui.onboarding.legal.LegalText
import kotlinx.coroutines.delay

// Helper Composables from your code (or similar functionality)
@Composable
fun WarningSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFFFF3CD), shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Warning",
            tint = Color(0xFF856404),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            "Modifying data included in your QR code can limit access to some information by authorized users. The data is already secured and only visible to verified profiles.",
            color = Color(0xFF856404),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DataCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataPrivacySettingsScreen(
    navController: NavController,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onBack: () -> Unit
) {
    var idNumberChecked by remember { mutableStateOf(true) }
    var nameChecked by remember { mutableStateOf(true) }
    var phoneChecked by remember { mutableStateOf(true) }
    var childrenChecked by remember { mutableStateOf(true) }
    var schoolStatusChecked by remember { mutableStateOf(true) }

    val defaultStates = remember {
        mapOf(
            "idNumber" to true,
            "name" to true,
            "phone" to true,
            "children" to true,
            "schoolStatus" to true
        )
    }

    val sectionVisibility = remember { mutableStateOf(MutableList(4) { false }) }

    LaunchedEffect(Unit) {
        val delayStep = 100L
        for (i in sectionVisibility.value.indices) {
            delay(delayStep)
            sectionVisibility.value = sectionVisibility.value.toMutableList().also {
                it[i] = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data & Privacy Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Section 0: Warning Section
                    AnimatedVisibility(
                        visible = sectionVisibility.value[0],
                        enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(
                            initialOffsetY = { it / 5 },
                            animationSpec = tween(durationMillis = 200)
                        )
                    ) {
                        Column {
                            WarningSection()
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // Section 1: Checkbox Title and Checkboxes
                    AnimatedVisibility(
                        visible = sectionVisibility.value[1],
                        enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(
                            initialOffsetY = { it / 5 },
                            animationSpec = tween(durationMillis = 200)
                        )
                    ) {
                        Column {
                            Text(
                                "Select the types of data included in your QR code:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            DataCheckbox(
                                label = "ID Number",
                                checked = idNumberChecked,
                                onCheckedChange = { idNumberChecked = it }
                            )
                            DataCheckbox(
                                label = "Full Name",
                                checked = nameChecked,
                                onCheckedChange = { nameChecked = it }
                            )
                            DataCheckbox(
                                label = "Phone Number",
                                checked = phoneChecked,
                                onCheckedChange = { phoneChecked = it }
                            )
                            DataCheckbox(
                                label = "Children Linked",
                                checked = childrenChecked,
                                onCheckedChange = { childrenChecked = it }
                            )
                            DataCheckbox(
                                label = "School Status (registration, attendance)",
                                checked = schoolStatusChecked,
                                onCheckedChange = { schoolStatusChecked = it }
                            )
                        }
                    }
                }

                // Section 2: Reset Button
                AnimatedVisibility(
                    visible = sectionVisibility.value[2],
                    enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(
                        initialOffsetY = { it / 5 },
                        animationSpec = tween(durationMillis = 200)
                    )
                ) {
                    Button(
                        onClick = {
                            idNumberChecked = defaultStates["idNumber"] == true
                            nameChecked = defaultStates["name"] == true
                            phoneChecked = defaultStates["phone"] == true
                            childrenChecked = defaultStates["children"] == true
                            schoolStatusChecked = defaultStates["schoolStatus"] == true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset to Recommended Defaults")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Section 3: Legal Text
                AnimatedVisibility(
                    visible = sectionVisibility.value[3],
                    enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(
                        initialOffsetY = { it / 5 },
                        animationSpec = tween(durationMillis = 200)
                    )
                ) {
                    LegalText(
                        onTermsClick = onTermsClick,
                        onPrivacyClick = onPrivacyClick
                    )
                }
            }
        }
    )
}

@Composable
fun LegalText(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "By using our services, you agree to our Terms and Conditions and Privacy Policy.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onTermsClick) {
                Text("Terms and Conditions")
            }
            Text(
                text = "and",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            TextButton(onClick = onPrivacyClick) {
                Text("Privacy Policy")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DataPrivacySettingsScreenPreview() {
    MaterialTheme {
        DataPrivacySettingsScreen(
            navController = rememberNavController(),
            onTermsClick = {},
            onPrivacyClick = {},
            onBack = {}
        )
    }
}