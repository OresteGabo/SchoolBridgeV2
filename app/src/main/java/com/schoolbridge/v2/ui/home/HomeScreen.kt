package com.schoolbridge.v2.ui.home // Adjust package as needed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.R
import com.schoolbridge.v2.data.session.UserSessionManager // Import your UserSessionManager
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerL
import com.schoolbridge.v2.ui.common.components.SpacerS

@OptIn(ExperimentalMaterial3Api::class) // Required for TopAppBar
@Composable
fun HomeScreen(
    userSessionManager: UserSessionManager,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    // Collect the currentUser StateFlow directly
    //Collect the currentUser StateFlow directly, providing an initial value of null
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

    val students: List<CurrentUser.LinkedStudent>? = currentUser?.linkedStudents

    // Determine the display name
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SchoolBridge") }, // A more general app title
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeScreenWithChildren(students =students)
            Text(
                text = "Welcome to SchoolBridge!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(16.dp))

            // Display personalized welcome if currentUser is available

            Text(
                text = "Hello, ${currentUser?.firstName}!", // Personalized greeting
                style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Email: ${currentUser?.email}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Your roles: ${currentUser?.activeRoles?.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium
            )


            Spacer(Modifier.height(32.dp))
            Text(
                text = "Explore your personalized dashboard here.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
@Composable
private fun HomeScreenWithChildren(students: List<CurrentUser.LinkedStudent>?) {

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        AppSubHeader("📚 " + t(R.string.your_children))
    }
    SpacerS()

    if (students.isNullOrEmpty()) {
        Text(text = "No children linked to this account")
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(students.size, key = { it }) {
                StudentCardProfileStyle(student = students[it])
            }
        }
    }

    SpacerL()

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        AppSubHeader("💬 " + t(R.string.recent_alerts))
    }
    SpacerS()
    AlertCardCompact(t(R.string.alert_midterm_exams))
    AlertCardCompact(t(R.string.alert_uniform_inspection))

    SpacerL()

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        AppSubHeader("📅 " + t(R.string.upcoming_events))
    }
    SpacerS()
    EventCardCompact(t(R.string.event_meeting), "June 10")
    EventCardCompact(t(R.string.event_sports_day), "June 20")
}

@Composable
fun AlertCardCompact(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Accent bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.error,
                        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    )
            )
            // Content
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun EventCardCompact(title: String, date: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Accent bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
@Composable
fun StudentCardProfileStyle(student: CurrentUser.LinkedStudent) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(280.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Side Accent Bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(12.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile image or fallback
                /*if (student.profileImageRes != null) {
                    Image(
                        painter = painterResource(id = student.profileImageRes),
                        contentDescription = "Student Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text= student.lastName.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }*/
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text= student.firstName.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${student.firstName} ${student.lastName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Reg No: ${student.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "DOB: 25/06/1995 }",//text = "DOB: ${student.dabtOfBirth}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}