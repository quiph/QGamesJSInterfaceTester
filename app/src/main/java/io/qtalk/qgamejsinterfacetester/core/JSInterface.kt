package io.qtalk.qgamejsinterfacetester.core

import android.webkit.JavascriptInterface

@Suppress("unused")
class JSInterface(private var jsInterfaceBridge: JSInterfaceBridge? = null) {

    fun setJSInterfaceBridge(jsInterfaceBridge: JSInterfaceBridge?) {
        this.jsInterfaceBridge = jsInterfaceBridge
    }

    @JavascriptInterface
    fun getUserAuthToken(): String {
        return jsInterfaceBridge?.getUserAuthToken()!!
    }

    @JavascriptInterface
    fun notifyGameRoundStarted() {
        jsInterfaceBridge?.notifyGameRoundStarted()
    }

    @JavascriptInterface
    fun notifyGameRoundEnded() {
        jsInterfaceBridge?.notifyGameRoundEnded()
    }

    @JavascriptInterface
    fun updateGamePrompts(prompts: String) {
        jsInterfaceBridge?.updateGamePrompts(prompts)
    }

    @JavascriptInterface
    fun clearGamePrompts() {
        jsInterfaceBridge?.clearGamePrompts()
    }

    @Deprecated(
        "Not supported any more",
        replaceWith = ReplaceWith("saveBase64Image(base64EncodedImageString)")
    )
    @JavascriptInterface
    fun onBase64ImageSaved(base64EncodedImageString: String) {
        saveBase64Image(base64EncodedImageString)
    }

    @JavascriptInterface
    fun saveBase64Image(base64EncodedImageString: String) {
        jsInterfaceBridge?.saveBase64Image(base64EncodedImageString)
    }

    @JavascriptInterface
    fun getInteractionType(): String {
        return jsInterfaceBridge?.getInteractionType() ?: ""
    }

    // test only
    @JavascriptInterface
    fun clearWebViewCache() {
        jsInterfaceBridge?.clearWebViewCache()
    }

    interface JSInterfaceBridge {

        fun getUserAuthToken(): String

        fun notifyGameRoundStarted()

        fun notifyGameRoundEnded()

        fun updateGamePrompts(prompts: String)

        fun clearGamePrompts()

        fun saveBase64Image(base64EncodedImageString: String)

        fun getInteractionType(): String

        // test only
        fun clearWebViewCache()
    }
}
