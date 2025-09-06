package cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data

import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.domain.DayReviewInfo
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.domain.GroupReviewInfo

fun DayReviewInfoDto.toDayReviewInfo(): DayReviewInfo{
    return DayReviewInfo(
        campDay = campDay,
        readyForReview = readyForReview,
        reviewed = reviewed,
        groupReviewInfos = groupReviewInfos.map { it.toGroupReviewInfo() }
    )
}

fun GroupReviewInfoDto.toGroupReviewInfo(): GroupReviewInfo {
    return GroupReviewInfo(
        groupId = groupId,
        readyForReview = readyForReview,
        reviewed = reviewed
    )
}