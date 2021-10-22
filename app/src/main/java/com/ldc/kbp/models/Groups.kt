package com.ldc.kbp.models

import kotlinx.serialization.Serializable
import org.jsoup.Jsoup

object Groups {
    @Serializable
    data class Timetable(val group: String, val link: String, val category: String)

    fun loadTimetable() {
        if (timetable.isEmpty()) {
            Jsoup.connect("http://kbp.by/rasp/timetable/view_beta_kbp/?q=").get()
                .getElementsByClass("block_back")[0].children().drop(1).map {
                val category = it.select("span").text()
                val a = it.select("a")

                timetable.add(Timetable(a.text(), a.attr("href"), category))
            }

            timetable.sortedBy { it.category }
            timetable = timetable.distinct().toMutableList()
        }
    }

    var timetable: MutableList<Timetable> = mutableListOf()
}