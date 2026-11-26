package id.neotica.profile.presentation.privacypolicy

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import id.neotica.neoverse.utils.privacyPolicy
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.theme.NeoPadding
import org.jetbrains.compose.ui.tooling.preview.Preview

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun PrivacyPolicyView(
    state: PrivacyPolicyState,
    onLoadingChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenExternal: (String) -> Unit,
) {
    BasicScaffold(
        topBarTitle = "Privacy Policy",
        enableActionBar = true,
        customActionBar = onBack,
        fullScreen = true,
    ) {
        if (state.isLoading) BackHandler {}
        Box(Modifier.padding(top = NeoPadding.topBarPadding)) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webViewClient = object : WebViewClient() {
                            override fun onReceivedSslError(
                                view: WebView?,
                                handler: SslErrorHandler?,
                                error: SslError?,
                            ) {
                                onLoadingChanged(false)
                                handler?.cancel()
                                onBack()
                                onOpenExternal(privacyPolicy)
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                onLoadingChanged(true)
                            }

                            override fun onPageCommitVisible(view: WebView?, url: String?) {
                                super.onPageCommitVisible(view, url)
                                onLoadingChanged(false)
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?,
                            ): Boolean = super.shouldOverrideUrlLoading(view, request)
                        }
                    }
                },
                update = { it.loadUrl(privacyPolicy) },
            )
            if (state.isLoading) LoadingIndicator()
        }
    }
}

@Preview
@Composable
private fun PrivacyPolicyViewPreview() {
    PrivacyPolicyView(
        state = PrivacyPolicyState(isLoading = false),
        onLoadingChanged = {},
        onBack = {},
        onOpenExternal = {},
    )
}
