package com.schoolbridge.v2.ui.settings.profile

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.schoolbridge.v2.data.session.UserSessionManager // Updated import
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.schoolbridge.v2.data.session.CurrentUser.Address
import com.schoolbridge.v2.domain.user.Gender
import com.schoolbridge.v2.domain.user.User
import kotlin.collections.forEachIndexed
import kotlin.collections.isNullOrEmpty
import android.graphics.Color as AndroidColor // Alias for Android's Color class

// Assuming these exist in your project, otherwise create them or replace with Text
// import com.schoolbridge.v2.ui.components.AppSubHeader
// import com.schoolbridge.v2.ui.components.SectionHeader


@RequiresApi(Build.VERSION_CODES.Q) // Keep this if you use Bitmap.set, createBitmap, etc.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userSessionManager: UserSessionManager, // Inject UserSessionManager
    onBack: () -> Unit // Callback for back navigation
) {
    // Get the current user from the session manager
    //val currentUser = userSessionManager.currentUser
    //Collect the currentUser StateFlow directly, providing an initial value of null
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)


    val currentOnBack by rememberUpdatedState(onBack)
    var isEditing by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    // This should ideally come from the User model or a ViewModel indicating verification status
    val userVerified = remember { mutableStateOf(true) }

    // Editable states initialized from currentUser
    var editablePhone ="250788000000"//by remember { mutableStateOf(currentUser?.phoneNumber ?: "Not provided") }
    var editableEmail by remember { mutableStateOf(currentUser?.email ?: "Not provided") }
    var editableDistrict = "gicumbi"//by remember { mutableStateOf(currentUser?.address?.district ?: "Unknown") }
    var editableSector ="kageyo"//by remember { mutableStateOf(currentUser?.address?.sector ?: "Unknown") }

    // Update editable states when currentUser changes (e.g., after login or refresh)
    LaunchedEffect(isEditing, currentUser) {
        currentUser?.let {
            /*editablePhone = it.phoneNumber ?: "Not provided"
            editableEmail = it.email ?: "Not provided"
            editableDistrict = it.address?.district ?: "Unknown"
            editableSector = it.address?.sector ?: "Unknown"*/
        }
    }

    val context = LocalContext.current // Get context for toasts

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = currentOnBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isEditing) {
                                Log.d(
                                    "ProfileScreen",
                                    "Saving changes: $editablePhone, $editableEmail, $editableDistrict, $editableSector"
                                )
                                // TODO: Call a ViewModel function here to save the updated data
                                // Example: profileViewModel.saveProfile(editablePhone, editableEmail, ...)
                                Toast.makeText(context, "Profile saved!", Toast.LENGTH_SHORT).show()
                            }
                            isEditing = !isEditing
                        }
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Save" else "Edit"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = currentUser?.profilePictureUrl, // Use actual profile picture URL
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape) // Added border for better visibility
                )
                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Column{ // Changed to Row to align name and badge
                        Text(
                            text = buildString {
                                val title = when (currentUser?.gender) {
                                    Gender.MALE -> "Mr."
                                    Gender.FEMALE -> "Mrs."
                                    else -> ""
                                }
                                append("$title ${currentUser?.firstName ?: ""} ${currentUser?.lastName ?: ""}".trim())
                            },
                            style = MaterialTheme.typography.headlineSmall
                        )
                        if (userVerified.value) {
                            Spacer(modifier = Modifier.width(8.dp))
                            VerifiedBadge()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- QR Code Section ---
            Text(
                text = "Your SchoolBridge ID",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your digital SchoolBridge ID",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This QR code can be used to verify your identity at school or via SchoolBridge services.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We ensure data access is encrypted and used only with your consent.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showQrDialog = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Show QR Code")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))


            // --- Contact Information Section ---
            // Replaced SectionHeader with Text for simpler integration
            Text(
                text = "Contact Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ProfileField("Phone Number", editablePhone, isEditing, { editablePhone = it })
            ProfileField("Email", editableEmail, isEditing, { editableEmail = it })
            ProfileReadonlyField("National ID", maskRwandaId(currentUser?.userId ?: "N/A"))

            Spacer(Modifier.height(24.dp))

            // --- Address Information Section ---
            Text(
                text = "Address Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // If district/sector are editable, use ProfileField. Otherwise, ProfileReadonlyField.
            ProfileField("District", editableDistrict, isEditing, { editableDistrict = it })
            ProfileField("Sector", editableSector, isEditing, { editableSector = it })

            Spacer(Modifier.height(24.dp))

            // --- Account Details Section ---
            Text(
                text = "Account Details",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            /*ListItem(
                headlineContent = { Text("Joined") },
                supportingContent = { Text(currentUser.dateOfBirth.toString() ?: "Date not available") }
            )*/

            Spacer(modifier = Modifier.height(16.dp))

            // --- Linked Children Section ---
            // Replaced AppSubHeader with Text for simpler integration
            Text(
                text = "Linked Children (${currentUser?.linkedStudents?.size ?: 0})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            /*if (currentUser.isParent && currentUser.parentDetails?.linkedChildren.isNullOrEmpty()) {
                Text(
                    text = "No children linked yet. Link your child to view their academic progress and more.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        currentUser.value.parentDetails?.linkedChildren?.forEachIndexed { index, child ->
                            ListItem(
                                headlineContent = { Text("${child.studentFirstName} ${child.studentLastName}") },
                                supportingContent = { Text("Student ID: ${child.studentId}") },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Child Icon",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { /* Handle click to view child details */ }
                                    .padding(horizontal = 8.dp)
                            )
                            /*if (index < (currentUser.value.parentDetails?.linkedChildren?.size ==null 0: ?)) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }*/
                        }
                    }
                }
            }*/

            // --- Verified Badge/Info ---
            if (userVerified.value) { // Show this only if verified
                Spacer(modifier = Modifier.height(24.dp))
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            Log.i("INFO_BADGE", "BADGE VERIFIED full information")
                            Toast.makeText(context, "Your account is officially verified!", Toast.LENGTH_SHORT).show()
                        },
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Your profile is verified via ID or MoMo match.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // QR Code Dialog
    if (showQrDialog && currentUser != null) {
        Dialog(onDismissRequest = { showQrDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Box {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 4.dp,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            UserQrCode(uid = "THIS IS THE UUID HERE") // Use currentUser.id
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        BridgeLockInfo(context = context)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Want to manage what data is shared?\nGo to Settings → Data & Privacy.",
                            style = MaterialTheme.typography.labelMedium,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                Toast.makeText(context, "Coming soon: Add to Google Wallet", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Wallet,
                                contentDescription = "Add to Wallet",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Wallet")
                        }
                    }
                }
            }
        }
    }
}


