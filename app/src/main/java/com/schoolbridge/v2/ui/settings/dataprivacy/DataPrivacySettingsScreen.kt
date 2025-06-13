package com.schoolbridge.v2.ui.settings.dataprivacy


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.schoolbridge.v2.ui.onboarding.legal.LegalText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataPrivacySettingsScreen(
    navController: NavController,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onBack: () -> Unit
) {
    // Simulated state for checkboxes
    var idNumberChecked by remember { mutableStateOf(true) }
    var nameChecked by remember { mutableStateOf(true) }
    var phoneChecked by remember { mutableStateOf(true) }
    var childrenChecked by remember { mutableStateOf(true) }
    var schoolStatusChecked by remember { mutableStateOf(true) }
    //var navController = rememberNavController()

    // Default values to reset to
    val defaultStates = mapOf(
        "idNumber" to true,
        "name" to true,
        "phone" to true,
        "children" to true,
        "schoolStatus" to true
    )

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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                WarningSection()

                Spacer(modifier = Modifier.height(24.dp))

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
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        // Reset all checkboxes to defaults
                        idNumberChecked = defaultStates["idNumber"] != false
                        nameChecked = defaultStates["name"] != false
                        phoneChecked = defaultStates["phone"] != false
                        childrenChecked = defaultStates["children"] != false
                        schoolStatusChecked = defaultStates["schoolStatus"] != false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset to Recommended Defaults")
                }
                Spacer(modifier = Modifier.height(48.dp))

                // Add LegalText here
                LegalText(
                    onTermsClick = onTermsClick,
                    onPrivacyClick = onPrivacyClick
                )
            }
        }
    )
}

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
            "⚠️ Modifying data included in your QR code can limit access to some information by authorized users. The data is already secured and only visible to verified profiles.",
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