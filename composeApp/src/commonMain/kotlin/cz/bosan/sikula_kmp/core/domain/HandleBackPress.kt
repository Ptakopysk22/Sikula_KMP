package cz.bosan.sikula_kmp.core.domain

import androidx.compose.runtime.Composable

@Composable
expect fun HandleBackPress(enabled: Boolean = true, onBack: () -> Unit)