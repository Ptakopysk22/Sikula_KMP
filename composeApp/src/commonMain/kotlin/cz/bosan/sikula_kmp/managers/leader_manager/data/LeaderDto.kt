package cz.bosan.sikula_kmp.managers.leader_manager.data

import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import kotlinx.datetime.LocalDate
import kotlinx.datetime.serializers.LocalDateIso8601Serializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderDto(
    @SerialName("userId") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("nickname") val nickName: String,
    @SerialName("email") val mail: String,
    @SerialName("bankAccountNumber") val bankAccount: String?,
    @SerialName("occupations") val occupations: List<OccupationDto>,
    @SerialName("dateOfBirth") @Serializable(with = LocalDateIso8601Serializer::class) val birthDate: LocalDate?,
)

@Serializable
data class OccupationDto(
    @SerialName("campId") val campId: Int,
    @SerialName("roleId") @Serializable(with = LeaderRoleSerializer::class) val role: Role,
    @SerialName("active") val isActive: Boolean,
    @SerialName("groupId") val groupId: Int?,
    @SerialName("positionIds") @Serializable(with = PositionListSerializer::class) val positionIds: List<Position> = emptyList(),
)

@Serializable
data class AssignLeaderAttendeeRequest(
    @SerialName("userId") val userId: Int,
    @SerialName("occupation") val occupation: OccupationDto,
)




