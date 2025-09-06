package cz.bosan.sikula_kmp.managers.points_manager.data

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.getDisciplineById
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord

class PointsRepository(
    private val pointsDataSource: PointsDataSource,
) {

    suspend fun getTotalPoints(
        campId: Int,
        campDay: Int,
    ): Result<List<PointRecord>, DataError.Remote> {
        return pointsDataSource.getTotalPoints(
            campId = campId,
            campDay = campDay,
        ).map { dto -> dto.map { it.toPointRecord(Discipline.Team.ALL) } }
    }

    suspend fun getDisciplinePoints(
        disciplineId: Int,
        campId: Int,
        campDay: Int?,
        crewId: Int?
    ): Result<List<PointRecord>, DataError.Remote> {
        return pointsDataSource.getDisciplinePoints(
            campId = campId,
            disciplineId = disciplineId,
            campDay = campDay,
            crewId = crewId
        ).map { dto ->
            dto.map { it.toPointRecord(getDisciplineById(disciplineId.toString())) }
        }
    }

    suspend fun getAllPoints(
        campId: Int,
        campDay: Int?,
        crewId: Int?
    ): Result<List<PointRecord>, DataError.Remote> {
        return pointsDataSource.getAllPoints(
            campId = campId,
            campDay = campDay,
            crewId = crewId
        ).map { dto -> dto.toPointRecords() }
    }

    suspend fun getMorningExercisePoints(
        campId: Int,
        campDay: Int?,
        crewId: Int?
    ): Result<List<PointRecord>, DataError.Remote> {
        return pointsDataSource.getMorningExercisePoints(
            campId = campId,
            campDay = campDay,
            crewId = crewId
        ).map { it.toPointRecords() }
    }

    suspend fun getBadgesPoints(
        campId: Int,
        campDay: Int?,
        crewId: Int?
    ): Result<List<PointRecord>, DataError.Remote> {
        return pointsDataSource.getBadgesPoints(
            campId = campId,
            campDay = campDay,
            crewId = crewId
        ).map { dto ->
            dto.map { it.toPointRecord(Discipline.Badges.BADGES) }
        }
    }
}

