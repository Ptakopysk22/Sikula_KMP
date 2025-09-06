package cz.bosan.sikula_kmp.managers.points_manager.domain

data class PointRecord(
    val crewId: Int,
    val disciplineId: Int,
    val description: String,
    val campDay: Int,
    val value: Double?,
)
