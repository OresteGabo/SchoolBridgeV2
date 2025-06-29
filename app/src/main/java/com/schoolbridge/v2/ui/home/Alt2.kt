package com.schoolbridge.v2.ui.home
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // IMPOTANT: This is used for R.drawable.your_icon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.ui.theme.AppPalette
import com.schoolbridge.v2.ui.theme.Contrast
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

// --- RE-USED CLASSES FROM PREVIOUS EXAMPLES (ENSURE THESE ARE IN YOUR PROJECT) ---
// Dummy data classes for preview purposes
data class Alt2_CurrentUser(val lastName: String = "Smith", val gender: _Gender = _Gender.MALE)
enum class Alt2_Gender { MALE, FEMALE, OTHER }
data class Alt2_Course(val name: String, val iconResId: Int, val progress: Float) // progress from 0.0 to 1.0

// Material 3 Theme definition - Reusing the ones defined earlier
val md_theme_light_primary = Color(0xFF6750A4)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFEADDFF)
val md_theme_light_onPrimaryContainer = Color(0xFF21005D)
val md_theme_light_secondary = Color(0xFF625B71)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFE8DEF8)
val md_theme_light_onSecondaryContainer = Color(0xFF1D192B)
val md_theme_light_tertiary = Color(0xFF7D5260)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFF9DEDC) // Corrected from FFD8E4 in some versions
val md_theme_light_onTertiaryContainer = Color(0xFF31111D) // Corrected from 31111D in some versions
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF1D1B20)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF1D1B20)
val md_theme_light_surfaceVariant = Color(0xFFE7E0EC)
val md_theme_light_onSurfaceVariant = Color(0xFF49454F)
val md_theme_light_outline = Color(0xFF7A757F)
val md_theme_light_inverseOnSurface = Color(0xFFF5EFF7)
val md_theme_light_inverseSurface = Color(0xFF322F35)
val md_theme_light_inversePrimary = Color(0xFFD0BCFF)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF6750A4)
val md_theme_light_outlineVariant = Color(0xFFCAC4D0)
val md_theme_light_scrim = Color(0xFF000000)


private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

// You might also need the DarkColorScheme and Typography/Shapes definitions
// from the first MD3 example, as they are used in the previews here.
// I'm omitting them for brevity, assuming you have them already.



val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

// New dummy data for this proposal
data class _Alt2_Assignment(val title: String, val dueDate: String, val course: String)
data class _Alt2_Activity(val type: String, val description: String, val time: String)
data class _Alt2_NextClass(val name: String, val time: LocalTime, val room: String)

// Dummy resource IDs for preview. Replace with your actual drawables.
// IMPORTANT CHANGE HERE: Referencing your new local drawable
object _Alt2_DummyResources {
    val user_avatar = R.drawable.ic_bridge // <--- CHANGE `com.example.yourapp` to your actual package name
    val class_icon_generic = android.R.drawable.ic_menu_today // This system drawable is generally safe for previews
}


