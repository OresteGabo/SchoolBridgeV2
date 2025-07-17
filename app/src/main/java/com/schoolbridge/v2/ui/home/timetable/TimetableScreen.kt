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
import kotlin.random.Random
@Composable
fun TimetableScreen(
    events: List<TimetableEntry>,
    slotHeight: Dp,
    dayWidth: Dp,
    timeColWidth: Dp,
    onEventClick: (TimetableEntry) -> Unit,
    days: List<DayOfWeek> = DayHeaders,
    hourRange: IntRange = HourRange,
    startOfWeek: LocalDate,
    onDayHeaderClick: (LocalDate) -> Unit
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
            onDayHeaderClick = onDayHeaderClick,
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
    startOfWeek: LocalDate,
    onDayHeaderClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .horizontalScroll(hScroll)
            .height(headerHeight)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .zIndex(2f)
    ) {
        days.forEachIndexed { index, dayOfWeek ->
            val date = startOfWeek.plusDays(index.toLong())
            Box(
                Modifier
                    .width(dayWidth)
                    .fillMaxHeight()
                    .clickable { onDayHeaderClick(date) } // ✅ handle click
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
                        date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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
/*
val sampleEvents = generateSampleEventsForWeek(LocalDate.now())
*/



fun generateSampleEventsForWeek(startOfWeek: LocalDate=LocalDate.now()): List<TimetableEntry> {
    val random = Random(startOfWeek.toEpochDay()) // Seeded for reproducibility
    val weekNumber = startOfWeek.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
    val subjects = listOf("Mathematics", "Physics", "Biology", "Chemistry", "History", "Geography", "Literature", "ICT", "Kinyarwanda", "English", "French")
    val rooms = listOf("Room A1", "Room B1", "Room C1", "Room D1", "Room E1", "Lab 1", "Lab 2", "Auditorium")
    val teachers = listOf("Mr. Nshimiyimana", "Mrs. Uwase", "Ms. Mukamana", "Mr. Twizeyimana", "Dr. Uwimana", "Ms. Kayitesi", "Mr. Mugenzi", "Mrs. Ingabire")

    val timetableEntries = mutableListOf<TimetableEntry>()
    var idCounter = weekNumber * 1000

    for (i in 0..6) { // Each day from Monday to Sunday
        val day = DayOfWeek.of((i % 7) + 1)
        val date = startOfWeek.with(day)

        // Max 4 events per day, random 1-4:
        val numberOfEvents = random.nextInt(1, 5)
        val usedTimeSlots = mutableSetOf<Int>() // avoid overlapping

        repeat(numberOfEvents) {
            var startHour: Int
            do {
                startHour = random.nextInt(7, 17) // allow courses between 7:00 and 16:00
            } while (!usedTimeSlots.add(startHour))

            val endHour = startHour + 1
            val start = date.atTime(startHour, 0)
            val end = date.atTime(endHour, 30)

            val subject = subjects.random(random)
            val room = rooms.random(random)
            val teacher = teachers.random(random)

            timetableEntries += TimetableEntry(
                id = idCounter++,
                start = start,
                end = end,
                title = subject,
                room = room,
                teacher = teacher,
                type = TimetableEntryType.LECTURE
            )
        }
    }

    return timetableEntries
}







