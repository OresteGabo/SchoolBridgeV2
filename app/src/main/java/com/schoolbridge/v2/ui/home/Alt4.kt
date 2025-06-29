package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.nativeCanvas
//import android.graphics.Paint
//import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.schoolbridge.v2.R

// Re-using dummy data classes from previous examples
// data class _CurrentUser(val lastName: String = "Smith", val gender: _Gender = _Gender.MALE)
// enum class _Gender { MALE, FEMALE, OTHER }
// data class _Course(val name: String, val iconResId: Int, val progress: Float)
// data class _Alt3_CourseCategory(val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)


// Custom Imigongo-inspired Colors
val ImigongoDarkBrown = Color(0xFF3A2B20) // Deep, earthy brown
val ImigongoRustRed = Color(0xFF8A3324)   // Rustic red
val ImigongoOchre = Color(0xFFC78C30)     // Golden ochre
val ImigongoCream = Color(0xFFF7F1E7)    // Off-white/cream
val ImigongoCharcoal = Color(0xFF202020)  // Almost black

private val ImigongoLightColorScheme = lightColorScheme(
    primary = ImigongoRustRed,
    onPrimary = ImigongoCream,
    primaryContainer = ImigongoOchre.copy(alpha = 0.3f), // Lighter, patterned container
    onPrimaryContainer = ImigongoDarkBrown,
    secondary = ImigongoOchre,
    onSecondary = ImigongoDarkBrown,
    secondaryContainer = ImigongoCream,
    onSecondaryContainer = ImigongoDarkBrown,
    tertiary = ImigongoCharcoal,
    onTertiary = ImigongoCream,
    background = ImigongoCream, // Main background
    onBackground = ImigongoDarkBrown,
    surface = ImigongoCream, // Card backgrounds
    onSurface = ImigongoDarkBrown,
    surfaceVariant = ImigongoCream.copy(alpha = 0.7f), // Lighter card/component backgrounds
    onSurfaceVariant = ImigongoDarkBrown,
    outline = ImigongoRustRed.copy(alpha = 0.5f), // Borders
    error = Color(0xFFD32F2F), // Standard error
    onError = Color.White
)

private val ImigongoDarkColorScheme = darkColorScheme(
    primary = ImigongoRustRed,
    onPrimary = ImigongoCream,
    primaryContainer = ImigongoDarkBrown.copy(alpha = 0.7f),
    onPrimaryContainer = ImigongoCream,
    secondary = ImigongoOchre,
    onSecondary = ImigongoDarkBrown,
    secondaryContainer = ImigongoCharcoal.copy(alpha = 0.7f),
    onSecondaryContainer = ImigongoOchre,
    tertiary = ImigongoCream,
    onTertiary = ImigongoCharcoal,
    background = ImigongoCharcoal,
    onBackground = ImigongoCream,
    surface = ImigongoDarkBrown,
    onSurface = ImigongoCream,
    surfaceVariant = ImigongoCharcoal.copy(alpha = 0.7f),
    onSurfaceVariant = ImigongoOchre,
    outline = ImigongoRustRed.copy(alpha = 0.5f),
    error = Color(0xFFEF9A9A),
    onError = Color(0xFFB71C1C)
)

// Custom Imigongo-inspired Shapes
val ImigongoShapes = Shapes(
    small = CutCornerShape(8.dp),      // Used for buttons, small elements
    medium = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 4.dp, bottomStart = 4.dp), // Used for cards, etc.
    large = CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp, topEnd = 8.dp, bottomStart = 8.dp) // Used for main hero section
)

val ImigongoTypography = Typography(
    // Retain standard readability but adjust colors/weights to fit theme
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = ImigongoRustRed
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = ImigongoRustRed
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        color = ImigongoDarkBrown
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = ImigongoDarkBrown
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = ImigongoDarkBrown
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = ImigongoDarkBrown
    )
    // Inherit other styles from MaterialTheme default or define explicitly if needed
)


@Composable
fun ImigongoTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ImigongoDarkColorScheme else ImigongoLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ImigongoTypography,
        shapes = ImigongoShapes, // Apply the custom shapes here
        content = content
    )
}

