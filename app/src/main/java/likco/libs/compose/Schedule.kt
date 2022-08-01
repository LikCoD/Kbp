package likco.libs.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ldc.kbp.models.Lesson
import likco.studyum.types.BoundInt
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.min
import kotlin.math.roundToInt

data class PositionedEvent(
    val lesson: List<List<Lesson>>,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime
)

private class EventDataModifier(
    val positionedEvent: PositionedEvent,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = positionedEvent
}

private fun Modifier.eventData(positionedEvent: PositionedEvent) =
    this.then(EventDataModifier(positionedEvent))

@Composable
fun Schedule(
    events: List<Lesson>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (i: Int, len: Int, lesson: Lesson) -> Unit,
    dayHeader: @Composable (day: LocalDate) -> Unit,
    timeLabel: @Composable (time: LocalTime) -> Unit,
    moreIndicator: DrawScope.() -> Unit,
    minDate: LocalDate = events.minOfOrNull(Lesson::startDate)?.toLocalDate() ?: LocalDate.now(),
    maxDate: LocalDate = events.maxOfOrNull(Lesson::endDate)?.toLocalDate() ?: minDate,
    minTime: LocalTime = events.minOfOrNull { it.startDate.toLocalTime() }
        ?.let { it.minusMinutes(it.minute.toLong()) } ?: LocalTime.MIN,
    maxTime: LocalTime = events.maxOfOrNull { it.endDate.toLocalTime() }
        ?.let { it.plusMinutes(60 - it.minute.toLong()) } ?: LocalTime.MAX,
    daySize: Dp = 256.dp,
    hourSize: Dp = 128.dp,
    subjectSize: Dp = 64.dp,
    gapSize: Dp = 30.dp
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    var sidebarWidth by remember { mutableStateOf(0) }
    BoxWithConstraints(modifier = modifier) {
        Column(modifier = modifier) {
            ScheduleHeader(
                minDate = minDate,
                maxDate = maxDate,
                dayWidth = daySize + gapSize,
                dayHeader = dayHeader,
                modifier = Modifier
                    .padding(start = with(LocalDensity.current) { sidebarWidth.toDp() })
                    .horizontalScroll(horizontalScrollState)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Start)
            ) {
                ScheduleSidebar(
                    hourHeight = hourSize,
                    minTime = minTime,
                    maxTime = maxTime,
                    times = events
                        .flatMap { listOf(it.startDate.toLocalTime(), it.endDate.toLocalTime()) }
                        .distinct(),
                    label = timeLabel,
                    modifier = Modifier
                        .verticalScroll(verticalScrollState)
                        .onGloballyPositioned { sidebarWidth = it.size.width }
                )
                BasicSchedule(
                    events = events,
                    eventContent = {
                        BasicCellContent(
                            event = it,
                            content = eventContent,
                            moreIndicator = moreIndicator
                        )
                    },
                    minDate = minDate,
                    maxDate = maxDate,
                    minTime = minTime,
                    maxTime = maxTime,
                    dayWidth = daySize,
                    hourHeight = hourSize,
                    subjectHeight = subjectSize,
                    gapWidth = gapSize,
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState)
                        .verticalScroll(verticalScrollState)
                )
            }
        }
    }
}

@Composable
fun BoxScope.BasicCellContent(
    event: PositionedEvent,
    content: @Composable (i: Int, len: Int, lesson: Lesson) -> Unit,
    moreIndicator: DrawScope.() -> Unit,
) {
    @Composable
    fun RowScope.ActionButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent
            ),
            modifier = Modifier
                .fillMaxSize()
                .weight(1F),
        ) {

        }
    }

    if (event.lesson.size > 1)
        Canvas(modifier = Modifier.align(Alignment.TopEnd)) {
            moreIndicator()
        }

    var chunk by remember { mutableStateOf(0) }
    Column {
        event.lesson[chunk].forEachIndexed { i, lesson ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                content(i, event.lesson[chunk].size, lesson)
            }
        }
    }

    Row {
        ActionButton { chunk = BoundInt.add(chunk - 1, event.lesson.size) }
        ActionButton { chunk = BoundInt.add(chunk + 1, event.lesson.size) }
    }
}

@Composable
fun ScheduleHeader(
    dayWidth: Dp,
    minDate: LocalDate,
    maxDate: LocalDate,
    modifier: Modifier = Modifier,
    dayHeader: @Composable (day: LocalDate) -> Unit,
) {
    Row(modifier = modifier) {
        val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
        repeat(numDays) { i ->
            Box(modifier = Modifier.width(dayWidth)) {
                dayHeader(minDate.plusDays(i.toLong()))
            }
        }
    }
}

