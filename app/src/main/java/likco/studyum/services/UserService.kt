package likco.studyum.services

import com.ldc.kbp.API_URL
import com.ldc.kbp.config
import com.ldc.kbp.models.Requests
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import likco.studyum.models.User

object UserService {
    var user: User? = null

    fun load() {
        user = Requests.get<User>("$API_URL/user")
    }

    fun login(login: String, password: String) {
        val connection = Requests.putConnection("$API_URL/user/login", LoginData(login, password))
        config.token = connection.cookie("authToken")

        try {
            user = Requests.JSON.decodeFromStream<User>(connection.bodyStream())
        } catch (_: Exception) {
        }
    }

    @Serializable
    private data class LoginData(val email: String, val password: String)
}