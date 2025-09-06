package cz.bosan.sikula_kmp.managers.user_manager

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.managers.leader_manager.data.LeaderDto
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    //tuto funkci zmením až bude k dispozici funkce na získávání usera pomocí mailu
    suspend fun getUserByEmail(email: String): Result<LeaderDto, DataError.Remote> {
        return httpClient.get("get-leader-by-email") {
            url { parameters.append("email", email) }
        }
    }

    suspend fun getUsersContainingString(searchedString: String): Result<List<UserDto>, DataError.Remote> {
        return httpClient.get("search-users") {
            url { parameters.append("searchedString", searchedString) }
        }
    }

    suspend fun createUser(newUserDto: NewUserDto): Result<NewUserIdDto, DataError.Remote> {
        return httpClient.post("create-user") {
            contentType(ContentType.Application.Json)
            setBody(newUserDto)
        }
    }


    suspend fun updateUser(userDto: UserDto): Result<Unit, DataError.Remote> {
        return httpClient.put("update-user") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }
    }

    suspend fun getBirthdays(campId: Int, campDay: Int): Result<List<UserDto>, DataError.Remote> {
        return httpClient.get("get-birthdays") {
            url {
                parameters.append("campID", campId.toString())
                parameters.append("day", campDay.toString())
            }
        }
    }

    suspend fun getAttendeesCount(campId: Int): Result<AttendeesCountDto, DataError.Remote> {
        return httpClient.get("get-active-attendees-count") {
            url { parameters.append("campID", campId.toString()) }
        }
    }

}