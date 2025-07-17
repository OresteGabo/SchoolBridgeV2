package com.schoolbridge.v2.ui.home.timetable

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.academic.TimetableEntry
import com.schoolbridge.v2.domain.academic.TimetableEntryType
import java.time.Duration
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PersonPinCircle
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.AlertSeverity
import com.schoolbridge.v2.domain.messaging.AlertSourceType
import com.schoolbridge.v2.domain.messaging.AlertType
import com.schoolbridge.v2.ui.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

@Deprecated("Use DailyCourseCard instead")
@Composable
fun DailyCourseCard1(
    entry: TimetableEntry,
    participants: List<String> = listOf("AB", "CD", "EF", "GH", "IJ", "KL"),
    modifier: Modifier = Modifier,
    attendanceRate: Float = 0.75f,
    onParticipantsClick: () -> Unit = {}
) {
    val accent = timetableEntryColor(entry.type)
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val durationMin = Duration.between(entry.start, entry.end).toMinutes()
    val durationStr = if (durationMin >= 60) {
        val h = durationMin / 60
        val m = durationMin % 60
        if (m == 0L) "${h}h" else "${h}h ${m}m"
    } else "${durationMin}m"

    val now = LocalDateTime.now()
    val showAttendance = entry.end.isBefore(now) || entry.start <= now

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.weight(1f))
                if (showAttendance) {
                    Text(
                        text = "${(attendanceRate * 100).toInt()}% attended",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = entry.teacher,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (entry.room.isNotBlank()) {
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = entry.room,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = "${entry.start.format(timeFmt)} – ${entry.end.format(timeFmt)} • $durationStr",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (participants.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                ParticipantAvatars(
                    participants = participants,
                    onClickMore = onParticipantsClick
                )
            }

            if (showAttendance) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = attendanceRate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = accent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}


@Deprecated("Use DailyCourseCard instead")
@Composable
fun DailyCourseCard2(
    entry: TimetableEntry,
    modifier: Modifier = Modifier,
    participants: List<String> = listOf("AB", "CD", "EF", "GH", "IJ", "KL"), // Example participants
    onParticipantsClick: () -> Unit = {}
) {
    val accent = timetableEntryColor(entry.type)
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val durationMin = Duration.between(entry.start, entry.end).toMinutes()

    // --- DUMMY attendance value (0f‒1f). Replace with real data later.
    val attendanceRate = 0.75f

    // Decide whether to show attendance: past or in‑progress
    val now = LocalDateTime.now()
    val isPastOrOngoing =
        entry.end.isBefore(now) || (entry.start.isBefore(now) && entry.end.isAfter(now))

    val durationTxt = remember(durationMin) {
        when {
            durationMin >= 60 -> {
                val h = durationMin / 60
                val m = durationMin % 60
                if (m == 0L) "${h}h" else "${h}h ${m}m"
            }
            else -> "${durationMin}m"
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Box(modifier = Modifier.wrapContentSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp)
            ) {
                // Colored side bar
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            accent,
                            RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                )

                // Main content
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DurationIndicatorModern(accent, durationTxt)
                    Spacer(Modifier.width(16.dp))

                    Column(Modifier.weight(1f)) {
                        // Title
                        Text(
                            entry.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Time range
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${entry.start.format(timeFmt)} – ${entry.end.format(timeFmt)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Teacher / room
                        if (entry.teacher.isNotBlank() || entry.room.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            TeacherAndRoomLine(entry.teacher, entry.room)
                        }

                        // --- Attendance block (only if past or ongoing) ---
                        if (isPastOrOngoing) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Attendance: ${(attendanceRate * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LinearProgressIndicator(
                                progress = { attendanceRate },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                    }
                }
            }

            // Participant avatars
            if (participants.isNotEmpty()) {
                ParticipantAvatars(
                    participants = participants,
                    modifier = Modifier
                        .clickable { onParticipantsClick() }
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    avatarSize = 32.dp
                )
            }
        }
    }
}


@Composable
fun DailyCourseCard(
    entry: TimetableEntry,
    modifier: Modifier = Modifier,
    attendanceRate: Float = 0.75f
) {
    val accentColor = timetableEntryColor(entry.type)
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val now = LocalDateTime.now()
    val isPastOrOngoing = entry.end.isBefore(now) || (entry.start.isBefore(now) && entry.end.isAfter(now))

    val durationMin = Duration.between(entry.start, entry.end).toMinutes()
    val durationText = if (durationMin >= 60) {
        val h = durationMin / 60
        val m = durationMin % 60
        if (m == 0L) "${h}h" else "${h}h ${m}m"
    } else "${durationMin}m"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(12.dp)) {
            // Accent bar
            Box(
                Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(accentColor)
            )

            Spacer(Modifier.width(12.dp))

            // Main Content
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (isPastOrOngoing) {
                        AttendancePill(attendanceRate)
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (entry.teacher.isNotBlank()) {
                        Icon(Icons.Default.Person, contentDescription = null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                        Text(entry.teacher, style = MaterialTheme.typography.labelSmall)
                    }

                    if (entry.room.isNotBlank()) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                        Text(entry.room, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${entry.start.format(timeFmt)} – ${entry.end.format(timeFmt)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Default.MoreHoriz, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(Modifier.width(4.dp))
                    Text(durationText, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun AttendancePill(rate: Float) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(100),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = "${(rate * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}


@Deprecated("Use DailyCourseCard instead")
@Composable
fun DailyCourseCard3(
    entry: TimetableEntry,
    modifier: Modifier = Modifier,
    attendanceRate: Float = 0.75f
) {
    val accentColor = timetableEntryColor(entry.type)
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val now = LocalDateTime.now()
    val isPastOrOngoing = entry.end.isBefore(now) || (entry.start.isBefore(now) && entry.end.isAfter(now))

    val durationMin = Duration.between(entry.start, entry.end).toMinutes()
    val durationText = if (durationMin >= 60) {
        val h = durationMin / 60
        val m = durationMin % 60
        if (m == 0L) "${h}h" else "${h}h ${m}m"
    } else "${durationMin}m"

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (entry.teacher.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, Modifier.size(16.dp), tint = accentColor)
                        Spacer(Modifier.width(6.dp))
                        Text(entry.teacher, style = MaterialTheme.typography.labelSmall)
                    }
                }
                if (entry.room.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, Modifier.size(16.dp), tint = accentColor)
                        Spacer(Modifier.width(6.dp))
                        Text(entry.room, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(6.dp))
                    Text("${entry.start.format(timeFmt)} – ${entry.end.format(timeFmt)}", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.width(10.dp))
                    Text("• $durationText", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (isPastOrOngoing) {
                CircularAttendanceDisplay(attendanceRate = attendanceRate)
            }
        }
    }
}

@Composable
fun CircularAttendanceDisplay(attendanceRate: Float) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .padding(start = 12.dp),
        contentAlignment = Alignment.Center
    ) {
       CircularProgressIndicator(
           progress = { attendanceRate },
           modifier = Modifier.fillMaxSize(),
           color = MaterialTheme.colorScheme.primary,
           strokeWidth = 6.dp,
           trackColor = MaterialTheme.colorScheme.surfaceVariant,
       )
        Text(
            "${(attendanceRate * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}




@Deprecated("Use DailyCourseCard instead")
@Composable
fun DailyCourseCard4(
    entry: TimetableEntry,
    modifier: Modifier = Modifier,
    attendanceRate: Float = 0.75f // Dummy value
) {
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val now = LocalDateTime.now()
    val isPastOrOngoing =
        entry.end.isBefore(now) || (entry.start.isBefore(now) && entry.end.isAfter(now))

    val durationMin = Duration.between(entry.start, entry.end).toMinutes()
    val durationTxt = if (durationMin >= 60) {
        val h = durationMin / 60
        val m = durationMin % 60
        if (m == 0L) "${h}h" else "${h}h ${m}m"
    } else "${durationMin}m"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Time & Duration
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${entry.start.format(timeFmt)} – ${entry.end.format(timeFmt)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MoreHoriz, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        durationTxt,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Course Title
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(6.dp))

            // Teacher and Room Chips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (entry.teacher.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = entry.teacher,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (entry.room.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = entry.room,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Attendance section (only show for past or ongoing)
            if (isPastOrOngoing) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Attendance: ${(attendanceRate * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = attendanceRate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(100)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}




@Composable
private fun DurationIndicatorModern(color: Color, duration: String) {
    Column(
        modifier = Modifier.width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Start Dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        // Vertical Line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(24.dp)
                .background(color.copy(alpha = 0.5f))
        )
        // Duration Text in a pill shape
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                duration,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        // Vertical Line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(24.dp)
                .background(color.copy(alpha = 0.5f))
        )
        // End Dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}


@Composable
private fun TeacherAndRoomLine(teacher: String, room: String) {
    Column{
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (teacher.isNotBlank()) {
                Icon(Icons.Default.Person, null, Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(6.dp))
                Text(
                    teacher,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (teacher.isNotBlank() && room.isNotBlank()) Spacer(Modifier.width(12.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (room.isNotBlank()) {
                Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(6.dp))
                Text(
                    room,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}



@Composable
private fun timetableEntryColor(type: TimetableEntryType): Color {
    return when(type) {
        TimetableEntryType.LECTURE -> MaterialTheme.colorScheme.primary
        TimetableEntryType.PRACTICAL -> MaterialTheme.colorScheme.secondary
        TimetableEntryType.GROUP_WORK -> MaterialTheme.colorScheme.tertiary
        TimetableEntryType.REMEDIAL -> MaterialTheme.colorScheme.inversePrimary
        TimetableEntryType.TEST -> MaterialTheme.colorScheme.error
        TimetableEntryType.ASSEMBLY -> MaterialTheme.colorScheme.surfaceTint
        TimetableEntryType.LAB -> MaterialTheme.colorScheme.surfaceTint
    }
}



@Composable
fun ParticipantAvatars(
    participants: List<String>,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 28.dp,
    onClickMore: () -> Unit = {
        //Log.d("ParticipantAvatars", "More participants clicked")
    }
) {
    val density = LocalDensity.current
     val maxVisibleAvatars = 3
    val avatarBgColor = MaterialTheme.colorScheme.surfaceVariant
    val avatarTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    val actualVisibleParticipants = participants.take(maxVisibleAvatars)
    val remainingParticipants = participants.size - actualVisibleParticipants.size
    val showMoreAvatar = remainingParticipants > 0

    fun overlapFractionFor(index: Int): Float = when (index) {
        0 -> 0.9f
        1 -> 0.88f
        else -> 0.5f
    }

    val totalWidth = with(density) {
        var widthPx = avatarSize.toPx()
        val currentAvatarsToDraw = actualVisibleParticipants.size + (if(showMoreAvatar) 1 else 0)
        for (i in 1 until currentAvatarsToDraw) {
            val prevOverlap = overlapFractionFor(i - 1)
            widthPx += (avatarSize.toPx() - (avatarSize.toPx() * prevOverlap))
        }
        widthPx.dp
    }

    val wiggleOffset by rememberInfiniteTransition(label = "wiggle").animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wiggleOffset"
    )

    var isTapped by remember { mutableStateOf(false) }
    val tapScale by animateFloatAsState(
        targetValue = if (isTapped) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tapScale",
        finishedListener = { isTapped = false }
    )

    Box(
        modifier = modifier
            .width(totalWidth)
            .clickable {
                isTapped = true
                onClickMore()
            }
            .height(avatarSize),
        contentAlignment = Alignment.CenterEnd
    ) {
        actualVisibleParticipants.forEachIndexed { index, participant ->
            val offsetX = with(density) {
                var offset = 0f
                for (i in 0 until index) {
                    offset += (avatarSize.toPx() - (avatarSize.toPx() * overlapFractionFor(i)))
                }
                offset.toDp()
            }

            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .offset(x = -offsetX)
                    .clip(CircleShape)
                    .background(avatarBgColor)
                    .border(
                        1.5.dp,
                        MaterialTheme.colorScheme.surface,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = participant.take(2).uppercase(),
                    color = avatarTextColor,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showMoreAvatar) {
            val lastAvatarIndex = actualVisibleParticipants.size
            val offsetX = with(density) {
                var offset = 0f
                for (i in 0 until lastAvatarIndex) {
                    offset += (avatarSize.toPx() - (avatarSize.toPx() * overlapFractionFor(i)))
                }
                offset.toDp()
            }

            // Calculate the diluted primary color using Color.lerp
            val dilutedPrimaryColor = lerp( // Use lerp directly
                start = MaterialTheme.colorScheme.primary,
                stop = MaterialTheme.colorScheme.surface, // Blend with your surface color
                fraction = 0.4f // Adjust this fraction (e.g., 0.3f, 0.4f, 0.5f) for desired dilution
                // 0.0 = full primary, 1.0 = full surface
            )

            // The 'onPrimary' color is designed to contrast with 'primary'.
            // For a slightly diluted primary, it often still works well.
            // If it doesn't, consider 'onSurface' or a custom 'on' color that
            // specifically contrasts with your chosen 'dilutedPrimaryColor'.
            val iconTint = MaterialTheme.colorScheme.onPrimary // Typically onPrimary will still work for slightly diluted primary

            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .offset(x = -offsetX + wiggleOffset.dp)
                    .graphicsLayer {
                        scaleX = tapScale
                        scaleY = tapScale
                    }
                    .clip(CircleShape)
                    .background(dilutedPrimaryColor)
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonPinCircle,
                    contentDescription = "More participants",
                    tint = iconTint,
                    modifier = Modifier.size(avatarSize * 0.7f)
                )
            }

        }
    }

    if(isTapped) {
        Log.d("ParticipantAvatars", "Tapped")
    }
}

