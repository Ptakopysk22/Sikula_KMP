package cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.data

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord

class BadgesDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    suspend fun getCampBadges(
        campId: Int,
    ): Result<List<BadgeDto>, DataError.Remote> {
        return httpClient.get("load-camp-badges") {
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun getBadgeResults(
        campId: Int,
        campDay: Int?
    ): Result<List<BadgeRecordDto>, DataError.Remote> {
        return httpClient.get("load-all-badge-results") {
            url { parameters.append("campID", campId.toString()) }
            url { campDay?.let { parameters.append("day", campDay.toString()) }}
        }
    }

    suspend fun getBadgeResultsToBeAwarded(
        campId: Int,
    ): Result<List<BadgeRecordDto>, DataError.Remote> {
        return httpClient.get("load-all-to-be-awarded-badge-results") {
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun getBadgeResultsToBeRemoved(
        campId: Int,
    ): Result<List<BadgeRecordDto>, DataError.Remote> {
        return httpClient.get("load-all-to-be-removed-badge-results") {
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun getCompetitorBadges(
        campId: Int,
        competitorId: Int,
    ): Result<List<BadgeRecordDto>, DataError.Remote> {
        return httpClient.get("load-competitor-badge-results") {
            url { parameters.append("campID", campId.toString()) }
            url { parameters.append("competitorID", competitorId.toString()) }
        }
    }

    suspend fun markBadgeRecordToBeAwarded(
        record: BadgeRecord,
        campId: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.put("mark-badge-result-to-be-awarded") {
            url { parameters.append("badgeResultID", record.id.toString()) }
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun markBadgeRecordAwarded(
        record: BadgeRecord,
        campId: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.put("mark-badge-result-awarded") {
            url { parameters.append("badgeResultID", record.id.toString()) }
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun markBadgeRecordToBeRemoved(
        record: BadgeRecord,
        campId: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.put("mark-badge-result-to-be-removed") {
            url { parameters.append("badgeResultID", record.id.toString()) }
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun markBadgeRecordRemoved(
        record: BadgeRecord,
        campId: Int
    ): Result<Unit, DataError.Remote> {
        return httpClient.put("mark-badge-result-removed") {
            url { parameters.append("badgeResultID", record.id.toString()) }
            url { parameters.append("campID", campId.toString()) }
        }
    }

}