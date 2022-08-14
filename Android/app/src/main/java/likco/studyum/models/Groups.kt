package likco.studyum.models

object Groups {
    @kotlinx.serialization.Serializable
    data class Schedule(val name: String, val type: String) {
        override fun toString(): String = "$type/$name"
    }
}