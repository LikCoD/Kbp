package likco.studyum.services

import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.fuel.serialization.responseObject
import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.onError
import com.ldc.kbp.API_URL
import com.ldc.kbp.models.JSON
import com.ldc.kbp.models.withToken
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import likco.studyum.models.User

object UserService {
    var user: User? = null
    var token: String? = null
    private set

    private const val tokenCookieName = "authToken"

    private lateinit var preferences: SharedPreferences

    private fun saveToken(token: String?) {
        this.token = token
        preferences.edit(commit = true) {
            putString("authToken", token)
        }
    }

    fun setPreferences(preferences: SharedPreferences) {
        this.preferences = preferences
        token = preferences.getString("authToken", null)
    }

    fun load(error: (FuelError) -> Unit) {
        val (_, _, result) = "$API_URL/user"
            .httpGet()
            .withToken()
            .responseObject<User>(JSON)

        user = result.onError(error).getOrNull()
    }

    fun login(data: LoginData, error: (FuelError) -> Unit) {
        val body = JSON.encodeToString(data)
        val (_, response, result) = "$API_URL/user/login"
            .httpPut()
            .jsonBody(body)
            .responseObject<User>(JSON)

        user = result.onError(error).getOrNull()

        val token = response
            .header("Set-Cookie")
            .find { it.startsWith("${tokenCookieName}=") }
            ?.substring(tokenCookieName.length + 1)

        saveToken(token)
    }

    fun logout() {
        user = null
        saveToken(null)
    }

    @Serializable
    data class LoginData(val email: String, val password: String)
}