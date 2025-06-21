package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.runtime.Composable
import com.schoolbridge.v2.domain.academic.*
import kotlin.collections.forEach
import kotlin.math.max
import kotlin.text.format

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





