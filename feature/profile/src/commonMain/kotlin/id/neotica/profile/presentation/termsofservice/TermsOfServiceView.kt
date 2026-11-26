package id.neotica.profile.presentation.termsofservice

import androidx.compose.runtime.Composable

@Composable
expect fun TermsOfServiceView(
    state: TermsOfServiceState,
    onLoadingChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenExternal: (String) -> Unit,
)
