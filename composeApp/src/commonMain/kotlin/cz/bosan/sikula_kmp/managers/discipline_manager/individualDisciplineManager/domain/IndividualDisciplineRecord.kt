package cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain

import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

data class IndividualDisciplineRecord(
    val id: Int?,
    val competitorId: Int,
    val campDay: Int,
    val value: String?,
    val timeStamp: LocalDateTime,
    val refereeId: Int,
    val comment: String,
    val countsForImprovement: Boolean? = null,
    val improvement: String? = null,
    val isRecord: Boolean? = null,
    val quest: Int? = null,
    val workedOff: Boolean? = null,
    val isUploaded: Boolean? = null,
) {
    companion object {
        val EMPTY = IndividualDisciplineRecord(
            id = 0,
            competitorId = 0,
            campDay = 0,
            value = null,
            timeStamp = LocalDateTime.now(),
            refereeId = 0,
            comment = "",
        )
    }
}
