package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getRoleIcon
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getRoleName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    currentLeader: CurrentLeader,
    textInBox: String? = null,
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    showProfile: Boolean,
    keyboardController: SoftwareKeyboardController? = null,
    showButton: Boolean,
    onButtonClick: () -> Unit = {},
    buttonContent: @Composable () -> Unit = {},
    enabledButton: Boolean = false,
    modifier: Modifier = Modifier
) {

    TopAppBar(
        title = {},
        modifier = modifier,
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.background
        ),
        navigationIcon = {},
        actions = {}
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 2.dp)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (showProfile) {
                Box(modifier = Modifier.padding(start = 18.dp, top = 5.dp)) {
                    Button(
                        onClick = onProfileClick,
                        modifier = Modifier.size(37.dp).align(Alignment.CenterStart),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        content = {
                            ProfilePicture(
                                currentLeader = currentLeader,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    )
                }
            } else {
                BackButtonRow(
                    onBackClick = onBackClick,
                    keyboardController = keyboardController,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
            textInBox?.let {
                TopBox(
                    text = it,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Row(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp, top = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showButton) {
                    PrimaryButton(
                        content = buttonContent,
                        enabled = enabledButton,
                        onClick = onButtonClick,
                         modifier = Modifier.offset(x = 17.dp, y = -(2).dp)
                    )
                } else {
                    Icon(
                        painter = getRoleIcon(currentLeader.leader.role),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = currentLeader.leader.role.let { getRoleName(it) },
                        modifier = Modifier.size(30.dp)
                    )
                }

            }
        }
    }
}