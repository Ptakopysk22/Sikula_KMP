package cz.bosan.sikula_kmp.managers.discipline_manager.domain

enum class DayRecordsState {
    OFFLINE,
    IN_PROGRESS,
    CHECKED_BY_GROUP,
    CHECKED_BY_GAME_MASTER,
    NON_CHECKED_BY_GROUP,
    NON_SYNCHRONIZE,
    WITHOUT_STATE,
}