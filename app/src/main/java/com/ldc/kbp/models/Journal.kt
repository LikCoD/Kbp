package com.ldc.kbp.models

import com.ldc.kbp.HttpRequests
import com.ldc.kbp.JOURNAL_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

@Serializable
data class Journal(
    var subjects: List<Subject>,
    var dates: List<Date>
) {

    @Serializable
    data class Date(
        var month: Int,
        var dates: List<String>
    )

    @Serializable
    data class Subject(
        var name: String = "",
        var index: Int = 0,
        var months: MutableList<Month>,
        var laboratoryWorks: MutableList<LaboratoryWork>? = null
    )

    @Serializable
    data class LaboratoryWork(
        val date: List<String>,
        val info: String
    )

    @Serializable
    data class Month(
        var cells: List<Cell>
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
            "$JOURNAL_URL/ajax.php",
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

            val dates: MutableList<Date> = mutableListOf()
            val subjectsLine = trs.drop(2).dropLast(1)
            val subjects = names.mapIndexed { i, el -> Subject(el, i, mutableListOf()) }
            trs[0].select("td").dropLast(1).mapIndexed { monthIndex, month ->
                val daysInMonth = month.attr("colspan").toInt()
                val date = Date(
                    if (monthIndex + 9 > 12) monthIndex - 3 else monthIndex + 9,
                    List(daysInMonth) { datesNumbers[it + currentNumber].text() }
                )

                List(subjects.size) { i ->
                    val cells = List(daysInMonth) { index ->
                        var pairId = ""
                        var studentId = ""

                        val marks = subjectsLine[i].select("td")[index + currentNumber].select("div").flatMap { div ->
                            pairId = div.attr("pair-id")
                            studentId = div.attr("st-id")
                            div.select("span").map {
                                Mark(
                                    it.text(),
                                    it.attr("data-mark-id")
                                )
                            }
                        }.filter { it.mark != "" }.toMutableList()

                        Cell(index + currentNumber, pairId, studentId, marks)
                    }

                    subjects[i].months.add(Month(cells))
                }

                dates.add(date)
                currentNumber += daysInMonth
            }

            return Journal(subjects, dates)
        }

        fun parseJournal(document: Document): Journal {
            val tables = document.select("table")

            val subjectsNames = tables[0].select("td").drop(2).dropLast(1).map {
                val url = it.select("a").getOrNull(0)?.attr("href")?.substring(1)
                if (url != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val info = Jsoup.connect("$JOURNAL_URL/templates/$url").get()
                            .select("tbody")[1].select("tr")[2].select("span")
                        println(info)
                    }

                }

                it.text()
            }
            val trs = tables[1].select("tr")

            return parse(subjectsNames, trs)
        }

        fun parseTeacherJournal(marksTables: Document, laboratoryTables: Document): Journal {
            val laboratoryTable = laboratoryTables.select("table")[1]

            val tables = marksTables.select("table")
            val names = tables[0].getElementsByClass("pupilName").mapIndexed { i, el -> "${i + 1} ${el.text()}" }
            val trs = tables.getOrNull(1)?.select("tr")

            if (trs == null) {
                val subjects = mutableListOf<Subject>()

                names.forEachIndexed { index, it ->
                    subjects.add(Subject(it, index, mutableListOf()))
                }

                return Journal(subjects, listOf())
            }

            val datesNumbers = trs[1].select("td")
            var currentNumber = 0

            val laboratoryLine = laboratoryTable.select("tr").drop(2).dropLast(1)
            val laboratoryDatesNumbers = laboratoryTable.select("tr")[1].select("td")
            var laboratoryIndex = 0
            var allLaboratoryIndex = 0

            val dates: MutableList<Date> = mutableListOf()
            val subjectsLine = trs.drop(2).dropLast(1)
            val subjects = names.mapIndexed { i, el -> Subject(el, i, mutableListOf()) }
            trs[0].select("td").dropLast(1).mapIndexed { monthIndex, month ->
                val daysInMonth = month.attr("colspan").toInt()
                val date = Date(
                    if (monthIndex + 9 > 12) monthIndex - 3 else monthIndex + 9,
                    List(daysInMonth) { datesNumbers[it + currentNumber].text() }
                )

                val laboratoryMonthsTds = laboratoryTable.select("tr")[0].select("td").dropLast(2)

                val laboratoriesInMonth = if (monthIndex < laboratoryMonthsTds.size && laboratoryMonthsTds[monthIndex].text() == month.text()){
                    laboratoryMonthsTds[monthIndex].attr("colspan").toInt()
                }else 0

                val laboratoryDate = Date(
                    if (monthIndex + 9 > 12) monthIndex - 3 else monthIndex + 9,
                    List(laboratoriesInMonth) { laboratoryDatesNumbers[it + allLaboratoryIndex].text() }
                )

                List(subjects.size) { i ->
                    val cells = List(daysInMonth) { index ->
                        var pairId = ""
                        var studentId = ""

                        val marks = subjectsLine[i].select("td")[index + currentNumber].select("div").flatMap { div ->
                            pairId = div.attr("pair-id")
                            studentId = div.attr("st-id")
                            div.select("span").map {
                                Mark(
                                    it.text(),
                                    it.attr("data-mark-id")
                                )
                            }
                        }.filter { it.mark != "" }.toMutableList()

                        if (date.dates[index] == laboratoryDate.dates.getOrNull(laboratoryIndex)) {
                            marks.addAll(
                                laboratoryLine[i].select("td")[allLaboratoryIndex + laboratoryIndex].select("div").flatMap { div ->
                                        div.select("span").map {
                                            Mark(
                                                it.text(),
                                                it.attr("data-mark-id")
                                            )
                                        }
                                    }.filter { it.mark != "" }
                            )

                            laboratoryIndex++
                        }

                        Cell(index + currentNumber, pairId, studentId, marks)
                    }

                    subjects[i].months.add(Month(cells))


                    laboratoryIndex = 0
                }

                allLaboratoryIndex += laboratoryDate.dates.size

                dates.add(date)
                currentNumber += daysInMonth
            }

            return Journal(subjects, dates)
        }
    }
}