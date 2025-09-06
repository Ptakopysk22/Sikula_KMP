package cz.bosan.sikula_kmp.managers.children_manager.data

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ChildDataSource(
    private val httpClient: TokenAwareHttpClient
) {

    suspend fun getTrailCategories(campId: Int): Result<List<TrailCategoryDto>, DataError.Remote> {
        return httpClient.get("get-trail-categories") {
                url { parameters.append("campId", campId.toString()) }
        }
    }

    suspend fun getCampsChildren(
        campId: Int,
        groupId: Int? = null,
        crewId: Int? = null,
    ): Result<List<ChildDto>, DataError.Remote> {
        return httpClient.get("get-camp-kids") {
                url {
                    parameters.append("campID", campId.toString())
                    groupId?.let { parameters.append("groupID", it.toString()) }
                    crewId?.let { parameters.append("crewID", it.toString()) }
                }
        }
    }

    suspend fun getChild(campId: Int, childId: Int): Result<ChildDto, DataError.Remote> {
        return httpClient.get("get-kid-by-id") {
                url {
                    parameters.append("campId", campId.toString())
                    parameters.append("userId", childId.toString())
                }
        }
    }

    suspend fun assignAttendee(
        campId: Int,
        userId: Int,
        child: Child,
    ): Result<Unit, DataError.Remote> {
        val requestBody = AssignChildAttendeeRequest(
            userId = userId,
            occupation = ChildOccupationDto(
                campId = campId,
                crewId = child.crewId!!,
                role = child.role!!,
                isActive = child.isActive,
                groupId = child.groupId!!,
                trailCategoryId = child.trailCategoryId!!,
            )
        )
        return httpClient.post("assign-attendee-kid") {
                url { parameters.append("campID", campId.toString()) }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }
}