package com.ldc.kbp.models

import kotlinx.serialization.Serializable
import org.jsoup.Jsoup

object Groups {
    @Serializable
    data class Timetable(val group: String, val link: String, val categoryIndex: Int)
        fun loadTimetable(){
            if (timetable.isEmpty()) {
                Jsoup.connect("http://kbp.by/rasp/timetable/view_beta_kbp/?q=").get()
                    .getElementsByClass("block_back")[0].children().drop(1).map {
                    val category = it.select("span").text()
                    val a = it.select("a")

                    val index =
                        if (categories.contains(category)) categories.indexOf(category) else {
                            categories.add(category)
                            categories.size - 1
                        }

                    timetable.add(Timetable(a.text(), a.attr("href"), index))
                }

                timetable.sortedBy { it.categoryIndex }
            }
        }

        var timetable: MutableSet<Timetable> = mutableSetOf()
        var categories: MutableSet<String> = mutableSetOf()
}