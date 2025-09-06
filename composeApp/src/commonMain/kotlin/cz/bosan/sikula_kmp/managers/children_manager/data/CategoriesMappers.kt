package cz.bosan.sikula_kmp.managers.children_manager.data

import cz.bosan.sikula_kmp.managers.camp_manager.presentation.colorToHex
import cz.bosan.sikula_kmp.managers.camp_manager.presentation.hexToColor
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory

fun TrailCategoryDto.toTrailCategory(): TrailCategory {
    return TrailCategory(
        id = id,
        name = name,
        description = description,
        baseTime = baseTime,
        color = hexToColor(color)
    )
}

fun TrailCategory.toTrailCategoryEntity(campId: Int): TrailCategoryEntity {
    return TrailCategoryEntity(
        id = id,
        campId = campId,
        name = name,
        description = description,
        baseTime = baseTime,
        color = colorToHex(color)
    )
}

fun TrailCategoryEntity.toTrailCategory(): TrailCategory {
    return TrailCategory(
        id = id,
        name = name,
        description = description,
        baseTime = baseTime,
        color = hexToColor(color)
    )
}