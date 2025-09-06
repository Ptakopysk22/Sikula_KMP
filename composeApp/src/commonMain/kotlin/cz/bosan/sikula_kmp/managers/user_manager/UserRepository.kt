package cz.bosan.sikula_kmp.managers.user_manager

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map

class UserRepository(
    private val userDataSource: UserDataSource
) {
    //pak zmenit na usera
    suspend fun getUserByEmail(email: String): Result<User, DataError.Remote> {
        return userDataSource.getUserByEmail(email).map {
            User(
                id = it.id,
                email = it.mail,
                name = it.name,
                nickName = it.nickName,
                birthDate = it.birthDate
            )
        }
    }

    suspend fun getUsersContainingString(searchedString: String): Result<List<User>, DataError.Remote> {
        return userDataSource.getUsersContainingString(searchedString)
            .map { dto -> dto.map { it.toUser() } }
    }

    suspend fun createUser(newUser: NewUser): Result<UserId, DataError.Remote> {
        return userDataSource.createUser(newUser.toNewUserDto()).map { it.toUserId() }
    }

    suspend fun updateUser(user: User): Result<Unit, DataError.Remote> {
        return userDataSource.updateUser(user.toUserDto())
    }

    suspend fun getBirthdays(campId: Int, campDay: Int): Result<List<User>, DataError.Remote> {
        return userDataSource.getBirthdays(campId, campDay)
            .map { dto -> dto.map { it.toUser() } }
    }

    suspend fun getAttendeesCount(campId: Int): Result<AttendeesCount, DataError.Remote> {
        return userDataSource.getAttendeesCount(campId).map { it.toAttendeesCount() }
    }
}
