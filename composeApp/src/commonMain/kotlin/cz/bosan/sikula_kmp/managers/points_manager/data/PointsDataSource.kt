package cz.bosan.sikula_kmp.managers.points_manager.data

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result

class PointsDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    suspend fun getTotalPoints(
        campId: Int,
        campDay: Int,
    ): Result<List<PointRecordDto>, DataError.Remote> {
        return httpClient.get("get-total-points") {
            url {
                parameters.append("campID", campId.toString())
                parameters.append("day", campDay.toString())
            }
        }
    }


    suspend fun getDisciplinePoints(
        disciplineId: Int,
        campId: Int,
        campDay: Int?,
        crewId: Int?
    ): Result<List<PointRecordDto>, DataError.Remote> {
        return httpClient.get("get-discipline-points") {
            url {
                parameters.append("campID", campId.toString())
                parameters.append("disciplineID", disciplineId.toString())
                campDay?.let { parameters.append("day", campDay.toString()) }
                crewId?.let { parameters.append("crewID", crewId.toString()) }
            }
        }
    }

    suspend fun getAllPoints(
        campId: Int,
        campDay: Int?,
        crewId: Int?
    ): Result<AllPointRecordDto, DataError.Remote> {
        return httpClient.get("get-all-points") {
            url {
                parameters.append("campID", campId.toString())
                campDay?.let { parameters.append("day", campDay.toString()) }
                crewId?.let { parameters.append("crewID", crewId.toString()) }
            }
        }
    }

    suspend fun getMorningExercisePoints(
        campId: Int,
        campDay: Int?,
        crewId: Int?
    ): Result<MorningExerciseOnlyDto, DataError.Remote> {
        return httpClient.get("get-morning-exercise-points") {
            url {
                parameters.append("campID", campId.toString())
                campDay?.let { parameters.append("day", campDay.toString()) }
                crewId?.let { parameters.append("crewID", crewId.toString()) }
            }
        }
    }

    suspend fun getBadgesPoints(
        campId: Int,
        campDay: Int?,
        crewId: Int?
    ): Result<List<PointRecordDto>, DataError.Remote> {
        return httpClient.get("get-badges-points") {
            url {
                parameters.append("campID", campId.toString())
                campDay?.let { parameters.append("day", campDay.toString()) }
                crewId?.let { parameters.append("crewID", crewId.toString()) }
            }
        }
    }
}