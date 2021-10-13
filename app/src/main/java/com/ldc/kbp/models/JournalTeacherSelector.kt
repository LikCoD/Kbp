package com.ldc.kbp.models

import kotlinx.serialization.Serializable
import org.jsoup.nodes.Document

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
        val subjectId: String,
        val groupId: String,
    )

    @Serializable
    data class Subjects(
        val index: Int,
        val subjects: List<Subject>
    )

    companion object {
        fun parseTeacherSelector(document: Document): JournalTeacherSelector {
            val groups = mutableMapOf<Group, Subjects>()

            val uls = document.select("ul")
            val groupsName = uls[0].select("li").mapIndexed { i, element -> Group(element.text(), i) }

            uls.drop(1).forEachIndexed { index, el ->
                groups[groupsName[index]] =
                    Subjects(index, el.select("li")
                        .map { Subject(it.text(), it.attr("subjectid"), it.attr("groupid")) })
            }

            return JournalTeacherSelector(groups)
        }
    }
}