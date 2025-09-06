package cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.data

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.Badge
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord

class BadgesRepository(
    private val badgesDataSource: BadgesDataSource,
) {

    suspend fun getCampBadges(
        campId: Int,
    ): Result<List<Badge>, DataError.Remote> {
        return badgesDataSource.getCampBadges(
            campId,
        ).map { dto -> dto.map { it.toBadge() } }
    }

    suspend fun getBadgeResults(
        campId: Int,
        campDay: Int?
    ): Result<List<BadgeRecord>, DataError.Remote> {
        return badgesDataSource.getBadgeResults(
            campId,
            campDay
        ).map { dto -> dto.map { it.toBadgeRecord() } }
    }

    suspend fun getBadgeResultsToBeAwarded(
        campId: Int,
    ): Result<List<BadgeRecord>, DataError.Remote> {
        return badgesDataSource.getBadgeResultsToBeAwarded(
            campId,
        ).map { dto -> dto.map { it.toBadgeRecord() } }
    }

    suspend fun getBadgeResultsToBeRemoved(
        campId: Int,
    ): Result<List<BadgeRecord>, DataError.Remote> {
        return badgesDataSource.getBadgeResultsToBeRemoved(
            campId,
        ).map { dto -> dto.map { it.toBadgeRecord() } }
    }

    suspend fun getCompetitorBadges(
        campId: Int,
        competitorId: Int,
    ): Result<List<BadgeRecord>, DataError.Remote> {
        return badgesDataSource.getCompetitorBadges(
            campId,
            competitorId
        ).map { dto -> dto.map { it.toBadgeRecord() } }
    }

    suspend fun grantBadge(
        record: BadgeRecord,
        campId: Int,
    ): Result<Unit, DataError.Remote> {
        if (record.toBeAwarded) {
            return badgesDataSource.markBadgeRecordAwarded(record, campId)
        } else {
            return badgesDataSource.markBadgeRecordRemoved(record, campId)
        }
    }

    suspend fun markBadgeToBeGranted(
        record: BadgeRecord,
        campId: Int,
    ): Result<Unit, DataError.Remote> {
        if (record.isAwarded) {
            return badgesDataSource.markBadgeRecordToBeAwarded(record, campId)
        } else {
            return badgesDataSource.markBadgeRecordToBeRemoved(record, campId)
        }
    }

}