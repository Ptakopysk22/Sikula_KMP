package cz.bosan.sikula_kmp.managers.leader_manager.data

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Occupation
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LeaderDataSource(
    private val httpClient: TokenAwareHttpClient,
) {

    suspend fun getCurrentLeaderRemote(mail: String): Result<LeaderDto, DataError.Remote> {
        return httpClient.get("get-leader-by-email") {
            url { parameters.append("email", mail) }
        }
    }

    suspend fun getCampsLeaders(campId: Int): Result<List<LeaderDto>, DataError.Remote> {
        return httpClient.get("get-camp-leaders") {
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun getLeader(campId: Int, leaderId: Int): Result<LeaderDto, DataError.Remote> {
        return httpClient.get("get-leader-by-id") {
            url {
                parameters.append("campId", campId.toString())
                parameters.append("userId", leaderId.toString())
            }
        }
    }

    suspend fun assignAttendee(
        campId: Int,
        userId: Int,
        occupation: Occupation,
    ): Result<Unit, DataError.Remote> {
        val requestBody = AssignLeaderAttendeeRequest(
            userId = userId,
            occupation = OccupationDto(
                campId = campId,
                role = occupation.role,
                isActive = occupation.isActive,
                groupId = occupation.groupId,
                positionIds = occupation.positions,
            )
        )
        return httpClient.post("assign-attendee-leader") {
            url { parameters.append("campID", campId.toString()) }
            contentType(ContentType.Application.Json)
            setBody(requestBody)

        }
    }


}