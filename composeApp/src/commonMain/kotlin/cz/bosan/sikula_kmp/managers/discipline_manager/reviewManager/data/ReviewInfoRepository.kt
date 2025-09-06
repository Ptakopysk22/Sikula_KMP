package cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.domain.DayReviewInfo

class ReviewInfoRepository(
    private val reviewInfoDataSource: ReviewInfoDataSource
) {
    suspend fun getDayReviewInfo(
        discipline: Discipline,
        campId: Int,
        campDay: Int,
    ): Result<DayReviewInfo?, DataError.Remote> {
        if (discipline is Discipline.Individual) {
            return reviewInfoDataSource.getReviewInfoIndividualDiscipline(
                discipline = discipline,
                campId = campId,
                campDay = campDay
            ).map { dtoList ->
                dtoList.firstOrNull()?.toDayReviewInfo()
                    ?: return Result.Error(DataError.Remote.NOT_FOUND)
            }
        } else {
            return reviewInfoDataSource.getReviewInfoTeamDiscipline(
                discipline = discipline,
                campId = campId,
                campDay = campDay
            ).map { dtoList ->
                dtoList.firstOrNull()?.toDayReviewInfo()
                    ?: return Result.Error(DataError.Remote.NOT_FOUND)
            }
        }
    }

    suspend fun getCampReviewInfos(
        discipline: Discipline,
        campId: Int,
    ): Result<List<DayReviewInfo>, DataError.Remote> {
        if (discipline is Discipline.Individual) {
            return reviewInfoDataSource.getReviewInfoIndividualDiscipline(
                discipline = discipline,
                campId = campId,
            ).map { dtoList ->
                dtoList.map { it.toDayReviewInfo() }
            }
        } else {
            return reviewInfoDataSource.getReviewInfoTeamDiscipline(
                discipline = discipline,
                campId = campId,
            ).map { dtoList ->
                dtoList.map { it.toDayReviewInfo() }
            }
        }
    }

    suspend fun submitRecordsGroup(
        submit: Boolean,
        discipline: Discipline,
        campId: Int,
        groupId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        return if (submit) {
            reviewInfoDataSource.submitRecordsGroup(
                discipline = discipline,
                campId = campId,
                groupId = groupId,
                campDay = campDay
            )
        } else {
            reviewInfoDataSource.unSubmitRecordsGroup(
                discipline = discipline,
                campId = campId,
                groupId = groupId,
                campDay = campDay
            )
        }
    }

    suspend fun submitRecordsPositionMaster(
        submit: Boolean,
        discipline: Discipline,
        campId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        if (discipline is Discipline.Individual) {
            return if (submit) {
                reviewInfoDataSource.submitRecordsPositionMasterIndividualDiscipline(
                    discipline = discipline,
                    campId = campId,
                    campDay = campDay
                )
            } else {
                reviewInfoDataSource.unSubmitRecordsPositionMasterIndividualDiscipline(
                    discipline = discipline,
                    campId = campId,
                    campDay = campDay
                )
            }
        } else {
            return submitTeamRecordsByPositionMaster(
                submit = submit,
                discipline = discipline,
                campId = campId,
                campDay = campDay
            )
        }
    }

    suspend fun submitTeamRecordsByPositionMaster(
        submit: Boolean,
        discipline: Discipline,
        campId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        if (submit) {
            return reviewInfoDataSource.submitTeamRecordsByPositionMaster(
                discipline = discipline,
                campId = campId,
                campDay = campDay
            )
        } else {
            return reviewInfoDataSource.unSubmitTeamRecordsByPositionMaster(
                discipline = discipline,
                campId = campId,
                campDay = campDay
            )
        }
    }

    suspend fun submitRecordsCamp(
        discipline: Discipline,
        campId: Int,
        campDay: Int
    ): Result<Unit, DataError.Remote> {
        if (discipline is Discipline.Individual) {
            return reviewInfoDataSource.submitRecordsCampIndividualDiscipline(
                discipline = discipline,
                campId = campId,
                campDay = campDay
            )
        } else {
            return reviewInfoDataSource.submitRecordsCampTeamDiscipline(
                discipline = discipline,
                campId = campId,
                campDay = campDay
            )
        }
    }
}