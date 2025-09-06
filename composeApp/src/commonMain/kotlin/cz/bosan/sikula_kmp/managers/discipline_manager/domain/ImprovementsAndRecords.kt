package cz.bosan.sikula_kmp.managers.discipline_manager.domain

data class ImprovementsAndRecords(
    val countsForImprovements: Boolean? = null,
    val improvement: Int? = null,
    val improvementString: String? = null,
    val improvementValue: Int? = null,
    val improvementTargetString: String? = null,
    val improvementTargetValue: Int? = null,
    val isRecord: Boolean? = null,
)
