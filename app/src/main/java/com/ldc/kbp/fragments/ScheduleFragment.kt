package com.ldc.kbp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.ldc.kbp.R
import com.ldc.kbp.composeView
import com.ldc.kbp.mainSchedule
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Lesson
import com.ldc.kbp.models.LessonType
import com.ldc.kbp.models.Schedule
import com.ldc.kbp.views.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import likco.libs.compose.Schedule
import java.time.format.DateTimeFormatter


class ScheduleFragment(private var info: Groups.Schedule? = null) : Fragment() {

    @OptIn(ExperimentalMaterialApi::class)
    @ExperimentalUnitApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeView(requireContext()) {
        val coroutineScope = rememberCoroutineScope()
        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
        )

        var schedule by remember { mutableStateOf<Schedule?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        update(info) {
            schedule = it
            isLoading = false

            if (info == null) info = Groups.Schedule(it.info.typeName, it.info.type)
        }

        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            backgroundColor = Color(0xFF434C5C),
            sheetPeekHeight = 60.dp,
            sheetElevation = 0.dp,
            sheetBackgroundColor = Color(0x66000000),
            sheetShape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
            sheetContent = {
                val fontSize = TextUnit(6f, TextUnitType.Em)

                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                ) {
                    val size = Size(200f, 15f)
                    val offset = Offset(center.x - size.width / 2, 10f)

                    this.drawRoundRect(Color(0xBFC8C8C8), offset, size, CornerRadius(15f, 15f))
                }
                if (isLoading) Text(
                    text = "Loading...",
                    color = Color.White,
                    fontSize = fontSize,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Transparent)
                )
                Text(
                    text = "Schedule",
                    color = Color.White,
                    fontSize = fontSize,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Transparent)
                )
                AndroidView(
                    factory = {
                        val view = inflater.inflate(R.layout.fragment_search, null, false)
                        SearchFragment(view,
                            Groups.timetable.map { it to Groups.getRusType(it) },
                            { it.name }) {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.collapse()
                            }

                            isLoading = true
                            update(it) { s -> schedule = s; isLoading = false }
                        }

                        view
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                )
            },
        ) {
            Column {
                Row {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search",
                        tint = Color.White,
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.collapse()
                            }
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "refresh",
                        tint = Color.White,
                        modifier = Modifier.clickable {
                            isLoading = true
                            update(info) { schedule = it; isLoading = false }
                        }
                    )
                }
                if (schedule != null)
                    Schedule(
                        events = schedule!!.lessons,
                        dayHeader = {
                            Text(
                                text = it.format(DateTimeFormatter.ofPattern("EEEE")),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        timeLabel = {
                            Text(
                                text = it.format(DateTimeFormatter.ofPattern("h:mm a")),
                                color = Color.White,
                            )
                        },
                        moreIndicator = { drawCircle(Color(0xFFE6BA92), 12f) },
                        eventContent = { i, len, l ->
                            LessonContainer(
                                i = i,
                                len = len,
                                lesson = l
                            )
                        },
                        daySize = 190.dp,
                        hourSize = 120.dp,
                        subjectSize = 90.dp,
                        gapSize = 20.dp
                    )
            }
        }
    }

    private fun update(info: Groups.Schedule? = null, onScheduleUpdate: (Schedule) -> Unit) {
        MainScope().launch {
            launch(Dispatchers.IO) {
                val schedule = Schedule.load(info)

                if (this@ScheduleFragment.info == null) mainSchedule = schedule
                this@ScheduleFragment.info = info

                launch(Dispatchers.Main) {
                    requireActivity().toolbar.title = schedule.info.typeName
                }

                onScheduleUpdate(schedule)
            }
        }
    }

    @Composable
    fun LessonContainer(i: Int, len: Int, lesson: Lesson) {
        val radius = 25.dp

        val shape = when {
            i == 0 && len == 1 -> RoundedCornerShape(radius)
            i == 0 -> RoundedCornerShape(topStart = radius, topEnd = radius)
            i == len - 1 -> RoundedCornerShape(bottomStart = radius, bottomEnd = radius)
            else -> RoundedCornerShape(0)
        }

        val color = when (lesson.type) {
            LessonType.STAY -> Color(0xFFF1F1F1)
            LessonType.ADDED -> Color(0xFF71AB7F)
            LessonType.REMOVED -> Color(0xFFFA6F46)
            LessonType.GENERAL -> Color(0xFFB4B4B4)
        }

        Box(
            modifier = Modifier
                .background(color, shape)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(90.dp)
                    .fillMaxSize()
                    .padding(vertical = 10.dp, horizontal = 15.dp)
            ) {
                Text(text = lesson.subject, fontWeight = FontWeight.Bold)
                Text(text = lesson.teacher)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = lesson.room)
                    Text(text = lesson.group)
                }
            }
        }
    }
}
