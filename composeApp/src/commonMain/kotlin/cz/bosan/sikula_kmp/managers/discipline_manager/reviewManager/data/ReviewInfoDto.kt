package cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DayReviewInfoDto(
    @SerialName("day") val campDay: Int,
    @SerialName("readyForReview") val readyForReview: Boolean,
    @SerialName("reviewed") val reviewed: Boolean,
    @SerialName("groupReviewInfos") val groupReviewInfos: List<GroupReviewInfoDto>,
)

@Serializable
data class GroupReviewInfoDto(
    @SerialName("groupID") val groupId: Int,
    @SerialName("readyForReview") val readyForReview: Boolean,
    @SerialName("reviewed") val reviewed: Boolean,
)