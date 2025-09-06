package cz.bosan.sikula_kmp.managers.discipline_manager.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImprovementsCompetitorOrCrewDto(
    @SerialName("competitorOrCrewID")  val id: Int,
    @SerialName("improvementString")  val improvementString: String?,
    @SerialName("improvementValue") val improvementValue: Int?,
    @SerialName("improvementTargetString") val improvementTargetString: String?,
    @SerialName("improvementTargetValue")  val improvementTargetValue: Int?,
)

@Serializable
data class ImprovementsAndRecordsDto(
    @SerialName("countsForImprovements") val countsForImprovements: Boolean? = null,
    //@SerialName("improvement")  val improvement: Int?,
    @SerialName("improvementString")  val improvementString: String? = null,
    @SerialName("improvementValue") val improvementValue: Int? = null,
    //@SerialName("improvementTargetString") val improvementTargetString: String?,
    //@SerialName("improvementTargetValue")  val improvementTargetValue: Int?,
    @SerialName("isRecord")  val isRecord: Boolean? = null,
)

@Serializable
data class UpdateDisciplineResultDto(
    @SerialName("scoringRecordId") val scoringRecordId: Int,
    @SerialName("value") val value: Double?,
    @SerialName("comment") val comment: String
)