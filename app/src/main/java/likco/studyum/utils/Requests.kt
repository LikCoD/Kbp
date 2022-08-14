package likco.studyum.utils

import com.github.kittinunf.fuel.core.Request
import kotlinx.serialization.json.Json
import likco.studyum.services.UserService

/*lateinit */var API_URL: String = "https://studyum-api.herokuapp.com/api"

val JSON = Json {
    ignoreUnknownKeys = true
    isLenient = true
}
fun Request.appendCookie(name: String, cookie: String?): Request =
    if (cookie == null) this
    else appendHeader("Cookie", "$name=$cookie")

fun Request.withToken(token: String? = UserService.token) = appendCookie("authToken", token)