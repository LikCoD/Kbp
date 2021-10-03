package com.ldc.kbp.models

import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

@Serializable
data class Journal(
    var subjects: MutableList<Subject>,
    var dates: List<String>
) {

    @Serializable
    data class Subject(
        var name: String = "",
        var index: Int = 0,
        var cells: MutableList<Cell>
    )

    @Serializable
    data class Cell(
        var index: Int = 0,
        var marks: MutableList<Mark>
    )

    @Serializable
    data class Mark(
        var mark: String = ""
    )

    companion object {
        private fun parse(names: List<String>, trs: Elements): Journal {
            val dates = trs[0].select("td").dropLast(1).flatMapIndexed { index, element ->
                List(element.attr("colspan").toInt()) {
                    val monthNum = index + 9
                    if (monthNum > 12) monthNum - 12 else monthNum
                }
            }.zip(trs[1].select("td")) { month: Int, date: Element ->
                "${month}\n${date.text()}"
            }

            val subjects = mutableListOf<Subject>()

            trs.drop(2).dropLast(1).forEachIndexed { index, tr ->
                subjects.add(Subject(names[index], index, mutableListOf()))

                tr.select("td").dropLast(1).forEachIndexed { i, it ->
                    val marks = it.select("span").map { Mark(it.text()) }.filter { it.mark != "" }.toMutableList()
                    if (marks.count { it.mark == "н" } > 2) {
                        marks.removeIf { it.mark == "н" }
                        marks.add(Mark("н"))
                    }

                    marks.sortBy { it.mark.length }

                    subjects.last().cells.add(Cell(i, marks))
                }
            }

            return Journal(subjects, dates)
        }

        fun parseJournal(html: String): Journal {
            val tables = Jsoup.parse(html).select("table")

            val subjectsNames = tables[0].select("td").drop(2).dropLast(1).map { it.text() }
            val trs = tables[1].select("tr")

            return parse(subjectsNames, trs)
        }

        fun parseTeacherJournal(html: String): Journal {
            val tables = Jsoup.parse(html).select("table")

            val surnames = tables[0].select("tr").drop(8).dropLast(1).map { it.text() }
            val trs = tables[2].select("tr")

            return parse(surnames, trs)
        }
    }
}