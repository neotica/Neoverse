package id.neotica.neoverse.presentation.screen.profile.privacypolicy

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PrivacyPolicyView(navController: NavController, paddingValues: PaddingValues, modifier: Modifier = Modifier) {
    BasicScaffold(
        topBarTitle = "Privacy Policy",
        true,
        navController = navController,
        paddingValues = paddingValues
    ) {
        var isLoading by remember { mutableStateOf(false) }

        Box {
            AndroidView(
                factory = {
                    isLoading = true
                    WebView(it).apply {
                        settings.javaScriptEnabled = true
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
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://neotica.id/privacy-policy.html"))
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
                    it.loadUrl("https://neotica.id/privacy-policy.html")
                }
            )
            if (isLoading) {
                LoadingIndicator()
            }
        }
    }
}