package cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data

import cz.bosan.sikula_kmp.managers.discipline_manager.data.ImprovementsAndRecordsDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IndividualDisciplineRecordDto(
    @SerialName("scoringRecordId") val id: Int,
    @SerialName("competitorId") val competitorId: Int,
    @SerialName("day")  val campDay: Int,
    @SerialName("value") val value: Double?,
    @SerialName("quest") val quest: Int?,
    @SerialName("createdAt") @Serializable(with = LocalDateTimeIso8601Serializer::class) val timeStamp: LocalDateTime,
    @SerialName("refereeID") val refereeId: Int,
    @SerialName("comment") val comment: String,
    @SerialName("improvementsAndRecords") val improvementsAndRecords: ImprovementsAndRecordsDto,
    @SerialName("workedOff") val workedOff: Boolean?,
    )

@Serializable
data class NewIndividualDisciplineRecordDto(
    @SerialName("competitorId") val competitorId: Int,
    @SerialName("day")  val campDay: Int,
    @SerialName("campId")  val campId: Int,
    @SerialName("value") val value: Double?,
    @SerialName("quest") val quest: Int?,
    @SerialName("createdAt") @Serializable(with = LocalDateTimeIso8601Serializer::class) val timeStamp: LocalDateTime,
    @SerialName("refereeID") val refereeId: Int,
    @SerialName("comment") val comment: String,
    @SerialName("countsForImprovement") val countsForImprovements: Boolean?,
)
