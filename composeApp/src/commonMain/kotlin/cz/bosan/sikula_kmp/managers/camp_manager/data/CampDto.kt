package cz.bosan.sikula_kmp.managers.camp_manager.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.serializers.LocalDateIso8601Serializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CampDto(
    @SerialName("campId")
    val id: Int,
    @SerialName("startDate")
    @Serializable(with = LocalDateIso8601Serializer::class)
    val startDate: LocalDate,
    @SerialName("endDate")
    @Serializable(with = LocalDateIso8601Serializer::class)
    val endDate: LocalDate,
    @SerialName("name")
    val name: String,
)
