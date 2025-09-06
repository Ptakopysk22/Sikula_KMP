package cz.bosan.sikula_kmp.managers.camp_manager.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    @SerialName("groupId") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("defaultColor") val color: String,
    @SerialName("crews") val crews: List<CrewDto>
)

@Serializable
data class CrewDto(
    @SerialName("crewId") val id: Int,
    @SerialName("groupId") val groupId: Int,
    @SerialName("name") val name: String,
    @SerialName("defaultColor") val color: String,
)

