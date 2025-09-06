package cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data

import androidx.sqlite.SQLiteException
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.managers.discipline_manager.data.toTargetImprovements
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.TargetImprovement
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord

class TeamDisciplineRepository(
    private val teamDisciplineDataSource: TeamDisciplineDataSource,
    private val teamRecordDao: TeamRecordDao
) {

    suspend fun getTeamDisciplineRecordsDay(
        discipline: Discipline,
        campId: Int,
        campDay: Int,
    ): Result<List<TeamDisciplineRecord>, DataError.Remote> {
        return teamDisciplineDataSource.getTeamDisciplineRecordsDay(
            discipline,
            campId,
            campDay
        ).map { dto -> dto.map { it.toTeamDisciplineRecord() } }
    }

    suspend fun getTeamDisciplineRecordsCrew(
        discipline: Discipline,
        campId: Int,
        crewId: Int,
    ): Result<List<TeamDisciplineRecord>, DataError.Remote> {
        return teamDisciplineDataSource.getTeamDisciplineRecordsCrew(
            discipline,
            campId,
            crewId
        ).map { dto -> dto.map { it.toTeamDisciplineRecord() } }
    }

    suspend fun getTeamRecordsLocally(
        campId: Int,
        campDay: Int,
        discipline: Discipline,
    ): Result<List<TeamDisciplineRecord>, DataError.Local> {
        return try {
            val teamRecords = teamRecordDao.getLocalTeamDayRecords(
                campId = campId,
                campDay = campDay,
                disciplineId = discipline.getId().toInt(),
            ).map { record ->
                record.toTeamDisciplineRecord()
            }
            Result.Success(teamRecords)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    suspend fun createTeamDisciplineRecord(
        record: TeamDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<TeamDisciplineRecord, DataError.Remote> {
        return teamDisciplineDataSource.createRecord(
            record = record.toNewTeamDisciplineRecordDto(campId), discipline = discipline
        ).map { it.toTeamDisciplineRecord() }
    }

    suspend fun updateTeamDisciplineRecord(
        record: TeamDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<TeamDisciplineRecord, DataError.Remote> {
        return teamDisciplineDataSource.updateRecord(
            record = record,
            discipline = discipline,
            campId = campId
        ).map { it.toTeamDisciplineRecord() }
    }

    suspend fun insertOrUpdateTeamRecordLocally(
        record: TeamDisciplineRecord,
        discipline: Discipline,
        campId: Int,
        idOnServer: Int?,
    ): Result<Int, DataError.Local> {
        return try {
            val newId = teamRecordDao.insertOrUpdateRecord(
                entity = record.toTeamRecordEntity(campId, discipline, idOnServer)
            )
            Result.Success(newId.toInt())
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    suspend fun updateTeamRecordCountsForImprovement(
        record: TeamDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<TeamDisciplineRecord, DataError.Remote> {
        return if (record.improvementsAndRecords?.countsForImprovements == true) {
            teamDisciplineDataSource.updateRecordCountsForImprovementToTrue(
                record = record,
                discipline = discipline,
                campId = campId
            ).map { it.toTeamDisciplineRecord() }
        } else {
            teamDisciplineDataSource.updateRecordCountsForImprovementToFalse(
                record = record,
                discipline = discipline,
                campId = campId
            ).map { it.toTeamDisciplineRecord() }
        }
    }

    suspend fun getTeamDisciplineTargetImprovements(
        discipline: Discipline,
        campId: Int,
        campDay: Int,
    ): Result<List<TargetImprovement>, DataError.Remote> {
        return teamDisciplineDataSource.getTeamDisciplineTargetImprovements(
            discipline,
            campId,
            campDay
        ).map { dto -> dto.map { it.toTargetImprovements() } }
    }

}