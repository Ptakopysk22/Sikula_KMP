package cz.bosan.sikula_kmp.managers.user_manager

import kotlinx.datetime.LocalDate

data class User(
    val id: Int,
    val email: String?,
    val name: String?,
    val nickName: String?,
    val birthDate: LocalDate?,
)

data class NewUser(
    val email: String?,
    val name: String,
    val nickName: String?,
    val birthDate: LocalDate?,
)

data class UserId(
    val value: Int
)

data class BirthdayUser(
    val user: User,
    val campDay: Int
)

data class AttendeesCount(
    val leadersCount: Int,
    val kidsCount: Int,
    val totalAttendees: Int,
)


