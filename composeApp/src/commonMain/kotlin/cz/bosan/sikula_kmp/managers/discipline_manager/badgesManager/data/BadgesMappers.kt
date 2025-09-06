package cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.data

import cz.bosan.sikula_kmp.managers.camp_manager.presentation.hexToColor
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.Badge
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord

fun BadgeDto.toBadge(): Badge {
    return Badge(
        id = id,
        name = name,
        disciplineId = disciplineId,
        color = hexToColor(color)
    )
}

fun BadgeRecordDto.toBadgeRecord(): BadgeRecord {
    return BadgeRecord(
        id = id,
        badgeId = badgeId,
        competitorId = competitorId,
        campDay = campDay,
        timeStamp = timeStamp,
        refereeId = refereeId,
        toBeAwarded = toBeAwarded,
        isAwarded = isAwarded,
        toBeRemoved = toBeRemoved,
        isRemoved = isRemoved
    )
}