// These helper composables are included as they are crucial for the ProfileScreen's structure
// and utilize Material 3 components.

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
            text = "Verified Account",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun BridgeLockBadge(
    modifier: Modifier = Modifier,
    text: String = "BridgeLock™",
    onClick: (() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(percent = 50),
        modifier = modifier
            .wrapContentSize()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "BridgeLock Security",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun BridgeLockInfo(modifier: Modifier = Modifier, context: Context) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BridgeLockBadge(
            onClick = {
                Toast.makeText(context, "BridgeLock™: Data secured.", Toast.LENGTH_SHORT).show()
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Protected by BridgeLock™",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = keyboardOptions,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        } else {
            ListItem(
                headlineContent = { Text(label) },
                supportingContent = { Text(value, style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ProfileReadonlyField(
    label: String,
    value: String
) {
    ListItem(
        headlineContent = { Text(label) },
        supportingContent = { Text(value, style = MaterialTheme.typography.bodyLarge) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun UserQrCode(uid: String, modifier: Modifier = Modifier) {
    val size = 200
    val qrBitmap = remember(uid) {
        val writer = QRCodeWriter()
        Log.d("UserQrCode", "Generating QR code for UID: $uid")
        val bitMatrix = writer.encode(
            "bridgeapp://user?uid=$uid", // QR Content
            BarcodeFormat.QR_CODE,
            size,
            size
        )
        val bmp = createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bmp[x, y] = if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE
            }
        }
        bmp
    }

    Image(
        bitmap = qrBitmap.asImageBitmap(),
        contentDescription = "User QR Code",
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(8.dp)
    )
}

fun maskRwandaId(id: String): String {
    val digits = id.filter { it.isDigit() }
    return if (digits.length >= 17) {
        val firstGroup = digits[0].toString()
        val secondGroup = digits.substring(1, 5)
        val thirdGroup = digits[digits.length - 3].toString()
        val fourthGroup = digits.takeLast(2)
        "$firstGroup $secondGroup •••••••• $thirdGroup $fourthGroup"
    } else {
        "Invalid ID"
    }
}
/*
// You might still want a preview for development:
@Preview(showBackground = true, widthDp = 360)
@Composable
@RequiresApi(Build.VERSION_CODES.Q)
fun ProfileScreenPreview() {
    // This is a dummy UserSessionManager for preview purposes
    val previewUserSessionManager = object : UserSessionManager {
        override val currentUser = mutableStateOf(
            User(
                id = "user123",
                firstName = "Jean-Pierre",
                lastName = "Mugisha",
                email = "jp.mugisha@example.com",
                phoneNumber = "+250 788 123 456",
                gender = Gender.MALE,

                profilePictureUrl = null, // Or provide a sample image URL

                dateOfBirth = TODO(),
                activeRoles = TODO(),
                verificationStatus = TODO(),
                verificationMethodUsed = TODO(),
                verificationNotes = TODO(),
                verifiedByUserId = TODO(),
                verifiedByUserMethod = TODO(),
                studentDetails = TODO(),
                teacherDetails = TODO(),
                parentDetails = TODO(),
                schoolAdminDetails = TODO()
            )
        )

        fun saveSession(user: User) {}
        override suspend fun clearSession() {}
        suspend fun isLoggedIn(): Boolean = true
        fun getToken(): String? = "dummy_token"
    }

    MaterialTheme { // Wrap in MaterialTheme for proper theming
        ProfileScreen(
            userSessionManager = previewUserSessionManager,
            onBack = {}
        )
    }
}

*/