// Dummy resource IDs for preview. Replace with your actual drawables.
object _Imigongo_DummyResources {
    // IMPORTANT: Make sure you have these drawables in your res/drawable folder!
    val user_profile_icon = R.drawable.ic_launcher_foreground // <--- YOUR APP PACKAGE HERE
    val banner_art_abstract = R.drawable.ic_bridge // Use a generic placeholder, ideally abstract art
}


// --- Pattern Drawing Composable (Alpha Reduced) ---
@Composable
fun ImigongoBackgroundPattern(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val patternSize = 80.dp.toPx()
        val numRows = (size.height / patternSize).toInt() + 2
        val numCols = (size.width / patternSize).toInt() + 2

        val strokeWidth = 2.dp.toPx()
        // No shadow for extreme subtlety to avoid "noticeable" effect
        val paint = Paint().apply {
            this.color = color //color.toArgb()
            this.style = PaintingStyle.Stroke //Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            this.strokeCap = StrokeCap.Round//Paint.Cap.ROUND
            this.isAntiAlias = true
        }

        drawIntoCanvas { canvas ->
            for (row in -1..numRows) {
                for (col in -1..numCols) {
                    val offsetX = col * patternSize
                    val offsetY = row * patternSize

                    // Draw a simplified Imigongo-like zig-zag/triangle motif
                    val path = Path().apply {
                        moveTo(offsetX, offsetY + patternSize * 0.5f)
                        lineTo(offsetX + patternSize * 0.5f, offsetY)
                        lineTo(offsetX + patternSize, offsetY + patternSize * 0.5f)
                        lineTo(offsetX + patternSize * 0.5f, offsetY + patternSize)
                        close()
                    }
                    canvas.drawPath(path, paint) // Use Android Path for more control

                    // Draw rotated version for complexity
                    val path2 = Path().apply {
                        moveTo(offsetX + patternSize * 0.5f, offsetY)
                        lineTo(offsetX + patternSize, offsetY + patternSize * 0.5f)
                        lineTo(offsetX + patternSize * 0.5f, offsetY + patternSize)
                        lineTo(offsetX, offsetY + patternSize * 0.5f)
                        close()
                    }
                    canvas.drawPath(path2, paint)
                }
            }
        }
    }
}


// --- Standalone Home Page Composable (Imigongo) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun _Imigongo_StandaloneEducationHomePage(currentUser: _CurrentUser = _CurrentUser()) {
    ImigongoTheme { // Apply the custom Imigongo theme
        Scaffold(
            topBar = {
                _Imigongo_TopAppBarSection(currentUser = currentUser)
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // IMPORTANT CHANGE HERE: Much lower alpha for the background pattern
                    ImigongoBackgroundPattern(
                        modifier = Modifier.matchParentSize(),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.01f) // VERY SUBTLE ALPHA
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        _Imigongo_HeroSection(currentUser = currentUser)
                        Spacer(modifier = Modifier.height(24.dp))
                        _Imigongo_SectionHeader(title = "Your Learning Journey")
                        _Imigongo_CourseProgressGrid(
                            courses = listOf(
                                _Course("Math Fund.", android.R.drawable.ic_dialog_info, 0.75f),
                                _Course("Bio Basics", android.R.drawable.ic_dialog_info, 0.50f),
                                _Course("World Hist.", android.R.drawable.ic_dialog_info, 0.90f),
                                _Course("Lit. Studies", android.R.drawable.ic_dialog_info, 0.30f)
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        _Imigongo_SectionHeader(title = "Explore New Paths")
                        _Imigongo_ExploreCategories(
                            categories = listOf(
                                _Alt3_CourseCategory("STEM", Icons.Filled.School),
                                _Alt3_CourseCategory("Arts", Icons.Filled.Book),
                                _Alt3_CourseCategory("Language", Icons.Filled.Book),
                                _Alt3_CourseCategory("Life Skills", Icons.Filled.Book)
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        )
    }
}

// --- Top App Bar (Imigongo) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _Imigongo_TopAppBarSection(currentUser: _CurrentUser) {
    TopAppBar(
        title = {
            Text("SchoolBridge", style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface))
        },
        navigationIcon = {
            IconButton(onClick = { /* Open drawer/profile */ }) {
                Icon(
                    Icons.Filled.Person, // Using standard icon for simplicity, could be custom
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Search */ }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = { /* Notifications */ }) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp) // More pronounced
        )
    )
}

// --- Hero Section (Imigongo) ---
@Composable
private fun _Imigongo_HeroSection(currentUser: _CurrentUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large, // Imigongo-inspired shape
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Muraaho, ${currentUser.lastName}!", // "Hello" in Kinyarwanda
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Let your knowledge flow like the Akagera.", // Cultural reference
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Go to personalized journey */ },
                shape = MaterialTheme.shapes.small, // Smaller cut corners for button
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Start Learning")
                Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            }
        }
    }
}

