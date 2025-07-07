package com.schoolbridge.v2.ui.onboarding.follow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Simple data class for minimal school info
data class SchoolBrief(
    val id: Long,
    val name: String,
    val abbrevName: String,
    val districtName: String
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FollowSchoolsGridScreen(
    schools: List<SchoolBrief>,
    onContinue: (followedSchools: List<SchoolBrief>) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var followedSchools by remember { mutableStateOf(setOf<SchoolBrief>()) }
    var showFollowedDialog by remember { mutableStateOf(false) }

    val filteredSchools = remember(searchQuery, followedSchools) {
        schools.filter {
            it.name.contains(searchQuery, ignoreCase = true) && !followedSchools.contains(it)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Follow Schools (optional)") },
                actions = {
                    if (followedSchools.isNotEmpty()) {
                        Box(modifier = Modifier.padding(end = 16.dp)) {
                            IconButton(onClick = { showFollowedDialog = true }) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text("${followedSchools.size}")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Followed Schools"
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                onClick = { onContinue(followedSchools.toList()) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Continue",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search schools") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredSchools.isEmpty()) {
                NoResultsMessage()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSchools, key = { it.id }) { school ->
                        SchoolProfileCard(
                            school = school,
                            isFollowed = false,
                            onFollowToggle = {
                                followedSchools = followedSchools + school
                            }
                        )
                    }
                }
            }
        }

        if (showFollowedDialog) {
            AlertDialog(
                onDismissRequest = { showFollowedDialog = false },
                title = { Text("Followed Schools") },
                text = {
                    if (followedSchools.isEmpty()) {
                        Text("You have not followed any schools yet.")
                    } else {
                        Column {
                            followedSchools.forEach { school ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(school.name, fontWeight = FontWeight.Medium)
                                    IconButton(
                                        onClick = {
                                            followedSchools = followedSchools - school
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Unfollow"
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFollowedDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun NoResultsMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "No schools match your search.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Some schools are not yet on the platform. We hope to add them soon!",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SchoolProfileCard(
    school: SchoolBrief,
    isFollowed: Boolean,
    onFollowToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFollowed) Color(0xFFD0F0C0) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            // Background icon with low alpha
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(64.dp)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = school.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = school.abbrevName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "District: ${school.districtName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onFollowToggle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Follow")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FollowSchoolsGridScreenPreview() {
    val sampleSchools = listOf(
        SchoolBrief(1, "Bright Future Academy", "BFA", "Gasabo"),
        SchoolBrief(2, "Green Valley High", "GVH", "Kicukiro"),
        SchoolBrief(3, "Riverstone College", "RSC", "Nyarugenge"),
        SchoolBrief(4, "Sunrise Technical Institute", "STI", "Gasabo"),
        SchoolBrief(5, "Mountain View Primary", "MVP", "Rwamagana"),
        SchoolBrief(6, "Lakeside University", "LSU", "Huye")
    )
    FollowSchoolsGridScreen(
        schools = sampleSchools,
        onContinue = {}
    )
}
