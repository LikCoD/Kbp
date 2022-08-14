package likco.studyum.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val verifiedEmail: Boolean,
    val login: String,
    val name: String,
    val picture: String,
    val type: String,
    val typeName: String,
    val studyPlaceId: Int,
    val permissions: List<String>?,
    val accepted: Boolean,
    val blocked: Boolean
)
