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
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import com.schoolbridge.v2.domain.academic.*
import java.time.LocalDate
import kotlin.math.max
import java.time.temporal.WeekFields

@Composable
fun TimetableScreen(
    events: List<TimetableEntry>,
    slotHeight: Dp,
    dayWidth: Dp,
    timeColWidth: Dp,
    onEventClick: (TimetableEntry) -> Unit,
    days: List<DayOfWeek> = DayHeaders,
    hourRange: IntRange = HourRange,
    startOfWeek: LocalDate // NEW PARAMETER
) {
    val headerHeight = 40.dp
    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        // 1. Day headers
        DayHeaderRow(
            days = days,
            dayWidth = dayWidth,
            timeColWidth = timeColWidth,
            headerHeight = headerHeight,
            hScroll = hScroll,
            startOfWeek = startOfWeek, // PASS THE NEW PARAMETER
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = timeColWidth)
        )

        // 2. Hour column
        HourColumn(
            hourRange,
            slotHeight,
            timeColWidth,
            headerHeight,
            vScroll,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = headerHeight)
        )

        // 3. Grid and events with scrolling
        TimetableGridAndEvents(
            days = days,
            hourRange = hourRange,
            events = events,
            slotHeight = slotHeight,
            dayWidth = dayWidth,
            timeColWidth = timeColWidth,
            headerHeight = headerHeight,
            hScroll = hScroll,
            vScroll = vScroll,
            onEventClick = onEventClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = timeColWidth, top = headerHeight)
        )
    }
}

