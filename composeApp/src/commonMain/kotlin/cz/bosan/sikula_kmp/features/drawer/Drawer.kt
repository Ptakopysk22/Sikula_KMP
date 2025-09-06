package cz.bosan.sikula_kmp.features.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.data.openUrl
import cz.bosan.sikula_kmp.core.presentation.components.PrimaryButton
import cz.bosan.sikula_kmp.core.presentation.components.ProfilePicture
import cz.bosan.sikula_kmp.core.presentation.components.formatDate
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getPositionName
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getRoleIcon
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getRoleName
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.camp
import sikula_kmp.composeapp.generated.resources.chat_circle_text
import sikula_kmp.composeapp.generated.resources.description_about_app
import sikula_kmp.composeapp.generated.resources.description_feedback
import sikula_kmp.composeapp.generated.resources.device_mobile
import sikula_kmp.composeapp.generated.resources.email
import sikula_kmp.composeapp.generated.resources.end
import sikula_kmp.composeapp.generated.resources.log_out
import sikula_kmp.composeapp.generated.resources.positions
import sikula_kmp.composeapp.generated.resources.role
import sikula_kmp.composeapp.generated.resources.start
import sikula_kmp.composeapp.generated.resources.without_positions

@Composable
fun Drawer(
    currentLeader: CurrentLeader,
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit,
    onAboutAppClick: () -> Unit
) {
    val firstColumnWidth = 85.dp
    val sortedPositions = currentLeader.leader.positions
        .map { it to getPositionName(it) }
        .sortedBy { (_, name) -> name }
    Column(
        modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(250.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp)
                )
                .padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                ProfilePicture(currentLeader, modifier = Modifier.padding(5.dp))
                Spacer(modifier = Modifier.height(5.dp))
                DrawerTextRow(
                    prefix = stringResource(Res.string.camp) + ": ",
                    value = currentLeader.camp.name
                )
                DrawerTextRow(
                    prefix = stringResource(Res.string.start) + ": ",
                    value = formatDate(currentLeader.camp.startDate)
                )
                DrawerTextRow(
                    prefix = stringResource(Res.string.end) + ": ",
                    value = formatDate(currentLeader.camp.endDate)
                )
                Row(modifier = Modifier.padding(top = 5.dp)) {
                    Text(
                        text = stringResource(Res.string.role) + ": ",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.alignBy(LastBaseline).width(firstColumnWidth)
                    )
                    Text(
                        text = getRoleName(currentLeader.leader.role),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alignBy(LastBaseline)
                    )
                    Icon(
                        painter = getRoleIcon(currentLeader.leader.role),
                        contentDescription = getRoleName(currentLeader.leader.role),
                        modifier = Modifier
                            .size(27.dp)
                            .alignBy(LastBaseline)
                            .padding(start = 8.dp)
                    )
                }
                DrawerTextRow(
                    prefix = stringResource(Res.string.email) + ": ",
                    value = currentLeader.leader.mail
                )
                Row(modifier = Modifier.padding(top = 5.dp)) {
                    Column {
                        Text(
                            text = stringResource(Res.string.positions) + ": ",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.width(firstColumnWidth)
                        )
                    }
                    Column(
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        for ((_, name) in sortedPositions) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        if(sortedPositions.isEmpty()){
                            Text(
                                text = stringResource(Res.string.without_positions),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp),
                ) {
                    PrimaryButton(
                        enabled = true,
                        onClick = {
                            onLogout()
                            onCloseDrawer()
                        },
                        content = {
                            Text(
                                text = stringResource(Res.string.log_out),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                ) {
                    IconButton(
                        onClick = { openUrl("https://docs.google.com/document/d/1L8kgfZaseIzWdUhNZ3cWSgdkzpBCWqIBsBSUtvFoCno/edit?usp=sharing") },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.chat_circle_text),
                            contentDescription = stringResource(Res.string.description_feedback),
                            //modifier = TODO()
                        )
                    }
                    IconButton(
                        onClick = { onAboutAppClick() },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.device_mobile),
                            contentDescription = stringResource(Res.string.description_about_app),
                            //modifier = TODO()
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun DrawerTextRow(prefix: String, value: String) {
    val firstColumnWidth = 85.dp
    Row(modifier = Modifier.padding(top = 5.dp)) {
        Text(
            text = prefix,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.alignBy(LastBaseline).width(firstColumnWidth)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alignBy(LastBaseline)
        )
    }
}