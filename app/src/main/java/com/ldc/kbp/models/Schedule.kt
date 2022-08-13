package com.ldc.kbp.models

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.serialization.responseObject
import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.onError
import com.ldc.kbp.API_URL
import kotlinx.serialization.Serializable
import likco.studyum.models.Lesson

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

    companion object {
        fun load(info: Groups.Schedule?, error: (FuelError) -> Unit): Schedule? {
            val (_, _, result) = "$API_URL/schedule/${info ?: "my"}"
                .httpGet()
                .withToken()
                .responseObject<Schedule>(JSON)

            return result.onError(error).getOrNull()
        }
    }
}
