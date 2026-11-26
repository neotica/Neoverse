package id.neotica.profile.presentation.termsofservice

import androidx.compose.runtime.Composable
import id.neotica.droidcore.component.button.NeoButton
import id.neotica.neoverse.utils.termsOfService

@Composable
actual fun TermsOfServiceView(
    state: TermsOfServiceState,
    onLoadingChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenExternal: (String) -> Unit,
) {
    NeoButton("Open terms of service in safari") {
        onOpenExternal(termsOfService)
    }
}
