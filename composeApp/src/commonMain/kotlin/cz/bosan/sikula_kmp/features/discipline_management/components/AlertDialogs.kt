package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.children_lowcase
import sikula_kmp.composeapp.generated.resources.childs
import sikula_kmp.composeapp.generated.resources.competitors
import sikula_kmp.composeapp.generated.resources.competitors_more
import sikula_kmp.composeapp.generated.resources.competitors_on_trail_format
import sikula_kmp.composeapp.generated.resources.crews
import sikula_kmp.composeapp.generated.resources.no
import sikula_kmp.composeapp.generated.resources.ok
import sikula_kmp.composeapp.generated.resources.restart_timer_confirmation_format
import sikula_kmp.composeapp.generated.resources.restart_timer_format
import sikula_kmp.composeapp.generated.resources.timer_couldnt_be_canceled
import sikula_kmp.composeapp.generated.resources.yes

@Composable
fun AlertDialogRestartChild(
    discipline: Discipline,
    itemName: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val competitor =
        if (discipline == Discipline.Individual.TRAIL)
            stringResource(Res.string.childs)
        else if (discipline == Discipline.Team.BOAT_RACE)
            stringResource(Res.string.crews)
        else
            stringResource(Res.string.competitors)

    AlertDialog(
        title = { Text(text = stringResource(Res.string.restart_timer_format, competitor)) },
        text = { Text(stringResource(Res.string.restart_timer_confirmation_format, itemName)) },
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(Res.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(Res.string.no))
            }
        }
    )
}

@Composable
fun QuitTimerAlertDialog(
    discipline: Discipline,
    onDismiss: () -> Unit,
) {
    val competitors =
        if (discipline == Discipline.Individual.TRAIL)
            stringResource(Res.string.children_lowcase)
        else if (discipline == Discipline.Team.BOAT_RACE)
            stringResource(Res.string.crews)
        else
            stringResource(Res.string.competitors_more)

    AlertDialog(
        title = { Text(text = stringResource(Res.string.timer_couldnt_be_canceled)) },
        text = { Text(stringResource(Res.string.competitors_on_trail_format, competitors)) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.ok))
            }
        },
    )
}