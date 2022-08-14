package likco.studyum.models

import kotlinx.serialization.Serializable

@Serializable
data class StudyPlace(
    val id: Int,
    val weeksCount: Int,
    val daysCount: Int,
    val name: String
)
