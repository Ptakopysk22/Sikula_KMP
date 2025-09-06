package cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.managers.discipline_manager.data.ImprovementsCompetitorOrCrewDto
import cz.bosan.sikula_kmp.managers.discipline_manager.data.UpdateDisciplineResultDto
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TeamDisciplineDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    suspend fun getTeamDisciplineRecordsDay(
        discipline: Discipline,
        campId: Int,
        campDay: Int,
    ): Result<List<TeamDisciplineRecordDto>, DataError.Remote> {
        return httpClient.get("get-team-discipline-results-day") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun getTeamDisciplineRecordsCrew(
        discipline: Discipline,
        campId: Int,
        crewId: Int,
    ): Result<List<TeamDisciplineRecordDto>, DataError.Remote> {
        return httpClient.get("get-team-discipline-results-crew") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("crewID", crewId.toString()) }
        }
    }

    suspend fun createRecord(
        record: NewTeamDisciplineRecordDto,
        discipline: Discipline
    ): Result<TeamDisciplineRecordDto, DataError.Remote> {
        return httpClient.post("create-team-discipline-result") {
            url { parameters.append("disciplineID", discipline.getId()) }
            contentType(ContentType.Application.Json)
            setBody(record)
        }
    }

    suspend fun updateRecord(
        record: TeamDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<TeamDisciplineRecordDto, DataError.Remote> {
        return httpClient.put("update-team-discipline-result") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            contentType(ContentType.Application.Json)
            setBody(
                UpdateDisciplineResultDto(
                    scoringRecordId = record.id!!,
                    value = record.value?.toDouble(),
                    comment = record.comment
                )
            )
        }
    }

    suspend fun updateRecordCountsForImprovementToTrue(
        record: TeamDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<TeamDisciplineRecordDto, DataError.Remote> {
        return httpClient.put("team-discipline-single-result-counts-for-improvement") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("resultID", record.id.toString()) }
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun updateRecordCountsForImprovementToFalse(
        record: TeamDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<TeamDisciplineRecordDto, DataError.Remote> {
        return httpClient.put("team-discipline-single-result-does-not-count-for-improvement") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("resultID", record.id.toString()) }
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun getTeamDisciplineTargetImprovements(
        discipline: Discipline,
        campId: Int,
        campDay: Int,
    ): Result<List<ImprovementsCompetitorOrCrewDto>, DataError.Remote> {
        return httpClient.get("get-team-discipline-target-improvements") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }
}