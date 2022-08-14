package likco.studyum.models

import kotlinx.serialization.Serializable
import likco.studyum.utils.serializers.DateTimeSerializer
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
    val description: String,
    val marks: List<Mark>? = null
)

@Serializable
data class Mark(
    val id: String,
    val mark: String,
    val userId: String,
    val lessonId: String,
    val studyPlaceId: Int
)

enum class LessonType {
    STAY,
    ADDED,
    REMOVED,
    GENERAL,
}