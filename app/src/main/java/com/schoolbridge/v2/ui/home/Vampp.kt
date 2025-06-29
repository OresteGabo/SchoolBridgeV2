package com.schoolbridge.v2.ui.home
import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Assuming you'll have resources like R.drawable.course_icon_math
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolbridge.v2.ui.theme.AppPalette
import com.schoolbridge.v2.ui.theme.Contrast
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme

// Dummy data classes for preview purposes
data class _CurrentUser(val lastName: String = "Smith", val gender: _Gender = _Gender.MALE)
enum class _Gender { MALE, FEMALE, OTHER }
data class _Course(val name: String, val iconResId: Int, val progress: Float) // progress from 0.0 to 1.0

// Dummy resource IDs for preview. Replace with your actual drawables.
// In a real app, these would come from your R.drawable.
object _DummyResources {
    val course_icon_math = R.drawable.ic_dialog_info // Placeholder
    val course_icon_science = R.drawable.ic_menu_agenda // Placeholder
    val course_icon_history = R.drawable.ic_menu_gallery // Placeholder
    val course_icon_literature = R.drawable.ic_menu_help // Placeholder
    val student_avatar_male = R.drawable.sym_action_chat // Placeholder
    val student_avatar_female = R.drawable.sym_action_email // Placeholder
}

// --- Standalone Home Page Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun _StandaloneEducationHomePage(currentUser: _CurrentUser = _CurrentUser()) {
    Scaffold(
        topBar = {
            _TopAppBarSection()
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                _WelcomeHeroSection(currentUser = currentUser)
                Spacer(modifier = Modifier.height(24.dp))
                _SearchBarSection()
                Spacer(modifier = Modifier.height(24.dp))
                _QuickActionsSection()
                Spacer(modifier = Modifier.height(24.dp))
                _CurrentCoursesSection(
                    courses = listOf(
                        _Course("Mathematics I", _DummyResources.course_icon_math, 0.75f),
                        _Course("Biology Basics", _DummyResources.course_icon_science, 0.50f),
                        _Course("World History", _DummyResources.course_icon_history, 0.90f)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                _UpcomingEventsSection(
                    events = listOf(
                        "Math Exam - July 10th",
                        "Science Fair - July 15th",
                        "Parent-Teacher Meeting - July 20th"
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

// --- Top App Bar ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _TopAppBarSection() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "SchoolBridge",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            IconButton(onClick = { /* Handle notification click */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        )
    )
}

// --- Welcome Hero Section ---
@Composable
private fun _WelcomeHeroSection(currentUser: _CurrentUser) {
    val name = currentUser.lastName
    val gender = currentUser.gender
    val greetingName = when (gender) {
        _Gender.MALE -> "Mr. $name"
        _Gender.FEMALE -> "Ms. $name"
        _Gender.OTHER -> name
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Hello $greetingName 👋",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ready to learn something new today?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

// --- Search Bar Section ---
@Composable
private fun _SearchBarSection() {
    OutlinedTextField(
        value = "",
        onValueChange = { /* Handle search input */ },
        placeholder = { Text("Search courses, assignments, teachers...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

// --- Quick Actions Section ---
@Composable
private fun _QuickActionsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            _QuickActionButton("My Schedule", Icons.Filled.Notifications) // Placeholder icon
            _QuickActionButton("Grades", Icons.Filled.Notifications) // Placeholder icon
            _QuickActionButton("Assignments", Icons.Filled.Notifications) // Placeholder icon
        }
    }
}

@Composable
private fun _QuickActionButton(label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { /* Handle action */ },
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// --- Current Courses Section ---
@Composable
private fun _CurrentCoursesSection(courses: List<_Course>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Your Current Courses",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(courses) { course ->
                _CourseCard(course = course)
            }
        }
    }
}

@Composable
private fun _CourseCard(course: _Course) {
    Card(
        modifier = Modifier.width(180.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Course Icon (placeholder)
                Image(
                    painter = painterResource(id = course.iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Inside
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = course.progress,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(course.progress * 100).toInt()}% Completed",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Upcoming Events Section ---
@Composable
private fun _UpcomingEventsSection(events: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Upcoming Events",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                events.forEach { event ->
                    Text(
                        text = "• $event",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    if (events.indexOf(event) < events.size - 1) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun _EducationHomePagePreview() {
    // You'd typically wrap this in your app's theme

        SchoolBridgeV2Theme(
            isDarkTheme = false,
            palette = AppPalette.GOLDEN,
            contrast = Contrast.NORMAL,

        ){
            _StandaloneEducationHomePage(
                currentUser = _CurrentUser(lastName = "Doe", gender = _Gender.FEMALE)
            )
        }


}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun _EducationHomePageDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFBB86FC),
            secondary = Color(0xFF03DAC6),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            surfaceVariant = Color(0xFF2C2C2C)
        ),
        //typography = Typography,
        //shapes = Shapes
    ) {
        _StandaloneEducationHomePage(
            currentUser = _CurrentUser(lastName = "Doe", gender = _Gender.FEMALE)
        )
    }
}