package com.schoolbridge.v2.ui.settings.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.components.FeatureBullet
import com.schoolbridge.v2.ui.common.components.SectionHeader


@Preview
@Composable
private fun AboutScreenPreview() {
    AboutScreen(
        onBack = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val openVersionSheet = remember { mutableStateOf(false) }

    // Version metadata
    val versions = listOf(
        VersionInfo(
            title = "V1: Umusingi",
            meaning = "“Umusingi” means foundation, the solid base of our journey.",
            releaseDate = "May 2025",
            focus = "Core infrastructure, identity, and onboarding",
            future = "Stability & groundwork for AI & automation",
            icon = Icons.Default.Construction,
            isCurrent = true
        ),
        /*
        VersionInfo(
            title = "V2: Intambwe",
            meaning = "“Intambwe” means step — progress toward reports and live updates.",
            releaseDate = "Planned: Late 2025",
            focus = "Performance, real-time sync, and academic reports",
            future = "Multi-student insights, offline mode",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            isCurrent = false
        ),
        VersionInfo(
            title = "V3: Urumuri",
            meaning = "“Urumuri” means light — guidance via AI and smart feedback.",
            releaseDate = "Planned: Early 2026",
            focus = "AI assistance, guidance, personalization",
            future = "Adaptive learning and parent-teacher analytics",
            icon = Icons.Default.LightMode,
            isCurrent = false
        )*/
    )

    if (openVersionSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { openVersionSheet.value = false },
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = versions.first().title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = versions.first().meaning,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Text("📆 Released: ${versions.first().releaseDate}")
                Text("🏠 Focus: ${versions.first().focus}")
                Spacer(Modifier.height(8.dp))
                Text("🌟 Future: ${versions.first().future}")
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { openVersionSheet.value = false }) {
                    Text("Close")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(t(R.string.app_version), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(t(R.string.about_description))
            Spacer(Modifier.height(16.dp))
            //HorizontalDivider()
            //Spacer(Modifier.height(16.dp))
            // Current Version Card
            VersionCard(version = versions.first(), onClick = { openVersionSheet.value = true })
            Spacer(Modifier.height(12.dp))

            // Future Versions
            versions.drop(1).forEach {
                VersionCard(version = it, onClick = {})
                Spacer(Modifier.height(12.dp))
            }

            //HorizontalDivider()

            // Features
            SectionHeader(t(R.string.features_title))
            Spacer(Modifier.height(8.dp))
            FeatureBullet("📨 " + t(R.string.feature_messaging))
            FeatureBullet("📊 " + t(R.string.feature_fees))
            FeatureBullet("📚 " + t(R.string.feature_linked_children))
            FeatureBullet("🧾 " + t(R.string.feature_verification))
            FeatureBullet("📍 " + t(context, R.string.feature_school_search))

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()

            // Privacy
            SectionHeader(t(R.string.privacy_title))
            Text(t(R.string.privacy_description))

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()

            // Developer Info
            SectionHeader(t(R.string.developed_by_title))
            Text(t(R.string.developed_by_description))

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()

            // Contact
            SectionHeader(t(R.string.contact_title))
            Text(t(R.string.contact_email))
            Text(t(R.string.contact_phone))
            Text(t(R.string.contact_website))

            Spacer(Modifier.height(32.dp))
            Text(
                text = t(R.string.copyright, 2025),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun VersionCard(version: VersionInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable(enabled = version.isCurrent, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(Modifier.height(120.dp)) {
            Icon(
                imageVector = version.icon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(100.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "SchoolBridge ${version.title}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = version.meaning,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

data class VersionInfo(
    val title: String,
    val meaning: String,
    val releaseDate: String,
    val focus: String,
    val future: String,
    val icon: ImageVector,
    val isCurrent: Boolean
)