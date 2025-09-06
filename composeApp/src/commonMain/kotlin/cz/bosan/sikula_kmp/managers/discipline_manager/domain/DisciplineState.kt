package cz.bosan.sikula_kmp.managers.discipline_manager.domain

data class DisciplineState(
    val discipline: Discipline,
    val dayRecordsState: DayRecordsState?
)
