package cz.bosan.sikula_kmp.features.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getRoleName
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_logo
import sikula_kmp.composeapp.generated.resources.sign_in
import sikula_kmp.composeapp.generated.resources.sikula_main_logo

@Composable
fun SignInRoute(
    onSignInFlowFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.campSelected) {
        if (state.campSelected) {
            onSignInFlowFinished()
        }
    }

    SignInScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun SignInScreen(
    state: SignInState,
    modifier: Modifier = Modifier,
    onAction: (SignInAction) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        WrapBox(
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            content = {
                if (state.warningMessage != null) {
                    Message(
                        text = state.warningMessage.asString(),
                        messageTyp = MessageTyp.ERROR,
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.weight(0.2f))
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(0.7f),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.sikula_main_logo),
                                contentDescription = stringResource(Res.string.description_logo),
                                modifier = Modifier.size(380.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.2f))
                        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            GoogleButtonUiContainer(
                                filterByAuthorizedAccounts = false,
                                onGoogleSignInResult = { onAction(SignInAction.OnSignInResult(it)) }
                            ) {
                                if (!state.multipleCampsChoose) {
                                    Column {
                                        Button(
                                            onClick = { onClick() },
                                            colors = ButtonDefaults.buttonColors(
                                                MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.padding(horizontal = 24.dp)
                                        ) {
                                            Text(
                                                stringResource(Res.string.sign_in),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontSize = 28.sp
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 400.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        items(state.campsRoles.size) { index ->
                                            val (camp, role) = state.campsRoles[index]
                                            ElevatedButton(
                                                onClick = {
                                                    onAction(SignInAction.OnCampSelected(camp))
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    MaterialTheme.colorScheme.primary
                                                ),
                                                modifier = Modifier.fillMaxWidth(0.8f),
                                                content = {
                                                    Column(
                                                        modifier = Modifier.padding(4.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            camp.name,
                                                            style = MaterialTheme.typography.titleLarge,
                                                            color = MaterialTheme.colorScheme.onPrimary
                                                        )
                                                        Text(
                                                            getRoleName(role),
                                                            style = MaterialTheme.typography.titleSmall,
                                                            color = MaterialTheme.colorScheme.onPrimary
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.3f))

                    }
                }
            }
        )
    }
}