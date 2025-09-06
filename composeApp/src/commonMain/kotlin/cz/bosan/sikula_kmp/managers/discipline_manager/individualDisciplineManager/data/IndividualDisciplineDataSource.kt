package cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.managers.discipline_manager.data.ImprovementsCompetitorOrCrewDto
import cz.bosan.sikula_kmp.managers.discipline_manager.data.UpdateDisciplineResultDto
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class IndividualDisciplineDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    suspend fun getIndividualDisciplineRecordsGroup(
        discipline: Discipline,
        campId: Int,
        groupId: Int,
        campDay: Int,
    ): Result<List<IndividualDisciplineRecordDto>, DataError.Remote> {
        return httpClient.get("get-individual-discipline-results-group") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("groupID", groupId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun getIndividualDisciplineRecordsDay(
        discipline: Discipline,
        campId: Int,
        campDay: Int,
    ): Result<List<IndividualDisciplineRecordDto>, DataError.Remote> {
        return httpClient.get("get-individual-discipline-results-day") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun getIndividualDisciplineRecordsCompetitor(
        discipline: Discipline,
        competitorId: Int,
        campId: Int,
    ): Result<List<IndividualDisciplineRecordDto>, DataError.Remote> {
        return httpClient.get("get-individual-discipline-results-competitor") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("competitorID", competitorId.toString()) }
        }
    }

    suspend fun getIndividualDisciplineAllRecords(
        discipline: Discipline,
        campId: Int,
    ): Result<List<IndividualDisciplineRecordDto>, DataError.Remote> {
        return httpClient.get("get-individual-discipline-all-results") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun getIndividualDisciplineTargetImprovements(
        discipline: Discipline,
        campId: Int,
        groupId: Int,
        campDay: Int,
    ): Result<List<ImprovementsCompetitorOrCrewDto>, DataError.Remote> {
        return httpClient.get("get-individual-discipline-target-improvements-group") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("groupID", groupId.toString()) }
            url { parameters.append("day", campDay.toString()) }
        }
    }

    suspend fun createRecord(
        record: NewIndividualDisciplineRecordDto,
        discipline: Discipline
    ): Result<IndividualDisciplineRecordDto, DataError.Remote> {
        return httpClient.post("create-individual-discipline-result") {
            url { parameters.append("disciplineID", discipline.getId()) }
            contentType(ContentType.Application.Json)
            setBody(record)
        }
    }

    suspend fun updateRecord(
        record: IndividualDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<IndividualDisciplineRecordDto, DataError.Remote> {
        return httpClient.put("update-individual-discipline-result") {
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
        record: IndividualDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<IndividualDisciplineRecordDto, DataError.Remote> {
        return httpClient.put("individual-discipline-single-result-counts-for-improvement") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("resultID", record.id.toString()) }
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun updateRecordCountsForImprovementToFalse(
        record: IndividualDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<IndividualDisciplineRecordDto, DataError.Remote> {
        return httpClient.put("individual-discipline-single-result-does-not-count-for-improvement") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("resultID", record.id.toString()) }
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun updateRecordWorkedOff(
        record: IndividualDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.put("individual-discipline-result-worked-off") {
            url { parameters.append("disciplineID", discipline.getId()) }
            url { parameters.append("resultID", record.id.toString()) }
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("workedOff", record.workedOff.toString()) }
        }
    }


}