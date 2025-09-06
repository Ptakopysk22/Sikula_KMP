package cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data

import androidx.sqlite.SQLiteException
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.managers.discipline_manager.data.toTargetImprovements
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.TargetImprovement
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord

class IndividualDisciplineRecordRepository(
    private val individualDisciplineDataSource: IndividualDisciplineDataSource,
    private val individualRecordDao: IndividualRecordDao
) {

    suspend fun getIndividualDisciplineRecordsGroup(
        discipline: Discipline,
        campId: Int,
        groupId: Int,
        campDay: Int,
    ): Result<List<IndividualDisciplineRecord>, DataError.Remote> {
        return individualDisciplineDataSource.getIndividualDisciplineRecordsGroup(
            discipline,
            campId,
            groupId,
            campDay
        ).map { dto -> dto.map { it.toIndividualDisciplineRecord(discipline) } }
    }

    suspend fun getIndividualDisciplineTargetImprovements(
        discipline: Discipline,
        campId: Int,
        groupId: Int,
        campDay: Int,
    ): Result<List<TargetImprovement>, DataError.Remote> {
        return individualDisciplineDataSource.getIndividualDisciplineTargetImprovements(
            discipline,
            campId,
            groupId,
            campDay
        ).map { dto -> dto.map { it.toTargetImprovements() } }
    }

    suspend fun getIndividualDisciplineRecordsDay(
        discipline: Discipline,
        campId: Int,
        campDay: Int,
    ): Result<List<IndividualDisciplineRecord>, DataError.Remote> {
        return individualDisciplineDataSource.getIndividualDisciplineRecordsDay(
            discipline,
            campId,
            campDay
        ).map { dto -> dto.map { it.toIndividualDisciplineRecord(discipline) } }
    }

    suspend fun getIndividualDisciplineRecordsCompetitor(
        discipline: Discipline,
        competitorId: Int,
        campId: Int,
    ): Result<List<IndividualDisciplineRecord>, DataError.Remote> {
        return individualDisciplineDataSource.getIndividualDisciplineRecordsCompetitor(
            discipline,
            competitorId,
            campId,
        ).map { dto -> dto.map { it.toIndividualDisciplineRecord(discipline) } }
    }

    suspend fun getIndividualDisciplineAllRecords(
        discipline: Discipline,
        campId: Int,
    ): Result<List<IndividualDisciplineRecord>, DataError.Remote> {
        return individualDisciplineDataSource.getIndividualDisciplineAllRecords(
            discipline,
            campId,
        ).map { dto -> dto.map { it.toIndividualDisciplineRecord(discipline) } }
    }


    suspend fun createIndividualDisciplineRecord(
        record: IndividualDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<IndividualDisciplineRecord, DataError.Remote> {
        return individualDisciplineDataSource.createRecord(
            record = record.toNewIndividualDisciplineRecordDto(campId), discipline = discipline
        ).map { it.toIndividualDisciplineRecord(discipline) }
    }

    suspend fun getIndividualRecordsLocally(
        campId: Int,
        campDay: Int,
        discipline: Discipline,
    ): List<IndividualDisciplineRecord> {
        val entity = individualRecordDao.getLocalIndividualRecords(
            campId = campId,
            campDay = campDay,
            disciplineId = discipline.getId().toInt(),
        )
        return entity.map { record ->
            record.toIndividualRecord()
        }
    }

    suspend fun insertOrUpdateIndividualRecordLocally(
        record: IndividualDisciplineRecord,
        discipline: Discipline,
        campId: Int,
        idOnServer: Int?,
    ): Result<Int, DataError.Local> {
        return try {
            val newId = individualRecordDao.insertRecord(
                entity = record.toIndividualRecordEntity(campId, discipline, idOnServer)
            ).toInt()
            Result.Success(newId)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    suspend fun updateIndividualRecord(
        record: IndividualDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<IndividualDisciplineRecord, DataError.Remote> {
        return individualDisciplineDataSource.updateRecord(
            record = record,
            discipline = discipline,
            campId = campId
        ).map { it.toIndividualDisciplineRecord(discipline) }
    }

    suspend fun updateIndividualRecordCountsForImprovement(
        record: IndividualDisciplineRecord,
        discipline: Discipline,
        countsForImprovement: Boolean,
        campId: Int
    ): Result<IndividualDisciplineRecord, DataError.Remote> {
        return if (countsForImprovement) {
            individualDisciplineDataSource.updateRecordCountsForImprovementToTrue(
                record = record,
                discipline = discipline,
                campId = campId
            ).map { it.toIndividualDisciplineRecord(discipline) }
        } else {
            individualDisciplineDataSource.updateRecordCountsForImprovementToFalse(
                record = record,
                discipline = discipline,
                campId = campId
            ).map { it.toIndividualDisciplineRecord(discipline) }
        }
    }

    suspend fun updateIndividualRecordWorkedOff(
        record: IndividualDisciplineRecord,
        discipline: Discipline,
        campId: Int
    ): Result<Unit, DataError.Remote> {
        return individualDisciplineDataSource.updateRecordWorkedOff(
            record = record,
            discipline = discipline,
            campId = campId
        )
    }

}