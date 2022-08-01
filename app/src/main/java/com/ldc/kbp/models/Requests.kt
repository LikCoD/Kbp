package com.ldc.kbp.models

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL


class Requests {
    companion object {
        inline fun <reified T> get(url: String): T? {
            val json = Jsoup.connect(url)
                .cookies(mapOf("authToken" to "9ccc5ed9e7f053e691234b40268bf855c4d1fb01bd7d69ab740c7e0ee21e14d1ef9f905bd79b50f6af70018a4c12cd68a9733b6fc0b0f2e346432841e0a21c1edf1aaf1d58ca72b2b031ee6b120923e3086412097c24058a6d07054674b140a5434aab0e2feaeb5191023e4cd6a2e0c28964b5f1adfcf540f86efce967b04b5f"))
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .execute()
                .bodyStream()

            return Json { ignoreUnknownKeys = true }.decodeFromStream<T>(json)
        }
    }
}