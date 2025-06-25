package com.schoolbridge.v2.ui.onboarding.legal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolbridge.v2.ui.components.AppParagraph
import com.schoolbridge.v2.ui.components.FeatureBullet
import com.schoolbridge.v2.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms of Service") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Effective Date: May 1, 2025",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("1. Acceptance of Terms")
            AppParagraph("By accessing or using the SchoolBridge application, you agree to be bound by these Terms of Service. If you do not agree, do not use the app.")

            SectionHeader("2. Who May Use the Service")
            AppParagraph("You must be a parent, legal guardian, school staff, or student authorized to access your school’s communication platform. Unauthorized use is strictly prohibited.")

            SectionHeader("3. Your Responsibilities")
            FeatureBullet("Provide accurate and truthful information")
            FeatureBullet("Use the app in a respectful and lawful manner")
            FeatureBullet("Maintain the confidentiality of your login credentials")
            FeatureBullet("Report misuse, bugs, or suspicious activity promptly")

            SectionHeader("4. User Conduct")
            AppParagraph("You agree not to:")
            FeatureBullet("Use the app to send spam or inappropriate content")
            FeatureBullet("Harass or threaten other users")
            FeatureBullet("Attempt to reverse-engineer or tamper with the app")
            FeatureBullet("Upload malware, viruses, or harmful code")

            SectionHeader("5. Content Ownership")
            AppParagraph("All intellectual property, including but not limited to code, design, and branding, belongs to SchoolBridge or its partners. You may not reproduce or distribute it without permission.")

            SectionHeader("6. Communication and Messaging")
            AppParagraph("You agree that communications via the app may be stored, archived, or monitored to ensure service quality and security. Misuse of messaging features may result in suspension.")

            SectionHeader("7. Service Availability")
            AppParagraph("We strive for high availability, but the app may be temporarily unavailable due to maintenance or technical issues. We are not liable for interruptions beyond our control.")

            SectionHeader("8. Termination")
            AppParagraph("We reserve the right to suspend or terminate your access if you violate these terms, misuse the service, or pose a risk to the platform’s security or integrity.")

            SectionHeader("9. Changes to Terms")
            AppParagraph("We may update these terms at any time. Material changes will be communicated through the app or email. Continued use after changes constitutes acceptance.")

            SectionHeader("10. Limitation of Liability")
            AppParagraph("We are not liable for damages arising from the use or inability to use the app, including loss of data, service outages, or third-party conduct.")

            SectionHeader("11. Indemnification")
            AppParagraph("You agree to indemnify and hold harmless SchoolBridge and its affiliates from any claims, losses, or damages resulting from your violation of these terms or misuse of the app.")

            SectionHeader("12. Governing Law")
            AppParagraph("These terms are governed by the laws of the Republic of Rwanda. Any disputes shall be resolved through competent courts in Rwanda.")

            SectionHeader("13. Contact Us")
            AppParagraph("If you have questions about these terms, reach out to us:")
            AppParagraph("📧 legal@schoolbridge.example")

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Thank you for using SchoolBridge responsibly.",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}