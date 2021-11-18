package com.ldc.kbp.models

import com.ldc.kbp.HttpRequests
import kotlinx.serialization.Serializable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

@Serializable
data class Journal(
    var subjects: MutableList<Subject>,
    var dates: List<Date>
) {

    @Serializable
    data class Date(
        var month: Int,
        var dates: List<Int>
    )

    @Serializable
    data class Subject(
        var name: String = "",
        var index: Int = 0,
        var cells: MutableList<Cell>
    )

    @Serializable
    data class Cell(
        var index: Int = 0,
        val pairId: String,
        val studentId: String,
        var marks: MutableList<Mark>
    )

    @Serializable
    data class Mark(
        var mark: String = "",
        val markId: String
    ) {
        fun remove(requests: HttpRequests, cell: Cell) = requests.post(
            "https://nehai.by/ej/ajax.php",
            "action" to "set_mark",
            "student_id" to cell.studentId,
            "pair_id" to cell.pairId,
            "mark_id" to markId,
            "value" to "X"
        )
    }

    companion object {
        private fun parse(names: List<String>, trs: Elements): Journal {
            val datesNumbers = trs[1].select("td")
            var currentNumber = 0

            val dates = trs[0].select("td").dropLast(1).mapIndexed { index, element ->
                val daysInMonth = element.attr("colspan").toInt()
                val monthNum = if (index + 9 > 12) index - 3 else index + 9

                Date(monthNum, List(daysInMonth) {
                    datesNumbers[it + currentNumber].text().toInt()
                }).also { currentNumber += daysInMonth }
            }

            val subjects = mutableListOf<Subject>()

            trs.drop(2).dropLast(1).forEachIndexed { index, tr ->
                subjects.add(Subject(names[index], index, mutableListOf()))

                tr.select("td").dropLast(1).forEachIndexed { i, td ->
                    var pairId = ""
                    var studentId = ""

                    val marks = td.select("div").flatMap { div ->
                        pairId = div.attr("pair-id")
                        studentId = div.attr("st-id")
                        div.select("span").map {
                            Mark(
                                it.text(),
                                it.attr("data-mark-id")
                            )
                        }
                    }.filter { it.mark != "" }.toMutableList()

                    marks.sortBy { it.mark.length }

                    subjects.last().cells.add(Cell(i, pairId, studentId, marks))
                }
            }

            return Journal(subjects, dates)
        }

        fun parseJournal(document: Document): Journal {
            val tables = document.select("table")

            val subjectsNames = tables[0].select("td").drop(2).dropLast(1).map { it.text() }
            val trs = tables[1].select("tr")

            return parse(subjectsNames, trs)
        }

        fun parseTeacherJournal(document: Document): Journal {
            val tables = document.select("table")

            val surnames =
                tables[0].select("tr").drop(2).dropLast(1).map { it.text() }.toMutableList()
            surnames.remove("Показать лабораторные")

            val trs = tables.getOrNull(1)?.select("tr")

            if (trs == null) {
                val subjects = mutableListOf<Subject>()

                surnames.forEachIndexed { index, it ->
                    subjects.add(Subject(it, index, mutableListOf()))
                }

                return Journal(subjects, listOf())
            }

            return parse(surnames, trs)
        }
    }
}