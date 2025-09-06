package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.features.discipline_management.count_recording_individual_discipline.CountRecordingBar
import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import kotlinx.datetime.LocalTime

@Composable
fun RecordingElement(
    discipline: Discipline,
    clickedItemName: String? = null,
    onValueChange: (String?) -> Unit = {},
    hideKeyboardAfterCheck: Boolean = true,
    showCrossIcon: Boolean = false,
    isNextRecord: Boolean = false,
    showTimePicker: Boolean = false,
    onShowTimePickerChange: () -> Unit = {},
    onUpdateTimeClick: () -> Unit = {},
    chooseFromResultOption: Boolean = true,
    childRole: ChildRole = ChildRole.MEMBER
) {
    if (discipline == Discipline.Individual.PULL_UPS || discipline == Discipline.Team.CORRECTIONS || discipline == Discipline.Team.BONUSES || discipline == Discipline.Team.THEME_GAME || (discipline == Discipline.Individual.MORSE && !chooseFromResultOption) || (discipline == Discipline.Team.QUIZ && !chooseFromResultOption)) {
        NumberDisciplineField(
            value = if (clickedItemName == "null") null else clickedItemName,
            hideKeyboardAfterCheck = hideKeyboardAfterCheck,
            onValueChange = onValueChange,
            isNextRecord = isNextRecord,
            discipline = discipline
        )
    } else if (discipline == Discipline.Individual.ROPE_CLIMBING || discipline == Discipline.Individual.GRENADES || discipline == Discipline.Individual.TIDYING || discipline == Discipline.Individual.AGILITY || discipline == Discipline.Individual.NEGATIVE_POINTS || discipline == Discipline.Individual.MORSE || discipline == Discipline.Team.QUIZ) {
        CountRecordingBar(
            items = if (discipline == Discipline.Individual.NEGATIVE_POINTS) {
                if (childRole == ChildRole.CREW_MASTER) listOf("-3", "-5") else listOf("-1")
            } else discipline.getResultOptions(),
            clickedItemName = clickedItemName,
            onItemClick = onValueChange,
            showCrossIcon = showCrossIcon
        )
    } else if (discipline == Discipline.Individual.TRAIL || discipline == Discipline.Team.BOAT_RACE) {
        val time: LocalTime?
        if (clickedItemName != null && clickedItemName != "null") {
            val rawTime = clickedItemName.toInt()
            val hours = rawTime / 60
            val minutes = rawTime % 60
            time = LocalTime(hour = hours, minute = minutes, second = 0)
        } else {
            time = null
        }
        TimePicker(
            time = time,
            modifier = Modifier.padding(end = 20.dp),
            showTimePicker = showTimePicker,
            onShowTimePickerChange = onShowTimePickerChange,
            onUpdateClick = onUpdateTimeClick,
            onTimeSelected = { time ->
                if(time == null){
                    onValueChange(null)
                } else{
                    onValueChange((time.minute + (time.hour * 60)).toString())
                }
            },
        )
    }
}