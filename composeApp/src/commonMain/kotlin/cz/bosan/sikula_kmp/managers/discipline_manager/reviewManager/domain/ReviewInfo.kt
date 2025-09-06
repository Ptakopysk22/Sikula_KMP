package cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.domain

data class DayReviewInfo(
    val campDay: Int,
    val readyForReview: Boolean,
    val reviewed: Boolean,
    val groupReviewInfos: List<GroupReviewInfo>
)

data class GroupReviewInfo(
    val groupId: Int,
    val readyForReview: Boolean,
    val reviewed: Boolean,
)
