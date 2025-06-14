package com.schoolbridge.v2.ui.settings.profile

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.schoolbridge.v2.data.session.UserSessionManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.schoolbridge.v2.domain.user.Gender // Assuming Gender is defined elsewhere
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.AppSubSectionDivider
import com.schoolbridge.v2.ui.common.components.SpacerL
import android.graphics.Color as AndroidColor // Alias for Android's Color class
import androidx.compose.ui.graphics.Color as ComposeColor
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userSessionManager: UserSessionManager,
    onBack: () -> Unit
) {
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

    val currentOnBack by rememberUpdatedState(onBack)
    var isEditing by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }

    var editablePhone by remember { mutableStateOf("") }
    var editableEmail by remember { mutableStateOf("") }
    var editableDistrict by remember { mutableStateOf("") }
    var editableSector by remember { mutableStateOf("") }
    var editableCell by remember { mutableStateOf("") }
    var editableVillage by remember { mutableStateOf("") }

    var animateContent by remember { mutableStateOf(false) }
    val sectionAnimatedStates = remember { mutableStateOf(MutableList(8) { false }) }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            editablePhone = user.phoneNumber ?: ""
            editableEmail = user.email
            editableDistrict = user.address?.district ?: ""
            editableSector = user.address?.sector ?: ""
            editableCell = user.address?.cell ?: ""
            editableVillage = user.address?.village ?: ""
        }

        animateContent = true

        val delayStep = 50L
        for (i in sectionAnimatedStates.value.indices) {
            delay(delayStep)
            sectionAnimatedStates.value = sectionAnimatedStates.value.toMutableList().also {
                it[i] = true
            }
        }
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (currentUser?.isVerified == true) {
                        VerifiedBadge()
                    } else {
                        VerificationNeededBadge(
                            modifier = Modifier,
                            onClick = { /* Handle click to initiate verification */ }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = currentOnBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isEditing) {
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
        AnimatedVisibility(
            visible = animateContent,
            enter = fadeIn(animationSpec = tween(durationMillis = 150)) +
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight / 20 },
                        animationSpec = tween(durationMillis = 150)
                    ),
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column {
//                AnimatedVisibility(
//                    visible = sectionAnimatedStates.value[0],
//                    enter = fadeIn(animationSpec = tween(150)) + slideInVertically(
//                        initialOffsetY = { it / 4 },
//                        animationSpec = tween(150)
//                    )
//                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = currentUser?.profilePictureUrl,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(20.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = buildString {
                                        val title = if (currentUser?.gender == Gender.MALE) "Mr." else if (currentUser?.gender == Gender.FEMALE) "Mrs." else ""
                                        append("$title ${currentUser?.firstName ?: ""} ${currentUser?.lastName ?: ""}".trim())
                                    },
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }

                            Text(
                                text = "Your roles: ${currentUser?.activeRoles?.joinToString(", ") ?: "N/A"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                //}

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = sectionAnimatedStates.value[1],
                    enter = fadeIn(animationSpec = tween(150)) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(150)
                    )
                ) {
                    Column {
                        AppSubHeader("QR Code for Verification or Access")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
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
                    }
                }

                AnimatedVisibility(
                    visible = sectionAnimatedStates.value[2],
                    enter = fadeIn(animationSpec = tween(150))
                ) {
                    AppSubSectionDivider()
                }

                AnimatedVisibility(
                    visible = sectionAnimatedStates.value[3],
                    enter = fadeIn(animationSpec = tween(150)) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(150)
                    )
                ) {
                    Column {
                        AppSubHeader("Contact Information")
                        ProfileField(
                            "Phone Number",
                            editablePhone,
                            isEditing,
                            { editablePhone = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        ProfileField(
                            "Email",
                            editableEmail,
                            isEditing,
                            { editableEmail = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        ProfileReadonlyField("National ID", maskRwandaId(currentUser?.nationalId ?: "N/A"))
                    }
                }

                AnimatedVisibility(
                    visible = sectionAnimatedStates.value[4],
                    enter = fadeIn(animationSpec = tween(150))
                ) {
                    AppSubSectionDivider()
                }

                AnimatedVisibility(
                    visible = sectionAnimatedStates.value[5],
                    enter = fadeIn(animationSpec = tween(150)) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(150)
                    )
                ) {
                    Column {
                        AppSubHeader("Address Information")
                        ProfileField("District", editableDistrict, isEditing, { editableDistrict = it })
                        ProfileField("Sector", editableSector, isEditing, { editableSector = it })
                        ProfileField("Cell", editableCell, isEditing, { editableCell = it })
                        ProfileField("Village", editableVillage, isEditing, { editableVillage = it })
                    }
                }

                AnimatedVisibility(
                    visible = sectionAnimatedStates.value[6],
                    enter = fadeIn(animationSpec = tween(150))
                ) {
                    AppSubSectionDivider()
                }

                AnimatedVisibility(
                    visible = sectionAnimatedStates.value[7],
                    enter = fadeIn(animationSpec = tween(150)) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(150)
                    )
                ) {
                    Column {
                        AppSubHeader("Account Details")
                        ListItem(
                            headlineContent = { Text("Primary Role") },
                            supportingContent = { Text(currentUser?.role?.replaceFirstChar(Char::titlecase) ?: "N/A") }
                        )
                        ListItem(
                            headlineContent = { Text("Joined Date") },
                            supportingContent = { Text(currentUser?.joinDate ?: "N/A") }
                        )
                        ListItem(
                            headlineContent = { Text("User ID") },
                            supportingContent = { Text(currentUser?.userId ?: "N/A") }
                        )
                    }
                }

                AppSubSectionDivider()

                Column {
                    AppSubHeader("Linked Children (${currentUser?.linkedStudents?.size ?: 0})")

                    if (currentUser?.linkedStudents.isNullOrEmpty()) {
                        Text(
                            text = "No linked students found.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column {
                                currentUser!!.linkedStudents?.forEachIndexed { index, child ->
                                    ListItem(
                                        headlineContent = { Text("${child.firstName} ${child.lastName}") },
                                        supportingContent = { Text("Student ID: ${child.id}") },
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
                                    if (index < (currentUser?.linkedStudents!!.size - 1)) {
                                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                SpacerL()
                AnimatedVisibility(
                    visible = animateContent,
                    enter = fadeIn(animationSpec = tween(durationMillis = 150, delayMillis = 300)) +
                            slideInVertically(
                                initialOffsetY = { fullHeight -> fullHeight / 5 },
                                animationSpec = tween(durationMillis = 150, delayMillis = 300)
                            )
                ) {
                    Column {
                        if (currentUser?.isVerified == true) {
                            Spacer(modifier = Modifier.height(24.dp))
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
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
                                        text = "Your profile is verified via ID or MoM match.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else {
                            VerificationInfoCard(
                                modifier = Modifier.fillMaxWidth(),
                                onVerifyClick = { /* Handle verify click */ }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showQrDialog && currentUser != null) {
        AnimatedVisibility(
            visible = showQrDialog,
            enter = fadeIn(animationSpec = tween(durationMillis = 100)),
            exit = fadeOut(animationSpec = tween(durationMillis = 100))
        ) {
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
                                UserQrCode(uid = currentUser!!.userId)
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
}



// In your ProfileScreen.kt file, add this new composable function:

@Composable
fun VerificationInfoCard(modifier: Modifier = Modifier, onVerifyClick: () -> Unit) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onVerifyClick), // Make the entire card clickable
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info, // Use Info icon for information/action
                contentDescription = "Verification Needed",
                tint = MaterialTheme.colorScheme.secondary, // Use your theme's secondary color
                modifier = Modifier.size(24.dp) // Slightly larger icon for emphasis
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Your account is not verified.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Verify your account for enhanced security and full access to features.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap here to verify now!", // Clear call to action
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary // Highlight the call to action
                )
            }
        }
    }
}

// --- Helper Composables (No changes needed, keeping them as they are) ---

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
                supportingContent = {
                    // Display "Not provided" or "Unknown" only if the value is empty
                    Text(
                        if (value.isBlank()) "Not provided" else value,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
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

fun maskRwandaId(nationalId: String): String {
    val digits = nationalId.filter { it.isDigit() }
    return if (digits.length >= 17) {
        val firstChar = nationalId[0].toString()
        val secondGroup = digits.substring(1, 5) // Characters 1-4 (0-indexed)
        val lastThreeDigits = digits.substring(digits.length - 3) // Last 3 digits
        "$firstChar $secondGroup •••••••• $lastThreeDigits"
    } else {
        nationalId // Return as is if not long enough to mask meaningfully
    }
}
/*
@Composable
fun VerificationNeededBadge(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp), // A subtle background
                RoundedCornerShape(percent = 50)
            )
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(percent = 50)) // Added border for clarity
            .clickable(onClick = onClick) // Make it clickable
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info, // A common icon for information/action needed
            contentDescription = "Verification Needed",
            tint = ComposeColor(0xFFFFA500), // Muted orange/amber color, can adjust to your palette
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Verify Account", // Clear call to action
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface // Good contrast text color
        )
    }
}

*/
@Composable
fun VerificationNeededBadge(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp), // A subtle background
                RoundedCornerShape(percent = 50)
            )
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(percent = 50)) // Added border for clarity
            .clickable(onClick = onClick) // Make it clickable
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info, // A common icon for information/action needed
            contentDescription = "Verification Needed",
            tint = MaterialTheme.colorScheme.secondary, // Using secondary color from the theme
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Verify your Account",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface // Good contrast text color
        )
    }
}