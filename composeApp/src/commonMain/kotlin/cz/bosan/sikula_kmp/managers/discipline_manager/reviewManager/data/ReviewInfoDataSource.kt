package cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline

class ReviewInfoDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    suspend fun getReviewInfoIndividualDiscipline(
        discipline: Discipline,
        campId: Int,
        campDay: Int? = null,
    ): Result<List<DayReviewInfoDto>, DataError.Remote> {
        return httpClient.get("get-individual-discipline-review-info") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            campDay?.let { url { parameters.append("day", campDay.toString()) } }
        }
    }

    suspend fun getReviewInfoTeamDiscipline(
        discipline: Discipline,
        campId: Int,
        campDay: Int? = null,
    ): Result<List<DayReviewInfoDto>, DataError.Remote> {
        return httpClient.get("get-team-discipline-review-info") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            campDay?.let { url { parameters.append("day", campDay.toString()) } }
        }
    }

    suspend fun submitRecordsGroup(
        discipline: Discipline,
        campId: Int,
        groupId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.post("group-individual-discipline-day-ready-for-review") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("groupID", groupId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }

    }

    suspend fun submitTeamRecordsByPositionMaster(
        discipline: Discipline,
        campId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.post("team-discipline-day-ready-for-review") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun unSubmitTeamRecordsByPositionMaster(
        discipline: Discipline,
        campId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.post("team-discipline-day-not-ready-for-review") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun unSubmitRecordsGroup(
        discipline: Discipline,
        campId: Int,
        groupId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.post("group-individual-discipline-day-not-ready-for-review") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("groupID", groupId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun submitRecordsPositionMasterIndividualDiscipline(
        discipline: Discipline,
        campId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.post("individual-discipline-day-ready-for-review") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun unSubmitRecordsPositionMasterIndividualDiscipline(
        discipline: Discipline,
        campId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.post("individual-discipline-day-ready-for-review") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun submitRecordsCampIndividualDiscipline(
        discipline: Discipline,
        campId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.post("individual-discipline-day-reviewed") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun submitRecordsCampTeamDiscipline(
        discipline: Discipline,
        campId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.post("team-discipline-day-reviewed") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }
}