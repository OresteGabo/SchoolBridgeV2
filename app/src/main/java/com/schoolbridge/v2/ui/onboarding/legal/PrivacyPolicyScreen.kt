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
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
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

            SectionHeader("1. Introduction")
            AppParagraph("Your privacy is important to us. This Privacy Policy explains how we collect, use, and protect your personal information when you use SchoolBridge.")

            SectionHeader("2. What We Collect")
            AppParagraph("We may collect the following information:")
            FeatureBullet("Personal identification (e.g., name, national ID)")
            FeatureBullet("Linked child/student details")
            FeatureBullet("Usage analytics and interaction data")
            FeatureBullet("Device and technical data (e.g., OS, app version)")

            SectionHeader("3. How We Use Your Data")
            AppParagraph("We use your data to:")
            FeatureBullet("Provide school communication and management services")
            FeatureBullet("Personalize and improve your experience")
            FeatureBullet("Resolve issues and support user requests")
            FeatureBullet("Ensure security and prevent misuse")

            SectionHeader("4. What We Don't Do With Your Data")
            AppParagraph("We strictly do NOT:")
            FeatureBullet("Sell your data to advertisers or third parties")
            FeatureBullet("Use your data for targeted ads or marketing")
            FeatureBullet("Share student or parent data without consent")
            FeatureBullet("Track your activity across other apps or platforms")

            SectionHeader("5. Data Sharing")
            AppParagraph("We may share limited data with:")
            FeatureBullet("Your child’s school or educational institution")
            FeatureBullet("Trusted providers that support our services (e.g., hosting, analytics)")
            AppParagraph("All shared data is strictly governed by confidentiality agreements.")

            SectionHeader("6. Data Security")
            AppParagraph("We use industry-standard encryption and security practices to protect your information. Despite this, no system is completely immune to risk.")

            SectionHeader("7. Data Retention")
            AppParagraph("We retain your data only as long as necessary to provide services. If your account becomes inactive, we may delete or anonymize the data after a set period.")

            SectionHeader("8. Your Rights and Control")
            AppParagraph("You have the right to:")
            FeatureBullet("Access your personal information")
            FeatureBullet("Correct inaccurate or outdated data")
            FeatureBullet("Request deletion of your account or associated data")
            FeatureBullet("Withdraw consent at any time")

            SectionHeader("9. Children’s Privacy")
            AppParagraph("We do not knowingly collect information directly from children. All child-related data is provided and managed by parents or legal guardians.")

            SectionHeader("10. Changes to This Policy")
            AppParagraph("We may occasionally update this policy. You’ll be notified of major changes through the app or email.")

            SectionHeader("11. Contact Us")
            AppParagraph("For questions or data-related concerns, contact us at:")
            AppParagraph("📧 support@schoolbridge.example")

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Thank you for trusting SchoolBridge.",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}