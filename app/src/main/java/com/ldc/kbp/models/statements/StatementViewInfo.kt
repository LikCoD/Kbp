package com.ldc.kbp.models.statements

data class StatementViewInfo(
    val description: String,
    val view: Views,
    val statementTextBefore: String = "",
    val statementTextAfter: String = "",
    val nextParagraph: Boolean = false,
    val spinnerArrayId: Int? = null
)