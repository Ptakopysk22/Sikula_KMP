package cz.bosan.sikula_kmp.features.about_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.presentation.components.ContainerBox
import cz.bosan.sikula_kmp.core.presentation.components.MainTopBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.about_app_text
import sikula_kmp.composeapp.generated.resources.description_about_app
import sikula_kmp.composeapp.generated.resources.developer_team
import sikula_kmp.composeapp.generated.resources.version
import sikula_kmp.composeapp.generated.resources.year_of_check

@Composable
fun AboutAppRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AboutAppViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AboutAppScreen(
        state = state,
        modifier = modifier,
        onBackClick = onBackClick
    )
}

@Composable
private fun AboutAppScreen(
    state: AboutAppState,
    modifier: Modifier,
    onBackClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            MainTopBar(
                currentLeader = state.currentLeader,
                textInBox = stringResource(Res.string.description_about_app),
                onBackClick = onBackClick,
                showProfile = false,
                showButton = false,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WrapBox(
                    isLoading = state.isLoading,
                    errorMessage = null,
                    content = {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            ContainerBox(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                content = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.version),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            )
                            ContainerBox(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                content = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            textAlign = TextAlign.Justify,
                                            text = stringResource(Res.string.about_app_text),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            )
                            ContainerBox(
                                title = stringResource(Res.string.developer_team),
                                modifier = Modifier.fillMaxWidth().weight(1f)
                                    .padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                                content = {
                                    DeveloperList(
                                        developers = state.developers,
                                        scrollState = rememberLazyListState()
                                    )
                                },
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(text = stringResource(Res.string.year_of_check), style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    },
                )
            }
        }
    }
}
