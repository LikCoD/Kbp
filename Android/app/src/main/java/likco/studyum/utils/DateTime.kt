package likco.studyum.utils

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDateTime.format(pattern: String, locale: Locale = Locale.US) =
    format(DateTimeFormatter.ofPattern(pattern, locale))!!

fun LocalDate.format(pattern: String, locale: Locale = Locale.US) =
    format(DateTimeFormatter.ofPattern(pattern, locale))!!

fun LocalTime.format(pattern: String, locale: Locale = Locale.US) =
    format(DateTimeFormatter.ofPattern(pattern, locale))!!
