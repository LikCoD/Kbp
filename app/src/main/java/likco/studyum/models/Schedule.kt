package likco.studyum.models

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
    data class Option(val name: String, val type: String) {
        val category: Category
            get() {
                val title = when (type) {
                    "room" -> "Room"
                    "group" -> "Group"
                    "subject" -> "Subject"
                    "teacher" -> "Teacher"
                    else -> "Unknown type"
                }

                return Category(type, title)
            }

        override fun toString(): String = "$type/$name"
    }
}