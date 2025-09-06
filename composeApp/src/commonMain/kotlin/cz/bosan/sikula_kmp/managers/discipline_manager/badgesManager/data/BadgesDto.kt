package cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.data

import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.LocalDateTimeIso8601Serializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BadgeDto(
    @SerialName("campBadgeId") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("disciplineId")  val disciplineId: Int,
    @SerialName("defaultColor") val color: String,
)

@Serializable
data class BadgeRecordDto(
    @SerialName("badgeResultId") val id: Int,
    @SerialName("campBadgeId") val badgeId: Int,
    @SerialName("competitorId") val competitorId: Int,
    @SerialName("day")  val campDay: Int,
    @SerialName("createdAt") @Serializable(with = LocalDateTimeIso8601Serializer::class) val timeStamp: LocalDateTime,
    @SerialName("refereeId") val refereeId: Int?,
    @SerialName("toBeAwarded") val toBeAwarded: Boolean,
    @SerialName("isAwarded") val isAwarded: Boolean,
    @SerialName("toBeRemoved") val toBeRemoved: Boolean,
    @SerialName("isRemoved") val isRemoved: Boolean,
)