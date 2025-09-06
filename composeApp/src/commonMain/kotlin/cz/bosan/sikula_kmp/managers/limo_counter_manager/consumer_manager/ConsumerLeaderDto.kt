package cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager

import cz.bosan.sikula_kmp.managers.leader_manager.data.OccupationDto
import kotlinx.datetime.LocalDate
import kotlinx.datetime.serializers.LocalDateIso8601Serializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsumerLeaderDto(
    @SerialName("userId") val userId: Int,
    @SerialName("consumerId") val consumerId: Int,
    @SerialName("name") val name: String,
    @SerialName("nickname") val nickName: String,
    @SerialName("email") val mail: String,
    @SerialName("bankAccountNumber") val bankAccount: String?,
    @SerialName("occupations") val occupations: List<OccupationDto>,
    @SerialName("dateOfBirth") @Serializable(with = LocalDateIso8601Serializer::class) val birthDate: LocalDate?,
    @SerialName("credit") val credit: Double?,
    @SerialName("nfctoken") val tag: String?,
)

@Serializable
data class AssignConsumerRequest(
    @SerialName("userId") val userId: Int,
    @SerialName("campId") val campId: Int,
    @SerialName("credit") val credit: Double,
    @SerialName("nfctoken") val tag: String?,
    @SerialName("targetBankTyp") val targetBankTyp: Int,
)