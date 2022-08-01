package com.ldc.kbp.models

import com.ldc.kbp.API_URL
import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val lessons: List<Lesson>,
    val info: Info
) {
    @Serializable
    data class Info(
        val type: String,
        val typeName: String,
        val studyPlace: StudyPlace,
        val date: String
    )

    @Serializable
    enum class Type {
        STAY,
        ADDED,
        REMOVED
    }

    companion object {
        fun load(info: Groups.Schedule?): Schedule =
            Requests.get<Schedule>("$API_URL/schedule/${info ?: "my"}")!!
    }
}
