package com.ldc.kbp.models

import android.content.Context
import com.ldc.kbp.config
import com.ldc.kbp.homeworkList
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Files {
    fun getConfig(context: Context) {
        config = fromJson(context, "config") ?: Config()
    }

    fun saveConfig(context: Context) = toJson(context, config, "config")

    fun getHomeworkList(context: Context) {
        homeworkList = fromJson(context, "homework") ?: Homeworks()
    }

    fun saveHomeworkList(context: Context) = toJson(context, homeworkList, "homework")

    private fun saveData(context: Context, path: String, data: Any) {
        try {
            val outputStream = context.openFileOutput(path, Context.MODE_PRIVATE)
            outputStream.write(data.toString().toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadData(context: Context, path: String): String? {
        var data: String? = null
        try {
            val inputStream = context.openFileInput(path)
            data = String(inputStream.readBytes())
            inputStream.close()
        } catch (e: Exception) {
        }
        return data
    }

    private inline fun <reified T> toJson(context: Context, obj: T, file: String) =
        saveData(context, "$file.json", Json.encodeToString(obj))

    private inline fun <reified T> fromJson(context: Context, file: String) =
        loadData(context, "$file.json")?.let {
            Json.decodeFromString<T>(it)
        }
}