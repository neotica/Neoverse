package id.neotica.neoverse.presentation.screen.profile.privacypolicy

import android.annotation.SuppressLint
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PrivacyPolicyView(navController: NavController, paddingValues: PaddingValues, modifier: Modifier = Modifier) {
    BasicScaffold(
        topBarTitle = "Privacy Policy",
        true,
        navController = navController,
        paddingValues = paddingValues
    ) {
        AndroidView(
            factory = {
                WebView(it).apply {
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    webViewClient = object : WebViewClient() {
                        @SuppressLint("WebViewClientOnReceivedSslError")
                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            handler?.proceed()
                        }
                    }
                }
            },
            update = {
                it.loadUrl("https://neotica.id/privacy-policy.html")
            }
        )
    }
}