package cz.bosan.sikula_kmp.managers.discipline_manager.data

import cz.bosan.sikula_kmp.managers.discipline_manager.domain.TargetImprovement

fun ImprovementsCompetitorOrCrewDto.toTargetImprovements() : TargetImprovement{
    return TargetImprovement(
        id = id,
        improvementString = improvementString,
        improvementValue = improvementValue,
        improvementTargetString = improvementTargetString,
        improvementTargetValue = improvementTargetValue
    )
}