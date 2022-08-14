package likco.studyum.models

import kotlinx.serialization.Serializable

@Serializable
data class Journal(val info: Info, val rows: List<Row>, val dates: List<Lesson>) {
    @Serializable
    data class Info(
        val editable: Boolean,
        val studyPlaceId: Int,
        val group: String,
        val teacher: String,
        val subject: String
    )

    @Serializable
    data class Row(
        val id: String,
        val title: String,
        val userType: String,
        val lessons: List<Lesson?>
    )

    @Serializable
    data class Option(
        val teacher: String,
        val subject: String,
        val group: String,
        val editable: Boolean
    ) {
        override fun toString(): String =
            if (teacher == "") "" else "${group}/${subject}/${teacher}"
    }
}
