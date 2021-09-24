package com.ldc.kbp.models

import com.ldc.kbp.getCurrentWeek
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

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
        var replacementLessons: MutableList<Lesson?>,
        var standardLessons: MutableList<Lesson?>,
        var state: UpdateState
    )

    @Serializable
    data class Lesson(
        var index: Int,
        var subjects: MutableList<Subject>
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

                                fun isSelectedGroup(pos: Int? = null) =
                                    if (Groups.categories.contains("преподаватель")) {
                                        if (pos == null) info.group == a[1].text() || info.group == a[2].text()
                                        else info.group == a[pos].text()
                                    } else false


                                fun getSubjects(replaced: Boolean): MutableList<Subject> {
                                    fun getSubject(pos: Int) = Subject(
                                        a[0].text(),
                                        a[pos].text(),
                                        a[4].text(),
                                        a[3].text(),
                                        replaced
                                    )

                                    val subjects = mutableListOf<Subject>()

                                    if (Groups.categories[info.categoryIndex] == "преподаватель") {
                                        when (info.group) {
                                            a[1].text() -> subjects.add(getSubject(1))
                                            a[2].text() -> subjects.add(getSubject(2))
                                        }
                                    } else {
                                        subjects.add(getSubject(1))

                                        if (a[2].text() != "")
                                            if (isSelectedGroup(2)) subjects.add(0, getSubject(2))
                                            else subjects.add(getSubject(2))
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
                            days.add(Day(mutableListOf(), mutableListOf(), status[dayIndex]))

                        days[dayIndex].replacementLessons.add(
                            if (replacementLessons.isEmpty()) null
                            else Lesson(lineIndex + 1, replacementLessons.toMutableList())
                        )
                        days[dayIndex].standardLessons.add(
                            if (standardLessons.isEmpty()) null
                            else Lesson(lineIndex + 1, standardLessons.toMutableList())
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