package id.neotica.profile.termsofservice

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import id.neotica.neoverse.utils.termsOfService
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.theme.NeoPadding

@Composable
fun TermsOfServiceView(navController: NavController) {
    BasicScaffold(
        topBarTitle = "Terms of Service",
        true,
        navController = navController,
        fullScreen = true
    ) {
        var isLoading by remember { mutableStateOf(true) }
        if (isLoading) BackHandler {}

        Box(Modifier.padding(top = NeoPadding.topBarPadding)) {
            AndroidView(
                factory = {
                    WebView(it).apply {
//                        settings.javaScriptEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webViewClient = object : WebViewClient() {
                            override fun onReceivedSslError(
                                view: WebView?,
                                handler: SslErrorHandler?,
                                error: SslError?
                            ) {
                                isLoading = false
                                handler?.cancel()
                                navController.navigateUp().apply {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsOfService))
                                    context.startActivity(intent)
                                }
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                            }

                            override fun onPageCommitVisible(view: WebView?, url: String?) {
                                super.onPageCommitVisible(view, url)
                                isLoading = false
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                return super.shouldOverrideUrlLoading(view, request)
                            }
                        }
                    }
                },
                update = {
                    it.loadUrl(termsOfService)
                }
            )
            if (isLoading) {
                LoadingIndicator()
            }
        }

    }
}