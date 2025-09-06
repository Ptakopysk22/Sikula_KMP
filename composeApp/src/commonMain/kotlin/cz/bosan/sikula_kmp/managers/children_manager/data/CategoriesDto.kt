package cz.bosan.sikula_kmp.managers.children_manager.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrailCategoryDto(
    @SerialName("trailCategoryId") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("comment") val description: String,
    @SerialName("baseTime") val baseTime: Int,
    @SerialName("defaultColor") val color: String,

)