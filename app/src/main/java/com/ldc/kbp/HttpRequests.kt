package com.ldc.kbp

import java.io.OutputStreamWriter
import java.net.*
import javax.net.ssl.HttpsURLConnection

class HttpRequests {

    fun get(url: String, params: String? = null): String {
        try {
            val client: HttpURLConnection = URL("$url?$params").openConnection() as HttpURLConnection

            client.requestMethod = "GET"
            client.setRequestProperty("User-Agent", "Mozilla/5.0")

            if (client.responseMessage != "OK") error("Bad answer")

            return client.inputStream.reader().readText()
        } catch (ex: UnknownHostException) {
            error(R.string.network_error)
        }
    }

    fun post(url: String, params: String? = null): String {
        try {
            val client = URL(url).openConnection() as HttpsURLConnection

            client.requestMethod = "POST"
            client.setRequestProperty("User-Agent", "Mozilla/5.0")

            client.doOutput = true
            if (params != null)
                OutputStreamWriter(client.outputStream).use { wr ->
                    wr.write(params)
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