@Composable
fun ScheduleSidebar(
    hourHeight: Dp,
    minTime: LocalTime,
    maxTime: LocalTime,
    times: List<LocalTime>,
    modifier: Modifier = Modifier,
    label: @Composable (time: LocalTime) -> Unit,
) {
    Box(
        modifier = modifier
            .height((ChronoUnit.MINUTES.between(minTime, maxTime) / 60f * hourHeight.value).dp)
    ) {
        times.forEach {
            val y = ChronoUnit.MINUTES.between(minTime, it) / 60f * hourHeight.value - 12
            Box(modifier = Modifier.offset(y = y.dp)) {
                label(it)
            }
        }
    }
}

@Composable
fun BasicSchedule(
    modifier: Modifier = Modifier,
    events: List<Lesson>,
    eventContent: @Composable BoxScope.(positionedEvent: PositionedEvent) -> Unit,
    minDate: LocalDate,
    maxDate: LocalDate,
    minTime: LocalTime,
    maxTime: LocalTime,
    dayWidth: Dp,
    hourHeight: Dp,
    subjectHeight: Dp,
    gapWidth: Dp
) {
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
    val numMinutes = ChronoUnit.MINUTES.between(minTime, maxTime).toInt() + 1
    val dividerColor = if (MaterialTheme.colors.isLight) Color.LightGray else Color.DarkGray
    val positionedEvents = remember(events) {
        splitEvents(events.sortedBy(Lesson::startDate), hourHeight, subjectHeight)
            .filter { it.end > minTime && it.start < maxTime }
    }
    Layout(
        content = {
            positionedEvents.forEach { positionedEvent ->
                Box(modifier = Modifier.eventData(positionedEvent)) {
                    eventContent(positionedEvent)
                }
            }
        },
        modifier = modifier.drawBehind {
            positionedEvents.flatMap { listOf(it.start, it.end) }.distinct().forEach {
                val y = ChronoUnit.MINUTES.between(minTime, it) / 60f * hourHeight.toPx()
                drawLine(
                    dividerColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    ) { measureables, constraints ->
        val height = (hourHeight.toPx() * (numMinutes / 60f)).roundToInt()
        val width = (dayWidth + gapWidth).roundToPx() * numDays
        val placeablesWithEvents = measureables.map { measurable ->
            val splitEvent = measurable.parentData as PositionedEvent
            val eventDurationMinutes =
                ChronoUnit.MINUTES.between(splitEvent.start, minOf(splitEvent.end, maxTime))
            val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val eventWidth = dayWidth.toPx().roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = eventWidth,
                    maxWidth = eventWidth,
                    minHeight = eventHeight,
                    maxHeight = eventHeight
                )
            )
            Pair(placeable, splitEvent)
        }
        layout(width, height) {
            placeablesWithEvents.forEach { (placeable, splitEvent) ->
                val eventOffsetMinutes = if (splitEvent.start > minTime)
                    ChronoUnit.MINUTES.between(minTime, splitEvent.start) else 0
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                val eventOffsetDays = ChronoUnit.DAYS.between(minDate, splitEvent.date).toInt()
                val eventX =
                    eventOffsetDays * (dayWidth + gapWidth).roundToPx() + (gapWidth / 2).roundToPx()
                placeable.place(eventX, eventY)
            }
        }
    }
}

private fun splitEvents(
    events: List<Lesson>,
    hourHeight: Dp,
    subjectHeight: Dp
): List<PositionedEvent> = events
    .map { event ->
        val startDate = event.startDate.toLocalDate()
        val endDate = event.endDate.toLocalDate()
        if (startDate == endDate) return@map listOf(event)

        val days = ChronoUnit.DAYS.between(startDate, endDate)
        val splitEvents = mutableListOf<Lesson>()
        for (i in 0..days) {
            val date = startDate.plusDays(i)
            splitEvents += event.copy(
                startDate = if (date == startDate) event.startDate
                else LocalDateTime.of(date, LocalTime.MIN),
                endDate = if (date == startDate) event.endDate
                else LocalDateTime.of(date, LocalTime.MAX)
            )
        }
        splitEvents
    }
    .flatten()
    .groupBy { it.startDate to it.endDate }.values
    .map {
        val cellHeight = hourHeight.value *
                (ChronoUnit.MINUTES.between(it[0].startDate, it[0].endDate).toInt() / 60.0)

        PositionedEvent(
            lesson = it.chunked((cellHeight / subjectHeight.value).toInt()),
            date = it[0].startDate.toLocalDate(),
            start = it[0].startDate.toLocalTime(),
            end = it[0].endDate.toLocalTime()
        )
    }




