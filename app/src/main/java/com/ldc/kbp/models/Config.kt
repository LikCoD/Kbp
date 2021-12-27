package com.ldc.kbp.models

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    var scheduleInfo: Groups.Schedule = Groups.Schedule("Т-095", "group"),
    var isStudent: Boolean = true,
    var groupId: String = "435",
    var password: String = "",
    var surname: String = "",
    var multiWeek: Boolean = true,
    var multiMonth: Boolean = true,
    var isFemale: Boolean = true,
    var department: String = "",
    var group: String = "",
    var sapperSizeX: Int = 10,
    var sapperSizeY: Int = 15,
    var sapperMinesCount: Int = 25
)