// --- Section Header (Imigongo Style) ---
@Composable
private fun _Imigongo_SectionHeader(title: String) {
    val triangleColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        // Optional: A small Imigongo-inspired decorative element
        Canvas(modifier = Modifier.size(24.dp)) {

            drawPath(Path().apply {
                moveTo(size.width * 0.2f, 0f)
                lineTo(size.width * 0.8f, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }, triangleColor)
        }
    }
}

// --- Course Progress Grid (Imigongo) ---
@Composable
private fun _Imigongo_CourseProgressGrid(courses: List<_Course>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Simple grid-like layout for cards
        courses.chunked(2).forEach { rowCourses ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowCourses.forEach { course ->
                    _Imigongo_CourseProgressCard(course = course, modifier = Modifier.weight(1f))
                }
                if (rowCourses.size == 1) {
                    Spacer(modifier = Modifier.weight(1f)) // Fill remaining space if odd number
                }
            }
        }
    }
}

@Composable
private fun _Imigongo_CourseProgressCard(course: _Course, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(140.dp),
        shape = MaterialTheme.shapes.medium, // Imigongo-inspired shape
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Progress Indicator (Imigongo-inspired)
            _Imigongo_LinearProgressIndicator(
                progress = course.progress,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${(course.progress * 100).toInt()}% Done",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun _Imigongo_LinearProgressIndicator(progress: Float, modifier: Modifier = Modifier) {
    val barColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    Canvas(modifier = modifier.height(8.dp)) {
        val width = size.width
        val height = size.height

        val segmentLength = height * 2 // Define segment length based on height for a chunky feel

        // Draw track
        drawLine(
            color = trackColor,
            start = Offset(0f, height / 2f),
            end = Offset(width, height / 2f),
            strokeWidth = height,
            cap = StrokeCap.Round
        )

        // Draw progress with segmented line effect
        drawLine(
            color = barColor,
            start = Offset(0f, height / 2f),
            end = Offset(width * progress, height / 2f),
            strokeWidth = height,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(segmentLength, segmentLength), 0f)
        )
    }
}

// --- Explore Categories (Imigongo) ---
@Composable
private fun _Imigongo_ExploreCategories(categories: List<_Alt3_CourseCategory>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            _Imigongo_CategoryPill(category = category)
        }
    }
}

@Composable
private fun _Imigongo_CategoryPill(category: _Alt3_CourseCategory) {
    Surface(
        modifier = Modifier.clickable { /* Handle category click */ },
        shape = MaterialTheme.shapes.small, // Small cut corners
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

// --- Previews (Imigongo Theme) ---
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun _Imigongo_EducationHomePagePreview() {
    ImigongoTheme(darkTheme = false) {
        _Imigongo_StandaloneEducationHomePage(
            currentUser = _CurrentUser(lastName = "Umuhoza", gender = _Gender.FEMALE)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun _Imigongo_EducationHomePageDarkPreview() {
    ImigongoTheme(darkTheme = true) {
        _Imigongo_StandaloneEducationHomePage(
            currentUser = _CurrentUser(lastName = "Umuhoza", gender = _Gender.FEMALE)
        )
    }
}