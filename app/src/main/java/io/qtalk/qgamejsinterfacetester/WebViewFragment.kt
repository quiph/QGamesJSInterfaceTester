package io.qtalk.qgamejsinterfacetester

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import io.qtalk.qgamejsinterfacetester.helpers.PreferenceManager
import io.qtalk.qgamejsinterfacetester.helpers.generateSHA1
import io.qtalk.qgamejsinterfacetester.views.PermissionAwareWebViewFragment
import kotlinx.android.synthetic.main.webview_fragment.*

class WebViewFragment: PermissionAwareWebViewFragment() {

    companion object {
        private const val JS_INTERFACE_OBJECT_NAME = "QTalkApp"

        private const val TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

        private const val ARG_URL_STRING = "arg-url"

        private var isTestUrl = false

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

        isTestUrl = getLoadedUrl()!!.startsWith("file:///")

        webView.addJavascriptInterface(JSInterface(activity!!, webView), JS_INTERFACE_OBJECT_NAME)

        clearLog.setOnClickListener {
            logText.text = ""
        }
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?) {
        super.onConsoleMessage(consoleMessage)
        consoleMessage?.apply {
            logText.append("${message()} ------ ${sourceId()}:${lineNumber()}\n")
            logsScrollView.fullScroll(View.FOCUS_DOWN)
        }

    }

    @Suppress("unused")
    private class JSInterface(private val context: Context, private val webView: WebView) {

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

        @JavascriptInterface
        fun updateGamePrompts(prompts: String){
            if (isTestUrl) {
                webView.handler.post {
                    webView.evaluateJavascript("setPromptToText($prompts)") { Log.d("JSInterface", "onReceiveValue") }
                }
            }else {
                Toast.makeText(context, "Prompts recieved: $prompts", Toast.LENGTH_LONG).show()
            }
        }

        @JavascriptInterface
        fun clearGamePrompts() {
            if (isTestUrl) {
                webView.handler.post {
                    webView.evaluateJavascript("setPromptToText(\"\")") { Log.d("JSInterface", "onReceiveValue") }
                }
            }else {
                Toast.makeText(context, "Prompts cleared", Toast.LENGTH_LONG).show()
            }
        }

        // todo add saveBase64 method

            // test only
        @JavascriptInterface
        fun clearWebViewCache(){
            Log.d("JSInterface", "clearWebViewCache: ")
            webView.handler.post {
                Toast.makeText(context, "Cache cleared!", Toast.LENGTH_LONG).show()
                webView.clearCache(true)
            }
        }
    }
}