package com.ldc.kbp

import java.io.OutputStreamWriter
import java.net.*
import likco.studyum.R
import javax.net.ssl.HttpsURLConnection

class HttpRequests {

    fun get(url: String, vararg params: Pair<String, String> = emptyArray()): String {
        try {
            val paramsList = params.joinToString("&"){"${it.first}=${it.second}"}

            val client = URL("$url?$paramsList").openConnection() as HttpURLConnection

            client.requestMethod = "GET"
            client.setRequestProperty("User-Agent", "Mozilla/5.0")

            if (client.responseMessage != "OK") error("Bad answer")

            return client.inputStream.reader().readText()
        } catch (ex: UnknownHostException) {
            error(R.string.network_error)
        }
    }

    fun post(url: String, vararg params: Pair<String, String> = emptyArray()): String {
        try {
            val client = URL(url).openConnection() as HttpsURLConnection

            client.requestMethod = "POST"
            client.setRequestProperty("User-Agent", "Mozilla/5.0")

            client.doOutput = true
            if (params.isNotEmpty())
                OutputStreamWriter(client.outputStream).use { wr ->
                    val paramsLine = params.joinToString("&"){"${it.first}=${it.second}"}

                    wr.write(paramsLine)
                    wr.flush()
                }

            if (client.responseMessage != "OK") error("Bad answer")

            return client.inputStream.reader().readText()
        } catch (ex: UnknownHostException) {
            error(R.string.network_error)
        }
    }

    init {
        val cookieManager = CookieManager()
        CookieHandler.setDefault(cookieManager)
    }
}