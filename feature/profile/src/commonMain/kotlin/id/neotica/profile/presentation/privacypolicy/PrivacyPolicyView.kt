package id.neotica.profile.presentation.privacypolicy

import androidx.compose.runtime.Composable

@Composable
expect fun PrivacyPolicyView(
    state: PrivacyPolicyState,
    onLoadingChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenExternal: (String) -> Unit,
)
