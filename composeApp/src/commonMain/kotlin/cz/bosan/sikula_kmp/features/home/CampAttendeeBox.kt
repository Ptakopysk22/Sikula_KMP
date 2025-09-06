package cz.bosan.sikula_kmp.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.components.ContainerBox
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.managers.user_manager.AttendeesCount
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.camp_attendees
import sikula_kmp.composeapp.generated.resources.children_count
import sikula_kmp.composeapp.generated.resources.leader_count

@Composable
fun CampAttendeesBox(
    campAttendees: AttendeesCount?,
    errorMessage: UiText?,
    modifier: Modifier = Modifier
) {
    ContainerBox(
        modifier = modifier,
        title = stringResource(Res.string.camp_attendees),
        content = {
            if (errorMessage != null) {
                Message(
                    text = errorMessage.asString(),
                    messageTyp = MessageTyp.ERROR
                )
            } else {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        )
                        {
                            Text(
                                text = stringResource(
                                    Res.string.children_count,
                                ),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = campAttendees?.kidsCount.toString(),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        )
                        {
                            Text(
                                text = stringResource(
                                    Res.string.leader_count,
                                ),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = campAttendees?.leadersCount.toString(),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        )
                        {
                            Text(
                                text = campAttendees?.totalAttendees.toString(),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                }
            }
        }
    )
}