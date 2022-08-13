package likco.studyum.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDateTime.format(pattern: String, locale: Locale = Locale.US) =
    format(DateTimeFormatter.ofPattern(pattern, locale))!!