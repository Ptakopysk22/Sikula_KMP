package cz.bosan.sikula_kmp.managers.user_manager

import kotlinx.datetime.LocalDate
import kotlinx.datetime.serializers.LocalDateIso8601Serializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("userId") val id: Int,
    @SerialName("nickname") val nickName: String?,
    @SerialName("name") val name: String?,
    @SerialName("email") val mail: String?,
    @SerialName("dateOfBirth") @Serializable(with = LocalDateIso8601Serializer::class) val birthDate: LocalDate?,
)

@Serializable
data class NewUserDto(
    @SerialName("nickname") val nickName: String?,
    @SerialName("name") val name: String,
    @SerialName("email") val mail: String?,
    @SerialName("dateOfBirth") @Serializable(with = LocalDateIso8601Serializer::class) val birthDate: LocalDate?,
)

@Serializable
data class NewUserIdDto(
    @SerialName("userId") val userId: Int,
)

@Serializable
data class AttendeesCountDto(
    @SerialName("leadersCount") val leadersCount: Int,
    @SerialName("kidsCount") val kidsCount: Int,
    @SerialName("totalAttendees") val totalAttendees: Int,
)

