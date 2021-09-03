package com.ldc.kbp.models.statements

import com.ldc.kbp.getString
import java.time.LocalDate

enum class Views(val text: String? = null) {
    DATE(LocalDate.now().getString()),
    TEXT,
    SPINNER,
    NONE
}