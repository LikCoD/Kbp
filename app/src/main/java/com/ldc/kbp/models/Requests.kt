package com.ldc.kbp.models

import com.ldc.kbp.config
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jsoup.Connection
import org.jsoup.Jsoup


class Requests {
    companion object {
        val JSON = Json { ignoreUnknownKeys = true }

        inline fun <reified T> get(url: String): T? = try {
            val json = Jsoup.connect(url)
                .cookies(mapOf("authToken" to (config.token ?: "")))
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .execute()
                .bodyStream()

            JSON.decodeFromStream<T>(json)
        } catch (_: Exception) {
            null
        }

        inline fun <reified T, reified D> put(url: String, data: D): T? = try {
            val json = putConnection(url, data).bodyStream()

            JSON.decodeFromStream<T>(json)
        } catch (e: Exception) {
            null
        }

        inline fun <reified D> putConnection(url: String, data: D): Connection.Response {
            val body = JSON.encodeToString(data)

            return Jsoup.connect(url)
                .method(Connection.Method.PUT)
                .requestBody(body)
                .cookies(mapOf("authToken" to (config.token ?: "")))
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .execute()
        }
    }
}