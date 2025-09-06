package cz.bosan.sikula_kmp.features.about_app

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.bosan.sikula_kmp.app.Graph
import kotlinx.serialization.Serializable

private interface AboutApp {
    @Serializable
    data object Root
}

fun NavGraphBuilder.aboutApp(
    onBackClick: () -> Unit,
) {
    navigation<Graph.AboutApp>(
        startDestination = AboutApp.Root,
    ) {
        composable<AboutApp.Root> {
            AboutAppRoute(
                onBackClick = onBackClick,
            )
        }
    }
}