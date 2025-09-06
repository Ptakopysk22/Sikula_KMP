package cz.bosan.sikula_kmp.features.signin

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.bosan.sikula_kmp.app.Graph
import kotlinx.serialization.Serializable

interface SignIn {
    @Serializable
    data object Root : SignIn
}

fun NavGraphBuilder.signIn(
    onSignInFlowFinished: () -> Unit,
) {
    navigation<Graph.SignIn>(
        startDestination = SignIn.Root
    ) {
        composable<SignIn.Root>{
            SignInRoute(
                onSignInFlowFinished = onSignInFlowFinished,
            )
        }
    }
}

