package cz.bosan.sikula_kmp.managers.camp_manager.domain

import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now

data class Camp(
    val id: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val name: String,
) {
    companion object {
        val EMPTY = Camp(
            id = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            name = ""
        )
    }
}