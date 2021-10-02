package com.ldc.kbp.models

import kotlinx.serialization.Serializable
import org.jsoup.Jsoup

@Serializable
data class JournalTeacherSelector(
    var groups: MutableMap<Group, Subjects> = mutableMapOf()
) {

    @Serializable
    data class Group(
        var name: String = "",
        var index: Int
    )

    @Serializable
    data class Subject(
        var name: String = "",
        val index: Int,
    )

    @Serializable
    data class Subjects(
        val subjects: List<Subject>
    )

    companion object {
         fun parseTeacherSelector(html: String): JournalTeacherSelector {
             val months = mutableMapOf<Group, Subjects>()

             val uls = Jsoup.parse(html).select("ul").drop(1)
             val groups = uls[0].select("li").mapIndexed { i, element ->  Group(element.text(), i) }

             uls.drop(1).forEachIndexed { index, el ->
                 months[groups[index]] = Subjects(el.select("li").mapIndexed { i, element -> Subject(element.text(), i) })
             }

             return JournalTeacherSelector(months)
         }
    }
}