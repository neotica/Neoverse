package id.neotica.profile.presentation.privacypolicy

import androidx.compose.runtime.Composable
import id.neotica.droidcore.component.button.NeoButton
import id.neotica.neoverse.utils.privacyPolicy

@Composable
actual fun PrivacyPolicyView(
    state: PrivacyPolicyState,
    onLoadingChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenExternal: (String) -> Unit,
) {
    NeoButton("Open privacy policy in safari") {
        onOpenExternal(privacyPolicy)
    }
}
