package com.ldc.kbp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ldc.kbp.*
import com.ldc.kbp.models.Groups
import com.ldc.kbp.views.adapters.emptyroom.FloorSwitcherAdapter
import kotlinx.android.synthetic.main.fragment_empty_room.view.*
import org.threeten.bp.LocalDate
import kotlin.concurrent.thread

class EmptyRoomFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_empty_room, container, false).apply {
      /*  val freeRooms = mutableListOf<String>()
        val roomAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, freeRooms)
        val floorAdapter = FloorSwitcherAdapter(requireContext(), 6)

        val daysOfWeek =
            resources.getStringArray(R.array.days_of_weeks).dropLast(7 - mainSchedule.info.daysCount)

        day_of_week_spinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daysOfWeek)
        rooms_list.adapter = roomAdapter
        empty_room_floor_recycler.adapter = floorAdapter

        rooms_list.setOnItemClickListener { v, _, i, _ ->
            requireActivity().supportFragmentManager.beginTransaction().let { transaction ->
                transaction.replace(
                    R.id.nav_host_fragment,
                    ScheduleFragment(Groups.timetable.find {
                        it.name == v.getItemAtPosition(i).toString()
                    }!!)
                )
                transaction.commit()
            }
        }

        if (LocalDate.now().dayOfWeek.ordinal < mainSchedule.info.daysCount)
            day_of_week_spinner.setSelection(LocalDate.now().dayOfWeek.ordinal)

        week_index_edit.setText((getCurrentWeek() + 1).toString())

        confirm_button.setOnClickListener { _ ->
            val lessonIndex = lesson_index_edit.text.toString().toIntOrNull() ?: 0
            val weekIndex = week_index_edit.text.toString().toIntOrNull() ?: 0

            if (lessonIndex > mainSchedule.info.subjectsCount || lessonIndex <= 0) {
                shortSnackbar(lesson_index_edit, R.string.error_lesson)
                return@setOnClickListener
            }
            if (weekIndex > mainSchedule.info.weeksCount || weekIndex <= 0) {
                shortSnackbar(week_index_edit, R.string.error_week)
                return@setOnClickListener
            }

            info_layout.isVisible = false
            freeRooms.clear()

            fun isRoomOnFloor(floor: Int, group: Groups.Schedule): Boolean =
                if (floor > 0) group.name.count { it.isDigit() } == 3 && group.name[0].digitToInt() == floor
                else group.name.count { it.isDigit() } == 2

            val groups = Groups.timetable.filter { room ->
                room.type == "room" && (0..5).any {
                    isRoomOnFloor(it, room) && floorAdapter.switchers[it].isChecked
                }
            }

            groups.forEach {
                thread {
                    //TODO
*//*                    val schedule = Schedule.load(it.type, it.name)
                    val subject =
                        schedule.subjects[((weekIndex - 1) * schedule.info.daysCount + day_of_week_spinner.selectedItemPosition) * schedule.info.subjectsCount + lessonIndex - 1]
                    if (subject == null || subject.subjects.all { it.type == "REMOVED" })
                        rooms_list.post { roomAdapter.add(it.name) }*//*
                }
            }
        }*/
    }
}
