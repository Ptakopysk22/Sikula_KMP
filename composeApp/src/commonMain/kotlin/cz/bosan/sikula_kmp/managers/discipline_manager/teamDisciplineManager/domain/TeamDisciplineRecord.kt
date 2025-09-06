package cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain

import cz.bosan.sikula_kmp.managers.discipline_manager.domain.ImprovementsAndRecords
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

data class TeamDisciplineRecord(
    val id: Int?,
    val crewId: Int,
    val campDay: Int,
    val value: String?,
    val timeStamp: LocalDateTime,
    val refereeId: Int,
    val comment: String,
    val improvementsAndRecords: ImprovementsAndRecords? = null,
    val isUploaded: Boolean? = null,
) {
    companion object {
        val EMPTY = TeamDisciplineRecord(
            id = null,
            crewId = 0,
            campDay = 0,
            value = null,
            timeStamp = LocalDateTime.now(),
            refereeId = 0,
            comment = "",
            improvementsAndRecords = null,
            isUploaded = null
        )
    }
}
