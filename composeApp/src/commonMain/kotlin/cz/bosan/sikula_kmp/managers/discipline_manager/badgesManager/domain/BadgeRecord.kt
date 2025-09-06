package cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain

import kotlinx.datetime.LocalDateTime

data class BadgeRecord(
    val id: Int,
    val badgeId: Int,
    val competitorId: Int,
    val campDay: Int,
    val timeStamp: LocalDateTime,
    val refereeId: Int?,
    val toBeAwarded: Boolean,
    val isAwarded: Boolean,
    val toBeRemoved: Boolean,
    val isRemoved: Boolean
)
