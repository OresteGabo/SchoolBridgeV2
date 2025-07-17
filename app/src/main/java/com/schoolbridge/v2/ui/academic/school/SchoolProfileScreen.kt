package com.schoolbridge.v2.ui.academic.school

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.schoolbridge.v2.R
import com.schoolbridge.v2.domain.school.School
import com.schoolbridge.v2.domain.school.SchoolLevel

import com.schoolbridge.v2.util.sampleSchool
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolProfileScreen(school: School) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                scope.launch { bottomSheetState.hide() }
            },
            sheetState = bottomSheetState
        ) {
            BottomSheetContent(
                onEnroll = {
                    Toast.makeText(context, "Enrolled in ${school.name}", Toast.LENGTH_SHORT).show()
                    showBottomSheet = false
                    scope.launch { bottomSheetState.hide() }
                },
                onFavorite = {
                    Toast.makeText(context, "${school.name} favorited", Toast.LENGTH_SHORT).show()
                    showBottomSheet = false
                    scope.launch { bottomSheetState.hide() }
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bridge),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            VerifiedBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = school.logoUrl,
                        error = painterResource(id = R.drawable.green_hills)
                    ),
                    contentDescription = "School Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }

        Text(
            text = school.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoItem("⭐", "${school.rating} Rating")
            VerticalDivider()
            InfoItem("🎓", "${school.studentsCount} Students")
            VerticalDivider()
            InfoItem("📅", "Since ${school.establishedYear}")
            VerticalDivider()
            InfoItem("🏷️", school.type)
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Location & Contact")
        InfoItem("📍", "District: ${school.district.title}, Sector: ${school.sector.title}")
        InfoItem("📞", "Phone: ${school.contactPhone}")
        InfoItem("✉️", "Email: ${school.contactEmail}")
        if (!school.websiteUrl.isNullOrBlank()) {
            InfoItem("🌐", "Website: ${school.websiteUrl}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Education Offered")
        school.educationLevelsOffered.forEach { level ->
            when (level) {
                is SchoolLevel.NurseryLevel -> InfoItem("👶", "Nursery: ${level.name}")
                is SchoolLevel.PrimaryLevel -> InfoItem("📘", "Primary: ${level.name}")
                is SchoolLevel.OLevel -> InfoItem("📗", "O-Level: ${level.name}")
                is SchoolLevel.ALevel -> {
                    InfoItem("📕", "A-Level: ${level.name}")
                    level.section?.let {
                        InfoItem("📚", "Section: ${it.abbrevName} - ${it.name}")
                    }
                }
                is SchoolLevel.TVETLevel -> InfoItem("🔧", "TVET: ${level.name} (${level.trade ?: "General"})")
                is SchoolLevel.UniversityLevel -> InfoItem("🎓", "University: ${level.name}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (school.bankAccounts.isNotEmpty()) {
            SectionTitle("Payment Info")
            school.bankAccounts.forEach { _ ->
                InfoItem("🏦", "bank 000000000000000")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showBottomSheet = true
                scope.launch { bottomSheetState.show() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("More Options")
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("Upcoming Events")
        EventCard("🎤 Parent-Teacher Meeting", "July 30, 2025")
        EventCard("🎉 Cultural Day", "August 5, 2025")

        SectionTitle("School Documents")
        DocumentCard("🧾 Fee Structure Term 3", "PDF")

        Spacer(Modifier.height(24.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedButton(onClick = {}) { Text("Contact") }
            OutlinedButton(onClick = {}) { Text("Favorite") }
            OutlinedButton(onClick = {}) { Text("Report") }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun InfoItem(icon: String, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
        Text(label, fontSize = 14.sp)
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(Color.LightGray)
    )
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                RoundedCornerShape(percent = 50)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = "Verified",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Verified",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun BottomSheetContent(onEnroll: () -> Unit, onFavorite: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEnroll, modifier = Modifier.fillMaxWidth()) {
            Text("Enroll")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onFavorite, modifier = Modifier.fillMaxWidth()) {
            Text("Add to Favorites")
        }
    }
}

@Composable
fun EventCard(title: String, date: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(date, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun DocumentCard(title: String, type: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Description, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            Text(type, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Preview
@Composable
private fun SchoolProfilePrev() {
    SchoolProfileScreen(
        school = sampleSchool
    )
}


@Composable
fun InfoItemHorizontal(icon: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 20.sp)
        Text(text = label, fontSize = 14.sp)
    }
}
/*
@Composable
fun InfoItemVertical(icon: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(text = icon, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
        Text(text = label, fontSize = 14.sp)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(Color.LightGray.copy(alpha = 0.6f))
    )
}
*/