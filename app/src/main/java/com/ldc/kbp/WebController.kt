package com.ldc.kbp

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

@SuppressLint("SetJavaScriptEnabled")
class WebController(
    val context: Context,
    var link: String,
    var isSetup: Boolean = false,
    var setup: (WebView.() -> Unit)? = null,
    val scriptName: String = "getHtml.js",
    var loadRes: Boolean = false,
    var onLoadRes: (String) -> Unit = {},
    var onLoad: (String?, String) -> Unit = {_, _ ->},
) {
    val webView = WebView(context)

    fun load() {
        webView.stopLoading()
        webView.loadUrl(link)
    }

    init {
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.databaseEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            var loadCount = 0

            override fun onPageFinished(view: WebView?, url: String?) {
                loadCount = 0
                if (view != null && setup != null && isSetup)
                    setup?.invoke(view)

                getHtml { onLoad(url, it) }
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                if (loadRes && loadCount == 2) {
                    getHtml {
                        onLoadRes(it)
                    }
                }
                loadCount++
                super.onLoadResource(view, url)
            }
        }
    }

    fun getHtml(onLoad: (String) -> Unit) {
        webView.evaluateJavascript(getAssets(context, scriptName)) {
            val html = it.replace("\\n", "")
                .replace("\\t", "")
                .replace("\\\"", "\"")
                .replace("\\u003C", "<")
                .replace("\"\"", "<")
                .drop(1).dropLast(1)

            onLoad(html)
        }
    }
}