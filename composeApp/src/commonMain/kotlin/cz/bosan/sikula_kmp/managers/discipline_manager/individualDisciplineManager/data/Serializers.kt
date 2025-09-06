package cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object LocalDateTimeIso8601Serializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val instant = value.toInstant(TimeZone.UTC)
        encoder.encodeString(instant.toString())
    }

     override fun deserialize(decoder: Decoder): LocalDateTime {
         val instant = Instant.parse(decoder.decodeString())
         val deviceTimeZone = TimeZone.currentSystemDefault()
         return instant.toLocalDateTime(deviceTimeZone)
     }
}