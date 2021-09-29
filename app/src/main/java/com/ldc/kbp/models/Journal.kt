package com.ldc.kbp.models

import kotlinx.serialization.Serializable
import org.jsoup.Jsoup

@Serializable
data class Journal(
    var months: MutableList<Month> = mutableListOf()
) {

    @Serializable
    data class Month(
        var name: String = "",
        var subjects: MutableList<Subject>
    )

    @Serializable
    data class Subject(
        var name: String = "",
        var cells: MutableList<Cell>
    )

    @Serializable
    data class Cell(
        val date: String = "",
        var marks: MutableList<Mark>
    )

    @Serializable
    data class Mark(
        var mark: String = ""
    )

    companion object {
        fun parseJournal(html: String): Journal {
            val tables = Jsoup.parse(html).select("table")
            val subjectsNames = tables[0].select("td").drop(2).dropLast(1).map { it.text() }

            val trs = tables[1].select("tr")
            val days = trs[0].select("td").dropLast(1)
                .map {
                    it.text().replaceFirstChar { c -> c.uppercaseChar() } to
                            it.attr("colspan").toInt()
                }

            val subjects = mutableListOf<Subject>()

            trs.drop(2).dropLast(1).forEachIndexed { index, tr ->
                subjects.add(Subject(subjectsNames[index], mutableListOf()))

                tr.select("td").dropLast(1).forEachIndexed { i, it ->
                    val date = trs[1].select("td")[i].text()
                    val marks = it.select("span").map { Mark(it.text()) }
                    subjects.last().cells.add(Cell(date, marks.toMutableList()))
                }
            }

            var startSubs = 0
            return Journal(days.map { day ->
                Month(
                    day.first,
                    subjects
                        .map { Subject(it.name, it.cells.subList(startSubs, day.second)) }
                        .toMutableList()
                ).also { startSubs += day.second }
            }.toMutableList())
        }
    }
}