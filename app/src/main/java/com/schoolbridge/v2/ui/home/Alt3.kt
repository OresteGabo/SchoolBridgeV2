package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.ui.theme.AppPalette
import com.schoolbridge.v2.ui.theme.Contrast
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme

// Re-using dummy data classes from the previous examples for context
// Dummy data classes for preview purposes
// data class _CurrentUser(val lastName: String = "Smith", val gender: _Gender = _Gender.MALE)
// enum class _Gender { MALE, FEMALE, OTHER }
// data class _Course(val name: String, val iconResId: Int, val progress: Float)

// New dummy data for this proposal
data class _Alt3_CourseCategory(val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

// Dummy resource IDs for preview. Replace with your actual drawables.
object _Alt3_DummyResources {
    val banner_image = R.drawable.ic_bridge // Placeholder banner image
}

// --- Standalone Home Page Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun _Alt3_StandaloneEducationHomePage(currentUser: _CurrentUser = _CurrentUser()) {
    Scaffold(
        topBar = {
            _Alt3_TopAppBarSection(currentUser = currentUser)
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
                _Alt3_GreetingBanner(currentUser = currentUser)
                Spacer(modifier = Modifier.height(24.dp))
                _Alt3_CourseCategoriesSection(
                    categories = listOf(
                        _Alt3_CourseCategory("Mathematics", Icons.Filled.Book),
                        _Alt3_CourseCategory("Science", Icons.Filled.Book),
                        _Alt3_CourseCategory("History", Icons.Filled.Book),
                        _Alt3_CourseCategory("Literature", Icons.Filled.Book)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                _Alt3_CourseCarouselSection(
                    courses = listOf(
                        _Course("Algebra I", android.R.drawable.ic_dialog_info, 0.6f),
                        _Course("Biology 101", android.R.drawable.ic_dialog_info, 0.8f),
                        _Course("US History", android.R.drawable.ic_dialog_info, 0.4f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

// --- Top App Bar Section (Alternative 3) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _Alt3_TopAppBarSection(currentUser: _CurrentUser) {
    TopAppBar(
        title = { Text("SchoolBridge", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { /* Handle menu click */ }) {
                Icon(Icons.Filled.Person, contentDescription = "Profile")
            }
        },
        actions = {
            IconButton(onClick = { /* Handle rewards click */ }) {
                Icon(Icons.Filled.Star, contentDescription = "Rewards")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        )
    )
}

// --- Greeting Banner ---
@Composable
private fun _Alt3_GreetingBanner(currentUser: _CurrentUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(200.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = _Alt3_DummyResources.banner_image), // Placeholder
                contentDescription = "Welcome Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.3f
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome, ${currentUser.lastName}!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Explore personalized learning paths.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Button(onClick = { /* Handle explore click */ }) {
                    Text("Explore Now")
                }
            }
        }
    }
}

// --- Course Categories Section ---
@Composable
private fun _Alt3_CourseCategoriesSection(categories: List<_Alt3_CourseCategory>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Course Categories",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(categories) { category ->
                _Alt3_CategoryCard(category = category)
            }
        }
    }
}

@Composable
private fun _Alt3_CategoryCard(category: _Alt3_CourseCategory) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable { /* Handle category click */ },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Course Carousel Section ---
@Composable
private fun _Alt3_CourseCarouselSection(courses: List<_Course>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Popular Courses",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(courses) { course ->
                _Alt3_CourseCard(course = course)
            }
        }
    }
}

@Composable
private fun _Alt3_CourseCard(course: _Course) {
    Card(
        modifier = Modifier.width(200.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = course.iconResId), // Placeholder
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop
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
                progress = { course.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(course.progress * 100).toInt()}% Complete",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun _Alt3_EducationHomePagePreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF9C27B0), // Purple primary
            secondary = Color(0xFF00BCD4), // Cyan secondary
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            surfaceVariant = Color(0xFFF0F2F5)
        ),
        //typography = Typography, // Re-use the typography from the previous MD3 example
        shapes = Shapes // Re-use the shapes from the previous MD3 example
    ) {
        _Alt3_StandaloneEducationHomePage(
            currentUser = _CurrentUser(lastName = "Garcia", gender = _Gender.FEMALE)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun _Alt3_EducationHomePageDarkPreview() {
    /*MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFCE93D8),
            secondary = Color(0xFF80DEEA),
            background = Color(0xFF212121),
            surface = Color(0xFF303030),
            surfaceVariant = Color(0xFF424242)
        ),
        //typography = Typography,
        shapes = Shapes
    ) {
        _Alt3_StandaloneEducationHomePage(
            currentUser = _CurrentUser(lastName = "Garcia", gender = _Gender.FEMALE)
        )
    }*/
    SchoolBridgeV2Theme(
        isDarkTheme = false,
        palette = AppPalette.GOLDEN,
        contrast = Contrast.NORMAL,

        ){
        _Alt3_StandaloneEducationHomePage(
            currentUser = _CurrentUser(lastName = "Garcia", gender = _Gender.FEMALE)
        )
    }
}