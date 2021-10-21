package com.ldc.kbp.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ldc.kbp.R
import com.ldc.kbp.getCurrentWeek
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.shortSnackbar
import com.ldc.kbp.mainTimetable
import kotlinx.android.synthetic.main.fragment_empty_room.view.*
import org.threeten.bp.LocalDate
import kotlin.concurrent.thread

class EmptyRoomFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(inflater.inflate(R.layout.fragment_empty_room, container, false)) {
            val freeRooms = mutableListOf<String>()
            val roomAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, freeRooms)

            val daysOfWeek = resources.getStringArray(R.array.days_of_weeks).dropLast(7 - mainTimetable.daysInWeek)

            day_of_week_spinner.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daysOfWeek)

            rooms_list.adapter = roomAdapter

            rooms_list.setOnItemClickListener { v, _, i, _ ->
                requireActivity().supportFragmentManager.beginTransaction().let {
                    it.replace(R.id.nav_host_fragment, TimetableFragment(Groups.timetable.find { it.group == v.getItemAtPosition(i).toString() }!!))
                    it.commit()
                }
            }

            if (LocalDate.now().dayOfWeek.ordinal <= mainTimetable.daysInWeek)
                day_of_week_spinner.setSelection(LocalDate.now().dayOfWeek.ordinal)

            week_index_edit.setText((getCurrentWeek(mainTimetable.weeks.size) + 1).toString())

            confirm_button.setOnClickListener {
                val lessonIndex = lesson_index_edit.text.toString().toIntOrNull() ?: 0
                val weekIndex = week_index_edit.text.toString().toIntOrNull() ?: 0

                if (lessonIndex > mainTimetable.lessonsInDay || lessonIndex <= 0) {
                    shortSnackbar(lesson_index_edit, R.string.error_lesson)
                    return@setOnClickListener
                }
                if (weekIndex > mainTimetable.weeks.size || weekIndex <= 0) {
                    shortSnackbar(week_index_edit, R.string.error_week)
                    return@setOnClickListener
                }

                info_layout.isVisible = false
                freeRooms.clear()

                fun isRoomOnFloor(floor: Int, group: Groups.Timetable): Boolean =
                    if (floor > 0) group.group.count { it.isDigit() } == 3 && group.group[0].digitToInt() == floor
                    else group.group.count { it.isDigit() } == 2

                val groups = Groups.timetable.filter {
                    Groups.categories[it.categoryIndex] == "аудитория"
                }.filter {
                    (isRoomOnFloor(0, it) && floor0_switcher.isChecked) ||
                            (isRoomOnFloor(1, it) && floor1_switcher.isChecked) ||
                            (isRoomOnFloor(2, it) && floor2_switcher.isChecked) ||
                            (isRoomOnFloor(3, it) && floor3_switcher.isChecked) ||
                            (isRoomOnFloor(4, it) && floor4_switcher.isChecked) ||
                            (isRoomOnFloor(5, it) && floor5_switcher.isChecked)
                }

                groups.forEach {
                    thread {
                        val timetable = Timetable.loadTimetable(it)
                        if (timetable.weeks[weekIndex - 1].days[day_of_week_spinner.selectedItemPosition].replacementLessons[lessonIndex - 1]?.subjects.isNullOrEmpty()) {
                            rooms_list.post { roomAdapter.add(it.group) }
                        }
                    }
                }
            }

            return this
        }
    }
}
