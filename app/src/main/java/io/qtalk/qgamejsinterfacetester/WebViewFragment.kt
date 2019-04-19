package io.qtalk.qgamejsinterfacetester

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.qtalk.qgamejsinterfacetester.helpers.PreferenceManager
import kotlinx.android.synthetic.main.webview_fragment.*

fun String?.formatUrl(): String {
    if (this.isNullOrEmpty()) throw IllegalAccessException("Need a valid url, current url passed is $this")

    // if loading local file, load it without adding http or http:// prefix
    if (startsWith("file://")) return this

    return if (!startsWith("https://") && !startsWith("http://")) {
        "http://$this"
    } else {
        this
    }
}

class WebViewFragment: Fragment() {

    companion object {
        private const val JS_INTERFACE_OBJECT_NAME = "QTalkApp"

        private const val TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

        private const val ARG_URL_STRING = "arg-url"
        fun init(url: String): WebViewFragment {
            return WebViewFragment().apply {
                arguments = Bundle(1).also {
                    it.putString(ARG_URL_STRING, url)
                }
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.webview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.addJavascriptInterface(JSInterface(activity!!), JS_INTERFACE_OBJECT_NAME)
        loadWebView((arguments?.getString(ARG_URL_STRING).formatUrl()))
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(url: String) {
        webView.webViewClient = InAppBrowser()

        webView.settings.javaScriptEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        webView.loadUrl(url)
    }

    private inner class InAppBrowser : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            view?.visibility = View.GONE
            webViewProgressBar?.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            webViewProgressBar?.visibility = View.GONE
            view?.visibility = View.VISIBLE
        }

        @Suppress("OverridingDeprecatedMember")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }
    }

    @Suppress("unused")
    private class JSInterface(private val context: Context) {

        @JavascriptInterface
        fun getUserAuthToken(): String {
            return PreferenceManager.getString(context, PreferenceManager.KEY_SELECTED_USER)?.generateSHA1() ?: TEST_TOKEN
        }

        @JavascriptInterface
        fun notifyGameRoundStarted(){
            Toast.makeText(context, "Game round started notified!", Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun notifyGameRoundEnded(){
            Toast.makeText(context, "Game round ended notified!", Toast.LENGTH_SHORT).show()
        }
    }
}