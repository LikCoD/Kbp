package likco.studyum.services

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.serialization.responseObject
import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.onError
import likco.studyum.utils.JSON
import likco.studyum.utils.withToken
import kotlinx.serialization.serializer
import likco.studyum.models.Journal
import likco.studyum.utils.API_URL

object JournalService {
    fun load(
        option: Journal.Option,
        error: (FuelError) -> Unit
    ): Journal? {
        val (_, _, result) = "$API_URL/journal/${option.group}/${option.subject}/${option.teacher}"
            .httpGet()
            .withToken()
            .responseObject<Journal>(JSON)

        return result.onError(error).getOrNull()
    }

    fun getOptions(error: (FuelError) -> Unit): List<Journal.Option>? {
        val (_, _, result) = "$API_URL/journal/options"
            .httpGet()
            .withToken()
            .responseObject<List<Journal.Option>>(serializer())

        return result.onError(error).getOrNull()
    }
}