@Composable
private fun DayHeaderRow(
    days: List<DayOfWeek>,
    dayWidth: Dp,
    timeColWidth: Dp,
    headerHeight: Dp,
    hScroll: ScrollState,
    startOfWeek: LocalDate, // NEW PARAMETER
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .horizontalScroll(hScroll)
            .height(headerHeight)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .zIndex(2f)
    ) {
        days.forEachIndexed { index, dayOfWeek -> // Use indexed to get position
            val date = startOfWeek.plusDays(index.toLong()) // Calculate date for this column
            Box(
                Modifier
                    .width(dayWidth)
                    .fillMaxHeight()
                    .border(0.5.dp, MaterialTheme.colorScheme.outline),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        date.dayOfMonth.toString(), // Display day of month
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), // More prominent day number
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun DayHeaderRow(
    days: List<DayOfWeek>,
    dayWidth: Dp,
    timeColWidth: Dp,
    headerHeight: Dp,
    hScroll: ScrollState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .horizontalScroll(hScroll)
            .height(headerHeight)
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
}

@Composable
private fun HourColumn(
    hourRange: IntRange,
    slotHeight: Dp,
    timeColWidth: Dp,
    headerHeight: Dp,
    vScroll: ScrollState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .verticalScroll(vScroll)
            .width(timeColWidth)
            .zIndex(2f)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(top = 0.dp) // padding handled outside via align + padding
    ) {
        hourRange.forEach { hour ->
            Box(
                Modifier
                    .height(slotHeight)
                    .fillMaxWidth()
                    .border(0.5.dp, MaterialTheme.colorScheme.outline),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = "%02dh".format(hour),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimetableGridAndEvents(
    days: List<DayOfWeek>,
    hourRange: IntRange,
    events: List<TimetableEntry>,
    slotHeight: Dp,
    dayWidth: Dp,
    timeColWidth: Dp,
    headerHeight: Dp,
    hScroll: ScrollState,
    vScroll: ScrollState,
    onEventClick: (TimetableEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    Box(
        modifier
            .horizontalScroll(hScroll)
            .verticalScroll(vScroll)
            .clipToBounds()
    ) {
        // Background grid
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

        // Event cards
        val firstHour = hourRange.first
        events.forEach { entry ->
            val dayIdx = days.indexOf(entry.start.dayOfWeek)
            if (dayIdx == -1) return@forEach

            val startMinutes = entry.start.hour * 60 + entry.start.minute
            val endMinutes = entry.end.hour * 60 + entry.end.minute
            val topMinutes = startMinutes - firstHour * 60
            val durationMinutes = max(15, endMinutes - startMinutes)

            val topPx = with(density) { (topMinutes / 60f * slotHeight.toPx()) }
            val heightPx = with(density) { (durationMinutes / 60f * slotHeight.toPx()) }
            val leftPx = with(density) { (dayIdx * dayWidth.toPx()) }

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
                    .zIndex(1f)
            ) {
                TimetableEventCard(
                    entry = entry,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}










/*
val sampleEvents = listOf(
    // Monday
    TimetableEntry(
        id = 1,
        start = LocalDate.now().with(DayOfWeek.MONDAY).atTime(7, 30),
        end   = LocalDate.now().with(DayOfWeek.MONDAY).atTime(9, 0),
        title = "Mathematics",
        room  = "Room A1",
        teacher = "Mr. Nshimiyimana",
        type  = TimetableEntryType.LECTURE
    ),
    TimetableEntry(
        id = 2,
        start = LocalDate.now().with(DayOfWeek.MONDAY).atTime(9, 15),
        end   = LocalDate.now().with(DayOfWeek.MONDAY).atTime(10, 45),
        title = "English Language",
        room  = "Room A2",
        teacher = "Mrs. Uwase",
        type  = TimetableEntryType.LECTURE
    ),
    TimetableEntry(
        id = 3,
        start = LocalDate.now().with(DayOfWeek.MONDAY).atTime(11, 0),
        end   = LocalDate.now().with(DayOfWeek.MONDAY).atTime(12, 30),
        title = "ICT Practical",
        room  = "Computer Lab",
        teacher = "Mr. Habimana",
        type  = TimetableEntryType.PRACTICAL
    ),

    // Tuesday
    TimetableEntry(
        id = 4,
        start = LocalDate.now().with(DayOfWeek.TUESDAY).atTime(8, 0),
        end   = LocalDate.now().with(DayOfWeek.TUESDAY).atTime(9, 30),
        title = "Kinyarwanda",
        room  = "Room A3",
        teacher = "Ms. Ingabire",
        type  = TimetableEntryType.LECTURE
    ),
    TimetableEntry(
        id = 5,
        start = LocalDate.now().with(DayOfWeek.TUESDAY).atTime(10, 0),
        end   = LocalDate.now().with(DayOfWeek.TUESDAY).atTime(11, 30),
        title = "Physics Experiment",
        room  = "Physics Lab",
        teacher = "Mr. Munyaneza",
        type  = TimetableEntryType.PRACTICAL
    ),

    // Wednesday
    TimetableEntry(
        id = 6,
        start = LocalDate.now().with(DayOfWeek.WEDNESDAY).atTime(7, 30),
        end   = LocalDate.now().with(DayOfWeek.WEDNESDAY).atTime(9, 0),
        title = "Entrepreneurship",
        room  = "Room B1",
        teacher = "Mrs. Nyirabizeyimana",
        type  = TimetableEntryType.LECTURE
    ),
    TimetableEntry(
        id = 7,
        start = LocalDate.now().with(DayOfWeek.WEDNESDAY).atTime(9, 15),
        end   = LocalDate.now().with(DayOfWeek.WEDNESDAY).atTime(10, 45),
        title = "Biology Group Work",
        room  = "Room B2",
        teacher = "Ms. Mukamana",
        type  = TimetableEntryType.GROUP_WORK
    ),

    // Thursday
    TimetableEntry(
        id = 8,
        start = LocalDate.now().with(DayOfWeek.THURSDAY).atTime(8, 30),
        end   = LocalDate.now().with(DayOfWeek.THURSDAY).atTime(10, 0),
        title = "History",
        room  = "Room C1",
        teacher = "Mr. Twizeyimana",
        type  = TimetableEntryType.LECTURE
    ),
    TimetableEntry(
        id = 9,
        start = LocalDate.now().with(DayOfWeek.THURSDAY).atTime(10, 15),
        end   = LocalDate.now().with(DayOfWeek.THURSDAY).atTime(11, 45),
        title = "Remedial: Mathematics",
        room  = "Room A1",
        teacher = "Mr. Nshimiyimana",
        type  = TimetableEntryType.REMEDIAL
    ),

    // Friday
    TimetableEntry(
        id = 10,
        start = LocalDate.now().with(DayOfWeek.FRIDAY).atTime(7, 30),
        end   = LocalDate.now().with(DayOfWeek.FRIDAY).atTime(8, 30),
        title = "School Assembly",
        room  = "Main Hall",
        teacher = "",
        type  = TimetableEntryType.ASSEMBLY
    ),
    TimetableEntry(
        id = 11,
        start = LocalDate.now().with(DayOfWeek.FRIDAY).atTime(9, 0),
        end   = LocalDate.now().with(DayOfWeek.FRIDAY).atTime(10, 30),
        title = "Geography",
        room  = "Room C2",
        teacher = "Ms. Kayitesi",
        type  = TimetableEntryType.LECTURE
    ),
    TimetableEntry(
        id = 12,
        start = LocalDate.now().with(DayOfWeek.FRIDAY).atTime(10, 45),
        end   = LocalDate.now().with(DayOfWeek.FRIDAY).atTime(12, 15),
        title = "English Test",
        room  = "Room A2",
        teacher = "Mrs. Uwase",
        type  = TimetableEntryType.TEST
    )
)
*/

val sampleEvents = generateSampleEventsForWeek(LocalDate.now())




fun generateSampleEventsForWeek(startOfWeek: LocalDate): List<TimetableEntry> {
    val weekNumber = startOfWeek.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
    fun dateForDay(day: DayOfWeek) = startOfWeek.with(day)

    // Define 3 different timetables that cycle every 3 weeks:
    val timetable1 = listOf(
        TimetableEntry(1, dateForDay(DayOfWeek.MONDAY).atTime(8, 0), dateForDay(DayOfWeek.MONDAY).atTime(9, 30), "Mathematics", "Room A1", "Mr. Nshimiyimana", TimetableEntryType.LECTURE),
        TimetableEntry(2, dateForDay(DayOfWeek.TUESDAY).atTime(10, 0), dateForDay(DayOfWeek.TUESDAY).atTime(11, 30), "Physics", "Physics Lab", "Mrs. Uwase", TimetableEntryType.LECTURE),
    )

    val timetable2 = listOf(
        TimetableEntry(3, dateForDay(DayOfWeek.MONDAY).atTime(8, 0), dateForDay(DayOfWeek.MONDAY).atTime(9, 30), "Biology", "Room B1", "Ms. Mukamana", TimetableEntryType.LECTURE),
        TimetableEntry(4, dateForDay(DayOfWeek.WEDNESDAY).atTime(9, 0), dateForDay(DayOfWeek.WEDNESDAY).atTime(10, 30), "History", "Room C1", "Mr. Twizeyimana", TimetableEntryType.LECTURE),
    )

    val timetable3 = listOf(
        TimetableEntry(5, dateForDay(DayOfWeek.MONDAY).atTime(8, 0), dateForDay(DayOfWeek.MONDAY).atTime(9, 30), "Chemistry", "Room D1", "Dr. Uwimana", TimetableEntryType.LECTURE),
        TimetableEntry(6, dateForDay(DayOfWeek.THURSDAY).atTime(10, 0), dateForDay(DayOfWeek.THURSDAY).atTime(11, 30), "Geography", "Room E2", "Ms. Kayitesi", TimetableEntryType.LECTURE),
    )

    // Cycle the timetables every 3 weeks:
    return when (weekNumber % 3) {
        1 -> timetable1
        2 -> timetable2
        else -> timetable3
    }
}









