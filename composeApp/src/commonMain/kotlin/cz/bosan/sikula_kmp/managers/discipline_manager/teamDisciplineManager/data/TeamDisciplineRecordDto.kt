package cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data

import cz.bosan.sikula_kmp.managers.discipline_manager.data.ImprovementsAndRecordsDto
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.LocalDateTimeIso8601Serializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDisciplineRecordDto(
    @SerialName("scoringRecordId") val id: Int,
    @SerialName("crewId") val crewId: Int,
    @SerialName("day")  val campDay: Int,
    @SerialName("value") val value: Int?,
    @SerialName("createdAt") @Serializable(with = LocalDateTimeIso8601Serializer::class) val timeStamp: LocalDateTime,
    @SerialName("refereeID") val refereeId: Int,
    @SerialName("comment") val comment: String,
    @SerialName("improvementsAndRecords") val improvementsAndRecords: ImprovementsAndRecordsDto,
    )

@Serializable
data class NewTeamDisciplineRecordDto(
    @SerialName("crewId") val crewId: Int,
    @SerialName("day")  val campDay: Int,
    @SerialName("campId")  val campId: Int,
    @SerialName("value") val value: Int?,
    @SerialName("createdAt") @Serializable(with = LocalDateTimeIso8601Serializer::class) val timeStamp: LocalDateTime,
    @SerialName("refereeID") val refereeId: Int,
    @SerialName("comment") val comment: String,
    @SerialName("countsForImprovement") val countsForImprovements: Boolean?,
)