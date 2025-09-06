package cz.bosan.sikula_kmp.managers.camp_manager.data

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result

class CampDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    suspend fun getCamps(): Result<List<CampDto>, DataError.Remote> {
        return httpClient.get("get-camps", checkToken = false)
    }

    suspend fun getGroups(campId: Int): Result<List<GroupDto>, DataError.Remote> {
        return httpClient.get("get-groups") {
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun getGroupCrews(
        campId: Int?,
        groupId: Int?
    ): Result<List<CrewDto>, DataError.Remote> {
        return httpClient.get("get-crews") {
            url {
                campId?.let { parameters.append("campID", campId.toString()) }
                groupId?.let { parameters.append("groupID", groupId.toString()) }
            }
        }
    }
}