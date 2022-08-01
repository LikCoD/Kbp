package com.ldc.kbp.models

import com.ldc.kbp.API_URL
import com.ldc.kbp.JOURNAL_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jsoup.Jsoup
import java.net.URL

object Groups {
    @Serializable
    data class Schedule(val name: String, val type: String) {
        override fun toString(): String = "$type/$name"
    }

    @Serializable
    data class SimpleInfo(val name: String, val id: String)

    fun loadTimetable() {
        timetable = Json.decodeFromStream(URL("$API_URL/schedule/types?studyPlaceId=0").openStream())
    }

    fun loadGroupsFromJournal() {
        groupsJournal = parseJournalViaUrl("$JOURNAL_URL/templates/login_parent.php")
    }

    fun getRusType(schedule: Schedule) =
        when (schedule.type){
            "room" -> "Кабинет"
            "group" -> "Группа"
            "teacher" -> "Учитель"
            "subject" -> "Предмет"
            else -> "-"
        }


    fun loadTeachersFromJournal() {
        teachersJournal = parseJournalViaUrl("$JOURNAL_URL/templates/login_teacher.php")
    }

    private fun parseJournalViaUrl(url: String): List<SimpleInfo> = runBlocking(Dispatchers.IO) {
        Jsoup.connect(url).get().select("option").map { SimpleInfo(it.text(), it.attr("value")) }
    }

    var timetable: List<Schedule> = emptyList()

    var groupsJournal: List<SimpleInfo> = emptyList()
    var teachersJournal: List<SimpleInfo> = emptyList()
}