package cz.bosan.sikula_kmp.managers.children_manager.data

import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import kotlinx.datetime.LocalDate
import kotlinx.datetime.serializers.LocalDateIso8601Serializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChildDto(
    @SerialName("userId") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("nickname") val nickName: String,
    @SerialName("email") val mail: String?,
    @SerialName("occupations") val occupations: List<ChildOccupationDto>,
    @SerialName("dateOfBirth") @Serializable(with = LocalDateIso8601Serializer::class) val birthDate: LocalDate?,
)

@Serializable
data class ChildOccupationDto(
    @SerialName("campId") val campId: Int,
    @SerialName("crewId") val crewId: Int,
    @SerialName("roleId") @Serializable(with = ChildRoleSerializer::class) val role: ChildRole,
    @SerialName("active") val isActive: Boolean,
    @SerialName("trailCategoryId") val trailCategoryId: Int,
    @SerialName("groupId") val groupId: Int,
)

@Serializable
data class AssignChildAttendeeRequest(
    @SerialName("userId") val userId: Int,
    @SerialName("occupation") val occupation: ChildOccupationDto,
)

@Serializable
data class LightChild(
    val id: Int,
    val nickname: String,
    val trailCategoryId: Int,
)
