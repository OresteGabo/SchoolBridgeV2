package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample data for alerts
    val alertMessages = remember {
        listOf(
            "Midterm exams are scheduled for next week.",
            "Uniform inspection will be conducted tomorrow.",
            "Fee deadline for this semester is approaching.",
            // Add more alert messages as needed
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alerts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            itemsIndexed(alertMessages) { index, message ->
                AlertCardCompact(
                    message = message,
                    index = index
                )
            }
        }
    }
}
