package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.PrimaryButton
import cz.bosan.sikula_kmp.core.presentation.components.formatDateTime
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.id
import cz.bosan.sikula_kmp.core.presentation.components.forms.TextField
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import sikula_kmp.composeapp.generated.resources.comment
import sikula_kmp.composeapp.generated.resources.comment_format
import sikula_kmp.composeapp.generated.resources.count_for_improvement
import sikula_kmp.composeapp.generated.resources.counted_format
import sikula_kmp.composeapp.generated.resources.date_and_time_format
import sikula_kmp.composeapp.generated.resources.id_format
import sikula_kmp.composeapp.generated.resources.local_id_format
import sikula_kmp.composeapp.generated.resources.no
import sikula_kmp.composeapp.generated.resources.no_record
import sikula_kmp.composeapp.generated.resources.not_count_for_improvement
import sikula_kmp.composeapp.generated.resources.referee_format
import sikula_kmp.composeapp.generated.resources.save
import sikula_kmp.composeapp.generated.resources.with_barriers
import sikula_kmp.composeapp.generated.resources.without_barriers
import sikula_kmp.composeapp.generated.resources.worked_off_format
import sikula_kmp.composeapp.generated.resources.yes

@Composable
fun RecordDetail(
    record: IndividualDisciplineRecord,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean? = false,
    onSaveComment: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val refereeNickname: String =
        leaders.find { it.id == record.refereeId }?.nickName ?: stringResource(
            Res.string.id,
            record.refereeId
        )
    var comment by remember { mutableStateOf(record.comment) }
    var nonSynchronizeComment by remember { mutableStateOf(false) }

    /* DisposableEffect(Unit) {
         onDispose {
             onSaveComment(comment)
         }
     }*/

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-10).dp),
        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, top = 2.dp, end = 10.dp, bottom = 4.dp)) {
            Spacer(modifier = Modifier.height(12.dp))
            if (record.id == 0) {
                Text(
                    text = stringResource(Res.string.no_record),
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                Text(
                    text = if (record.isUploaded == true) stringResource(
                        Res.string.id_format,
                        record.id.toString()
                    ) else stringResource(Res.string.local_id_format, record.id.toString()),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = stringResource(Res.string.referee_format, refereeNickname),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = stringResource(
                        Res.string.date_and_time_format,
                        formatDateTime(record.timeStamp)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
                record.countsForImprovement?.let {
                    Text(
                        text = stringResource(
                            Res.string.counted_format,
                            if (it) stringResource(Res.string.with_barriers) else stringResource(Res.string.without_barriers)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                record.workedOff?.let {
                    Text(
                        text = stringResource(
                            Res.string.worked_off_format,
                            if (it) stringResource(Res.string.yes) else stringResource(Res.string.no)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (enabledUpdateRecords == true) {
                    Row(
                        modifier = Modifier.offset(x = (-12).dp, y = (-4).dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            text = comment,
                            onTextChange = {
                                comment = it
                                nonSynchronizeComment = true
                            },
                            label = stringResource(Res.string.comment),
                            keyboardController = keyboardController,
                            modifier = Modifier.weight(0.8f)
                        )
                        PrimaryButton(
                            content = {
                                Text(
                                    text = stringResource(Res.string.save),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            },
                            onClick = {
                                onSaveComment(comment)
                                nonSynchronizeComment = false
                            },
                            enabled = nonSynchronizeComment,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                } else {
                    Text(
                        text = stringResource(Res.string.comment_format, record.comment),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}


@Composable
fun RecordDetailTeamDiscipline(
    record: TeamDisciplineRecord,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean? = false,
    onSaveComment: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val refereeNickname: String =
        leaders.find { it.id == record.refereeId }?.nickName ?: stringResource(
            Res.string.id,
            record.refereeId
        )
    var comment by remember { mutableStateOf(record.comment) }
    var nonSynchronizeComment by remember { mutableStateOf(false) }

    /* DisposableEffect(Unit) {
         onDispose {
             onSaveComment(comment)
         }
     }*/

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-10).dp),
        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, top = 2.dp, end = 10.dp, bottom = 4.dp)) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (record.isUploaded == true) stringResource(
                    Res.string.id_format,
                    record.id.toString()
                ) else stringResource(Res.string.local_id_format, record.id.toString()),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(Res.string.referee_format, refereeNickname),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(
                    Res.string.date_and_time_format,
                    formatDateTime(record.timeStamp)
                ),
                style = MaterialTheme.typography.bodySmall,
            )
            record.improvementsAndRecords?.countsForImprovements?.let {
                Text(
                    text = stringResource(
                        Res.string.counted_format,
                        if (it) stringResource(Res.string.count_for_improvement) else stringResource(
                            Res.string.not_count_for_improvement
                        )
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (enabledUpdateRecords == true) {
                Row(
                    modifier = Modifier.offset(x = (-12).dp, y = (-4).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        text = comment,
                        onTextChange = {
                            comment = it
                            nonSynchronizeComment = true
                        },
                        label = stringResource(Res.string.comment),
                        keyboardController = keyboardController,
                        modifier = Modifier.weight(0.8f)
                    )
                    PrimaryButton(
                        content = {
                            Text(
                                text = stringResource(Res.string.save),
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            onSaveComment(comment)
                            nonSynchronizeComment = false
                        },
                        enabled = nonSynchronizeComment,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            } else {
                Text(
                    text = stringResource(Res.string.comment_format, record.comment),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}