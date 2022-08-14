package likco.studyum.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import likco.studyum.models.Journal
import likco.studyum.models.Lesson
import likco.studyum.models.Mark
import likco.studyum.models.TopBarItem
import likco.studyum.services.JournalService
import likco.studyum.utils.format

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun JournalView(setTitle: (String) -> Unit, topBar: (List<TopBarItem>) -> Unit) {
    val scope = rememberCoroutineScope()

    var options by remember { mutableStateOf<List<Journal.Option>?>(null) }
    var journal by remember { mutableStateOf<Journal?>(null) }

    if (options == null) {
        setTitle("Journal")
        topBar(emptyList())
        scope.launch(Dispatchers.IO) {
            options = JournalService.getOptions { it.printStackTrace() }
        }
    }

    fun onSelect(option: Journal.Option) {
        scope.launch(Dispatchers.IO) {
            journal = JournalService.load(option) { it.printStackTrace() }
        }
    }

    if (options != null && journal == null) {
        setTitle("Journal")
        topBar(emptyList())

        if (options!!.size == 1) onSelect(options!![0])
        else JournalSelector(options = options!!, onSelect = ::onSelect)
    }

    if (journal != null) {
        setTitle("${journal!!.info.subject} ${journal!!.info.group}")
        topBar(listOf(
            TopBarItem(Icons.Default.ArrowBack, "back") {
                journal = null
            },
            TopBarItem(Icons.Default.Refresh, "refresh") {
                val i = journal?.info ?: return@TopBarItem
                onSelect(Journal.Option(i.teacher, i.subject, i.group, i.editable))
            }
        ))

        Journal(journal!!)
    }
}

@Composable
fun JournalSelector(options: List<Journal.Option>, onSelect: (Journal.Option) -> Unit) {
    LazyColumn {
        items(options) {
            Button(onClick = { onSelect(it) }) {
                Text(text = "${it.group}/${it.subject}/${it.teacher}")
            }
        }
    }
}

@Composable
fun Journal(journal: Journal, modifier: Modifier = Modifier) {
    val headerHeight = 55.dp
    val cellSize = 65.dp

    val state = rememberScrollState()

    Row(modifier) {
        BasicJournalBar(
            rows = journal.rows,
            cellSize = cellSize,
            modifier = Modifier
                .padding(top = headerHeight)
                .verticalScroll(state)
        )
        Column {
            BasicJournal(
                journal = journal,
                cellSize = cellSize,
                headerHeight = headerHeight,
                state = state
            )
        }
    }
}

@Composable
fun BasicJournalBar(rows: List<Journal.Row>, modifier: Modifier = Modifier, cellSize: Dp) {
    Column(modifier) {
        rows.forEach {
            JournalBarItem(row = it, modifier = Modifier.size(125.dp, cellSize))
        }
    }
}

@Composable
fun BasicJournal(journal: Journal, cellSize: Dp, headerHeight: Dp, state: ScrollState) {
    LazyRow {
        items(journal.dates.size) {
            Column {
                JournalHeaderItem(
                    date = journal.dates[it],
                    modifier = Modifier.size(cellSize, headerHeight)
                )
                Column(
                    Modifier.verticalScroll(state)
                ) {
                    journal.rows.forEach { row ->
                        JournalCell(row.lessons[it], cellSize)
                    }
                }
            }
        }
    }
}

@Composable
fun JournalHeaderItem(date: Lesson, modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Text(text = date.startDate.format("MMM\nE d"), textAlign = TextAlign.Center)
    }
}


@Composable
fun JournalBarItem(row: Journal.Row, modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Text(
            text = row.title,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun JournalCell(lesson: Lesson?, cellSize: Dp) {
    val primaryColor = MaterialTheme.colors.primary

    val color = when (lesson?.marks?.size) {
        null, 0 -> Color(0xFF505A64)
        else -> Color(0xFFF1F1F1)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(cellSize)
            .padding(7.5.dp)
            .background(color, RoundedCornerShape(15.dp))
    ) {
        when (lesson?.marks?.size) {
            null, 0 -> {}
            1 -> JournalMark1Cell(lesson.marks[0])
            2 -> JournalMark2Cell(lesson.marks[0], lesson.marks[1])
            else -> {
                JournalMark2Cell(lesson.marks[0], lesson.marks[1])
                Canvas(modifier = Modifier.align(Alignment.TopEnd)) {
                    journalMarkIndicator(primaryColor)
                }
            }
        }
    }
}

@Composable
fun BoxScope.JournalMark1Cell(mark: Mark) {
    Text(text = mark.mark, color = MaterialTheme.colors.onSecondary)
}

@Composable
fun BoxScope.JournalMark2Cell(mark1: Mark, mark2: Mark) {
    val color = MaterialTheme.colors.onSecondary
    val paddingVertical = 2.dp
    val paddingHorizontal = 17.dp

    Text(
        text = mark1.mark,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopStart)
            .padding(top = paddingVertical, end = paddingHorizontal)
    )

    Canvas(Modifier.fillMaxSize()) {
        val m = 20f
        drawLine(color, Offset(size.width - m, m), Offset(m, size.height - m), 3f)
    }

    Text(
        text = mark2.mark,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomEnd)
            .padding(bottom = paddingVertical, start = paddingHorizontal)
    )
}

fun DrawScope.journalMarkIndicator(color: Color) {
    drawCircle(color, 8f)
}