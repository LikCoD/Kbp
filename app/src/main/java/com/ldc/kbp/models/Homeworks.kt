package com.ldc.kbp.models

import kotlinx.serialization.Serializable

@Serializable
data class Homeworks(var days: MutableMap<String, Day> = mutableMapOf()) {
    @Serializable
    data class Day(val subjects: MutableMap<String, Homework> = mutableMapOf())
    @Serializable
    data class Homework(var homework: String = "", var subject: Schedule.Subject?)
}