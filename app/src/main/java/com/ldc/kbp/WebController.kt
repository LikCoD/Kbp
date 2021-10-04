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
    var setup: (WebView.() -> Unit)? = null,
    val scriptName: String = "getHtml.js",
    var resName: String = "",
    var onLoadRes: ((String) -> Unit)? = null,
    var onLoad: (String?, String) -> Unit = {_, _ ->},
) {
    val webView = WebView(context)

    fun load() {
        webView.stopLoading()
        webView.loadUrl(link)
    }

    fun js(script: String, callback: (String) -> Unit = {}) = webView.evaluateJavascript(script, callback)

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

            override fun onPageFinished(view: WebView?, url: String?) {
                if (view != null && setup != null)
                    setup?.invoke(view)

                getHtml { onLoad(url, it) }
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                if (view != null && onLoadRes != null && resName == url)
                    getHtml { onLoadRes!!.invoke(it) }

                super.onLoadResource(view, url)
            }
        }
    }

    fun getHtml(onLoad: (String) -> Unit) {
        js(getAssets(context, scriptName)) {
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