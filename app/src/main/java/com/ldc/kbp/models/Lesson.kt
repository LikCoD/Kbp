package com.ldc.kbp.models

import com.ldc.kbp.serializers.DateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Lesson(
    val id: String,
    val studyPlaceId: Int,
    val type: LessonType,
    @Serializable(DateTimeSerializer::class) val endDate: LocalDateTime,
    @Serializable(DateTimeSerializer::class) val startDate: LocalDateTime,
    val subject: String,
    val group: String,
    val teacher: String,
    val room: String,
    val title: String,
    val homework: String,
    val description: String
)

enum class LessonType {
    STAY,
    ADDED,
    REMOVED,
    GENERAL,
}