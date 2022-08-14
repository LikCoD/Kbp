package likco.studyum.services

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.serialization.responseObject
import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.onError
import kotlinx.serialization.serializer
import likco.studyum.models.Journal
import likco.studyum.models.Schedule
import likco.studyum.utils.API_URL
import likco.studyum.utils.JSON
import likco.studyum.utils.withToken

object ScheduleService {
    var options: List<Schedule.Option>? = null

    fun load(
        info: Schedule.Option?,
        error: (FuelError) -> Unit
    ): Schedule? {
        val (_, _, result) = "$API_URL/schedule/${info ?: "my"}"
            .httpGet()
            .withToken()
            .responseObject<Schedule>(JSON)

        return result.onError(error).getOrNull()
    }

    fun getOptions(error: (FuelError) -> Unit): List<Schedule.Option>? {
        val (_, _, result) = "$API_URL/schedule/types?studyPlaceId=${UserService.user?.studyPlaceId}"
            .httpGet()
            .withToken()
            .responseObject<List<Schedule.Option>>(serializer())

        options = result.onError(error).getOrNull()
        return options
    }
}