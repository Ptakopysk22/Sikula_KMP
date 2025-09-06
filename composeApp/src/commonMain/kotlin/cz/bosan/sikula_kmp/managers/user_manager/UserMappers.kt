package cz.bosan.sikula_kmp.managers.user_manager

fun UserDto.toUser(): User {
    return User(
        id = id,
        email = mail,
        name = name,
        nickName = nickName,
        birthDate = birthDate
    )
}

fun User.toUserDto(): UserDto {
    return UserDto(
        id = id,
        mail = email,
        name = name,
        nickName = nickName,
        birthDate = birthDate
    )
}

fun NewUser.toNewUserDto(): NewUserDto {
    return NewUserDto(
        mail = email,
        name = name,
        nickName = nickName,
        birthDate = birthDate
    )
}

fun NewUserIdDto.toUserId() : UserId {
    return UserId(
        value = userId
    )
}

fun AttendeesCountDto.toAttendeesCount(): AttendeesCount{
    return AttendeesCount(
        leadersCount = leadersCount,
        kidsCount = kidsCount,
        totalAttendees = totalAttendees
    )
}