package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import com.schoolbridge.v2.R
import com.schoolbridge.v2.domain.academic.*
import com.schoolbridge.v2.localization.t
import kotlin.collections.count
import kotlin.collections.forEach
import kotlin.math.max
import kotlin.ranges.coerceAtLeast
import kotlin.ranges.coerceAtMost
import kotlin.ranges.coerceIn
import kotlin.run
import kotlin.text.format
import kotlin.text.ifBlank

@Composable
fun TimetableScreen(
    events: List<TimetableEntry>,
    slotHeight: Dp,
    dayWidth: Dp,
    timeColWidth: Dp,
    onEventClick: (TimetableEntry) -> Unit,
    days: List<DayOfWeek> = DayHeaders,
    hourRange: IntRange = HourRange            // e.g. 7..18
) {
    val headerH = 40.dp

    /* one shared horizontal & vertical scroll state */
    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Box(Modifier.fillMaxSize()) {

        /* ───────────── 1 ▸ Sticky DAY header row ───────────── */
        Row(
            Modifier
                .align(Alignment.TopStart)
                .padding(start = timeColWidth)           // leave space for hours col
                .horizontalScroll(hScroll)
                .height(headerH)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .zIndex(2f)
        ) {
            days.forEach { day ->
                Box(
                    Modifier
                        .width(dayWidth)
                        .fillMaxHeight()
                        .border(0.5.dp, MaterialTheme.colorScheme.outline),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        /* ───────────── 2 ▸ Sticky HOUR column ─────────────── */
        Column(
            Modifier
                .align(Alignment.TopStart)
                .padding(top = headerH)
                .verticalScroll(vScroll)
                .width(timeColWidth)
                .zIndex(2f)
                .background(MaterialTheme.colorScheme.surfaceContainerLow) // theme-based bg
        ) {
            hourRange.forEach { hour ->
                Box(
                    Modifier
                        .height(slotHeight)
                        .fillMaxWidth()
                        .border(0.5.dp, MaterialTheme.colorScheme.outline),
                    contentAlignment = Alignment.TopStart // precise alignment
                ) {
                    Text(
                        text = "%02dh".format(hour),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .padding(start = 4.dp, top = 2.dp), // top padding to match time start
                        color = MaterialTheme.colorScheme.onSurfaceVariant // readable but subtle
                    )
                }
            }
        }



        /* ───────────── 3 ▸ Scrollable GRID + EVENTS layer ─── */
        val density = LocalDensity.current
        Box(
            Modifier
                .align(Alignment.TopStart)
                .padding(start = timeColWidth, top = headerH)
                .horizontalScroll(hScroll)
                .verticalScroll(vScroll)
                .clipToBounds()            // events disappear behind headers
        ) {
            /* background grid */
            Row {
                days.forEach { _ ->
                    Column {
                        hourRange.forEach { _ ->
                            Box(
                                Modifier
                                    .size(dayWidth, slotHeight)
                                    .border(0.5.dp, MaterialTheme.colorScheme.outline)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)

                            )
                        }
                    }
                }
            }

            /* event cards */
            val firstHour = hourRange.first
            events.forEach { entry ->
                val dayIdx = days.indexOf(entry.day)
                if (dayIdx == -1) return@forEach

                /* — vertical position & height — */
                val startMins = entry.start.hour * 60 + entry.start.minute
                val endMins   = entry.end.hour   * 60 + entry.end.minute
                val topMins   = startMins - firstHour * 60
                val durMins   = max(15, endMins - startMins)

                val topPx    = with(density) { (topMins / 60f * slotHeight.toPx()) }
                val heightPx = with(density) { (durMins / 60f * slotHeight.toPx()) }


                /* — horizontal position — */
                val leftPx   = with(density) { (dayIdx * dayWidth.toPx()) }

                Box(
                    Modifier
                        .absoluteOffset(
                            x = with(density) { leftPx.toDp() },
                            y = with(density) { topPx.toDp() }
                        )
                        .width(dayWidth)
                        .height(with(density) { heightPx.toDp() })
                        .padding(2.dp)
                        .clickable { onEventClick(entry) }
                        .zIndex(1f)          // above grid, below headers
                ) {
                    TimetableEventCard(
                        entry  = entry,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


/* ──────────────────────────────────────────────────────────────
   TimetableEventCard – mini version styled like TodayScheduleCard
   ────────────────────────────────────────────────────────────── */
@Composable
private fun TimetableEventCard(
    entry: TimetableEntry,
    modifier: Modifier = Modifier
) {
    // Colour strip on the left re‑uses your helper for entry‑type colouring
    val stripColor = timetableEntryColor(entry.type)

    Card(
        modifier = modifier
            .fillMaxSize(),
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor   = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            /* type / attendance strip (similar idea to TodayScheduleCard’s dot+line) */
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(stripColor)
            )

            Spacer(Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                /* title */
                Text(
                    entry.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                /* time row */
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${entry.start} – ${entry.end}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                /* teacher row (optional) */
                if (entry.teacher.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            entry.teacher,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                /* room row */
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        entry.room,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


/* ───────────────────────────────────────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyTimetableTab(
    events: List<TimetableEntry> = sampleEvents,
    onBack: () -> Unit // kept for your usage if needed inside, but Scaffold removed
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selected by rememberSaveable { mutableStateOf<TimetableEntry?>(null) }

    // Base sizes as Dp units
    val baseSlotH = 64.dp
    val baseDayW = 128.dp
    val baseTimeW = 56.dp

    var scale by remember { mutableFloatStateOf(1f) }
    val minScale = 0.3f
    val maxScale = 3f

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val constraints = this.constraints
            val density = LocalDensity.current

            val totalHours = HourRange.count()
            val totalDays = DayHeaders.size

            val minScaleFit = run {
                val totalWidthPx = with(density) { (baseTimeW + baseDayW * totalDays).toPx() }
                val totalHeightPx = with(density) { (baseSlotH * totalHours).toPx() }
                val wFit = constraints.maxWidth.toFloat() / totalWidthPx
                val hFit = constraints.maxHeight.toFloat() / totalHeightPx
                kotlin.comparisons.minOf(wFit, hFit).coerceAtMost(maxScale)
            }

            scale = scale.coerceIn(minScaleFit, maxScale)

            val dayW = baseDayW * scale
            val timeW = baseTimeW * scale

            val minSlotHeight = with(density) {
                (constraints.maxHeight.toFloat() / totalHours).toDp()
            }
            val slotH = (baseSlotH * scale).coerceAtLeast(minSlotHeight)

            TimetableScreen(
                events = events,
                slotHeight = slotH,
                dayWidth = dayW,
                timeColWidth = timeW,
                onEventClick = {
                    selected = it
                    scope.launch { sheetState.show() }
                }
            )
        }

        FloatingZoomControls(
            onZoomIn = { scale = (scale * 1.12f).coerceIn(minScale, maxScale) },
            onZoomOut = { scale = (scale / 1.12f).coerceIn(minScale, maxScale) },
            onAddPersonalEvent = { /* TODO */ },
            onNavigateToday = { /* TODO */ },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

    if (selected != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                selected = null
            }
        ) {
            val e = selected!!
            Column(Modifier.padding(16.dp)) {
                Text(
                    e.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text("Teacher: ${e.teacher.ifBlank { "N/A" }}")
                Text("Room: ${e.room}")
                Text("Time: ${e.start} – ${e.end}")
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }
                        selected = null
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}





/**
 * Hard‑coded demo data used in previews and the preview screen.
 */
val sampleEvents = listOf(
    TimetableEntry(1, DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(10, 0),
        "Algo Géo TD", "ILL E37", "Pr Schmitt", TimetableEntryType.TD),
    TimetableEntry(2, DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 30),
        "Réseaux Présentation", "ILL B3", "L. Moalic", TimetableEntryType.PRESENTATION),
    TimetableEntry(3, DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(10, 30),
        "Algo Géo TD", "ILL E37", "", TimetableEntryType.TD),
    TimetableEntry(4, DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(12, 30),
        "Génie Logiciel CM", "K 109", "Y. Maillot", TimetableEntryType.COURSE),
    TimetableEntry(5, DayOfWeek.FRIDAY, LocalTime.of(14, 0), LocalTime.of(16, 0),
        "Dév. Web Avancé CM", "K 109", "F. Cordier", TimetableEntryType.COURSE),
    TimetableEntry(5, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(16, 0),
        "Dév. Web Avancé CM", "K 109", "F. Cordier", TimetableEntryType.COURSE)
)





