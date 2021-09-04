package com.ldc.kbp.models

data class DayOfWeek(val weekIndex: Int, val dayIndex: Int) {
    companion object{
        fun create(timetable: Timetable): MutableList<DayOfWeek> {
            val daysOfWeek = mutableListOf<DayOfWeek>()

            timetable.weeks.forEachIndexed { index, _ ->
                (0..timetable.daysInWeek).forEach {
                    daysOfWeek.add(DayOfWeek(index, it))
                }
            }

            return daysOfWeek
        }
    }
}