// --- Standalone Home Page Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun _Alt2_StandaloneEducationHomePage(currentUser: _CurrentUser = _CurrentUser()) {
    Scaffold(
        topBar = {
            _Alt2_TopAppBarSection(currentUser = currentUser)
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
                Spacer(modifier = Modifier.height(16.dp))

                _Alt2_NextClassCard(
                    nextClass = _Alt2_NextClass("Algebra II", LocalTime.of(10, 30), "Room 201")
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    _Alt2_DashboardMetricCard(
                        title = "Assignments Due",
                        value = "3",
                        icon = Icons.Filled.Assignment,
                        modifier = Modifier.weight(1f)
                    )
                    _Alt2_DashboardMetricCard(
                        title = "New Grade",
                        value = "A- (Math)",
                        icon = Icons.Filled.Grade,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                _Alt2_DueAssignmentsSection(
                    assignments = listOf(
                        _Alt2_Assignment("Chapter 5 Quiz", "Tomorrow", "Algebra II"),
                        _Alt2_Assignment("Lab Report", "July 5th", "Biology Basics"),
                        _Alt2_Assignment("Essay Draft", "July 7th", "World History")
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                _Alt2_RecentActivitySection(
                    activities = listOf(
                        _Alt2_Activity("Grade Posted", "New grade for History received", "Just now"),
                        _Alt2_Activity("Announcement", "School will be closed next Monday", "1 hour ago"),
                        _Alt2_Activity("Assignment Due", "Algebra II quiz due tomorrow", "Yesterday")
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

// --- Top App Bar Section (Alternative 2) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _Alt2_TopAppBarSection(currentUser: _CurrentUser) {
    TopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${currentUser.lastName}!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* Open navigation drawer */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Navigation Menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Handle notifications */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.width(8.dp))
            // User Avatar
            Image(
                // THIS IS THE LINE THAT LIKELY CAUSED THE NPE, NOW REFERENCING YOUR LOCAL DRAWABLE
                painter = painterResource(id = _Alt2_DummyResources.user_avatar),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp) // Higher elevation for a more prominent bar
        )
    )
}

// --- Next Class Card ---
@Composable
private fun _Alt2_NextClassCard(nextClass: _Alt2_NextClass) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Next Class:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
                Text(
                    text = nextClass.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${nextClass.time.format(DateTimeFormatter.ofPattern("hh:mm a"))} • ${nextClass.room}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
            Icon(
                imageVector = Icons.Filled.School, // Or a more specific icon
                contentDescription = "Next Class",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

// --- Dashboard Metric Card ---
@Composable
private fun _Alt2_DashboardMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// --- Due Assignments Section ---
@Composable
private fun _Alt2_DueAssignmentsSection(assignments: List<_Alt2_Assignment>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Assignments Due Soon",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 250.dp), // Limit height to avoid excessive scrolling
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assignments) { assignment ->
                _Alt2_AssignmentItem(assignment)
            }
        }
    }
}

@Composable
private fun _Alt2_AssignmentItem(assignment: _Alt2_Assignment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Course: ${assignment.course}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Due:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = assignment.dueDate,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.error // Highlight due date
                )
            }
        }
    }
}

// --- Recent Activity Section ---
@Composable
private fun _Alt2_RecentActivitySection(activities: List<_Alt2_Activity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            activities.forEach { activity ->
                _Alt2_ActivityItem(activity)
            }
        }
    }
}

@Composable
private fun _Alt2_ActivityItem(activity: _Alt2_Activity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (activity.type) {
                    "Grade Posted" -> Icons.Filled.Grade
                    "Announcement" -> Icons.Filled.Notifications
                    "Assignment Due" -> Icons.Filled.Assignment
                    else -> Icons.Filled.CalendarToday // Default icon
                },
                contentDescription = activity.type,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(24.dp).padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.type,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = activity.time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun _Alt2_EducationHomePagePreview() {
//    MaterialTheme(
//        colorScheme = LightColorScheme, // Using the full light color scheme for consistency
//        //typography = Typography,
//        shapes = Shapes
//    ) {
//        _Alt2_StandaloneEducationHomePage(
//            currentUser = _CurrentUser(lastName = "Williams", gender = _Gender.FEMALE)
//        )
//    }

    SchoolBridgeV2Theme(
        isDarkTheme = false,
        palette = AppPalette.GOLDEN,
        contrast = Contrast.NORMAL,

        ){
        _Alt2_StandaloneEducationHomePage(
            currentUser = _CurrentUser(lastName = "Williams", gender = _Gender.FEMALE)
        )
    }
}

// //Ensure you also have the DarkColorScheme defined in your project if you use this preview
// @Preview(showBackground = true, widthDp = 360, heightDp = 800)
// @Composable
// fun _Alt2_EducationHomePageDarkPreview() {
//     MaterialTheme(
//         colorScheme = DarkColorScheme, // Using your full dark color scheme
//         typography = Typography,
//         shapes = Shapes
//     ) {
//         _Alt2_StandaloneEducationHomePage(
//             currentUser = _CurrentUser(lastName = "Williams", gender = _Gender.FEMALE)
//         )
//     }
// }