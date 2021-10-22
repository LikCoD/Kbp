package com.ldc.kbp.models

import com.ldc.kbp.getCurrentWeek
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.threeten.bp.LocalDate

@Serializable
data class Timetable(
    var weeks: MutableList<Week> = mutableListOf(),
    val info: Groups.Timetable? = null
) {

    @Serializable
    data class Week(
        var days: MutableList<Day>
    )

    @Serializable
    data class Day(
        var replacementLessons: MutableList<Lesson>,
        var standardLessons: MutableList<Lesson>
    )

    @Serializable
    data class Lesson(
        var index: Int,
        var state: UpdateState,
        var subjects: MutableList<Subject>? = null
    )

    @Serializable
    data class Subject(
        var subject: String,
        var teacher: String,
        var room: String,
        var group: String,
        var isReplaced: Boolean = false
    )

    enum class UpdateState(val value: String) {
        REPLACEMENT("Змена"),
        NO_REPLACEMENT("Замен нет"),
        NOT_UPDATED("Не обновлено")
    }

    val daysInWeek: Int
        get() = weeks.getOrNull(0)?.days?.size ?: 0

    val lessonsInDay: Int
        get() = weeks.getOrNull(0)?.days?.getOrNull(0)?.replacementLessons?.size ?: 0

    val weeksCount: Int
        get() = weeks.size

    val firstLessonIndex: Int
        get() {
            val days = weeks[getCurrentWeek(weeksCount)].days
            val day = days.getOrElse(LocalDate.now().dayOfWeek.ordinal) { days[0] }

            return day.replacementLessons.indexOfFirst { it.subjects != null }
        }

    companion object {
        fun loadTimetable(info: Groups.Timetable): Timetable {
            val body =
                Jsoup.connect("http://kbp.by/rasp/timetable/view_beta_kbp/${info.link}").get()
                    .body()
            val htmlWeeks = body.getElementsByClass("find_block")[0].child(1).children()

            val weeks = mutableListOf<Week>()
            htmlWeeks.forEach { htmlWeek ->
                val days = mutableListOf<Day>()
                val status = mutableListOf<UpdateState>()

                var replacement: Elements
                var timeTableLines: List<Element>
                htmlWeek.select("table").select("tr").apply {
                    replacement = get(1).select("th")
                    timeTableLines = drop(2)
                }

                replacement.drop(1).dropLast(1).forEach {
                    when (it.text()) {
                        "Показать замены" -> status.add(UpdateState.REPLACEMENT)
                        "Замен нет" -> status.add(UpdateState.NO_REPLACEMENT)
                        else -> status.add(UpdateState.NOT_UPDATED)
                    }
                }

                timeTableLines.forEachIndexed { lineIndex, line ->
                    line.select("td").drop(1).dropLast(1).forEachIndexed { dayIndex, htmlDay ->
                        val replacementLessons = mutableSetOf<Subject>()
                        val standardLessons = mutableSetOf<Subject>()

                        htmlDay.children().forEach { htmlSubject ->
                            if (htmlSubject.className() != "empty-pair") {
                                val a = htmlSubject.select("a")

                                fun getSubjects(replaced: Boolean): MutableList<Subject> {
                                    fun getSubject(pos: Int) = Subject(
                                        a[0].text(),
                                        a[pos].text(),
                                        a[4].text(),
                                        a[3].text(),
                                        replaced
                                    )

                                    val subjects = mutableListOf<Subject>()

                                    if (info.category == "преподаватель") {
                                        when (info.group) {
                                            a[1].text() -> subjects.add(getSubject(1))
                                            a[2].text() -> subjects.add(getSubject(2))
                                        }
                                    } else {
                                        if (a[1].text() != "") subjects.add(getSubject(1))
                                        if (a[2].text() != "") subjects.add(getSubject(2))
                                    }

                                    return subjects
                                }

                                val replacementSubjects = mutableSetOf<Subject>()
                                val standardSubjects = mutableSetOf<Subject>()

                                val className = htmlSubject.className()
                                when {
                                    className.contains("added") ->
                                        replacementSubjects.addAll(getSubjects(true))
                                    className.contains("removed") && status[dayIndex] != UpdateState.NOT_UPDATED ->
                                        standardSubjects.addAll(getSubjects(false))
                                    else -> {
                                        replacementSubjects.addAll(getSubjects(false))
                                        standardSubjects.addAll(getSubjects(false))
                                    }
                                }

                                replacementLessons.addAll(replacementSubjects)
                                standardLessons.addAll(standardSubjects)
                            }
                        }

                        if (lineIndex == 0)
                            days.add(Day(mutableListOf(), mutableListOf()))

                        days[dayIndex].replacementLessons.add(
                            if (replacementLessons.isEmpty()) Lesson(lineIndex + 1, status[dayIndex])
                            else Lesson(lineIndex + 1, status[dayIndex], replacementLessons.toMutableList())
                        )
                        days[dayIndex].standardLessons.add(
                            if (standardLessons.isEmpty()) Lesson(lineIndex + 1, status[dayIndex])
                            else Lesson(lineIndex + 1, status[dayIndex], standardLessons.toMutableList())
                        )
                    }
                }
                weeks.add(Week(days))
            }

            if (getCurrentWeek(weeks.size) == 1)
                weeks.reverse()

            return Timetable(weeks, info)
        }
    }
}