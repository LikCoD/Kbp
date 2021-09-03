package com.ldc.kbp.models

import androidx.appcompat.app.AppCompatDelegate
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    var theme: Int = AppCompatDelegate.MODE_NIGHT_YES,
    var link: String = "?cat=group&id=66",
    var isStudent: Boolean = true,
    var groupId: String = "",
    var password: String = "",
    var surname: String = "",
    var isFemale: Boolean = true,
    var department: String = "",
    var group: String = "",
    var sapperSizeX: Int = 10,
    var sapperSizeY: Int = 15,
    var sapperMinesCount: Int = 25
)