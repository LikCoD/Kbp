package com.ldc.kbp.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime

interface ITimeSerializer<T> : KSerializer<T> {
    val Decoder.timeString: String
        get() {
            val str = decodeString()

            return if (str.endsWith("z", ignoreCase = true)) str.dropLast(1) else str
        }
}

class DateTimeSerializer : ITimeSerializer<LocalDateTime> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalDateTime) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): LocalDateTime =
        LocalDateTime.parse(decoder.timeString)
}

class DateSerializer : ITimeSerializer<LocalDate> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalDate) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): LocalDate =
        LocalDate.parse(decoder.timeString)
}