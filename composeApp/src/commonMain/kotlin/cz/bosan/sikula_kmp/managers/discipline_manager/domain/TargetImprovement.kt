package cz.bosan.sikula_kmp.managers.discipline_manager.domain

data class TargetImprovement(
    val id: Int,
    val improvementString: String?,
    val improvementValue: Int?,
    val improvementTargetString: String?,
    val improvementTargetValue: Int?,
)