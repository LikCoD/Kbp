package com.ldc.kbp.models

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    var link: String = "?cat=group&id=66",
    var timetableInfo: Groups.Timetable = Groups.Timetable("Т-095", "?cat=group&id=66", "группа"),
    var isStudent: Boolean = true,
    var groupId: String = "",
    var password: String = "",
    var surname: String = "",
    var multiWeek: Boolean = true,
    var isFemale: Boolean = true,
    var department: String = "",
    var group: String = "",
    var sapperSizeX: Int = 10,
    var sapperSizeY: Int = 15,
    var sapperMinesCount: Int = 25
)