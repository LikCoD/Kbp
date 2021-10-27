package com.ldc.kbp.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup

object Groups {
    @Serializable
    data class Timetable(val group: String, val link: String, val category: String)

    @Serializable
    data class SimpleInfo(val name: String, val id: String)

    fun loadTimetable() {
        val timetableMutableList = mutableListOf<Timetable>()

        Jsoup.connect("http://kbp.by/rasp/timetable/view_beta_kbp/?q=").get()
            .getElementsByClass("block_back")[0].children().drop(1).map {
            val category = it.select("span").text()
            val a = it.select("a")

            timetableMutableList.add(Timetable(a.text(), a.attr("href"), category))
        }

        timetable = timetableMutableList.distinct()
    }

    fun loadGroupsFromJournal() {
        groupsJournal = parseJournalViaUrl("https://nehai.by/ej/templates/login_parent.php")
    }

    fun loadTeachersFromJournal() {
        teachersJournal = parseJournalViaUrl("https://nehai.by/ej/templates/login_teacher.php")
    }

    private fun parseJournalViaUrl(url: String): List<SimpleInfo> = runBlocking(Dispatchers.IO) {
        Jsoup.connect(url).get().select("option").map { SimpleInfo(it.text(), it.attr("value")) }
    }

    var timetable: List<Timetable> = emptyList()

    var groupsJournal: List<SimpleInfo> = emptyList()
    var teachersJournal: List<SimpleInfo> = emptyList()
}