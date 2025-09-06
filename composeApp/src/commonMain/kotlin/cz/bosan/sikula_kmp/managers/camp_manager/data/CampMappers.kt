package cz.bosan.sikula_kmp.managers.camp_manager.data

import cz.bosan.sikula_kmp.managers.camp_manager.domain.Camp
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.camp_manager.presentation.colorToHex
import cz.bosan.sikula_kmp.managers.camp_manager.presentation.hexToColor

fun CampDto.toCamp(): Camp {
    return Camp(
        id = id,
        startDate = startDate,
        endDate = endDate,
        name = name
    )
}

fun GroupDto.toGroup(): Group {
    return Group(
        id = id,
        name = name,
        color = hexToColor(color),
        crews = crews.map { Crew(it.id, it.groupId, it.name,
            hexToColor(it.color)
        ) }
    )
}

fun CrewDto.toCrew(): Crew {
    return Crew(
        id = id,
        groupId = groupId,
        name = name,
        color = hexToColor(color)
    )
}

fun Crew.toCrewDto(): CrewDto {
    return CrewDto(
        id = id,
        groupId = groupId,
        name = name,
        color = colorToHex(color)
    )
}

fun CrewEntity.toCrew(): Crew {
    return Crew(
        id = id,
        groupId = groupId,
        name = name,
        color = hexToColor(color)
    )
}

fun Crew.toCrewEntity(campId: Int): CrewEntity {
    return CrewEntity(
        id = id,
        groupId = groupId,
        name = name,
        color = colorToHex(color),
        campId = campId
    )
}