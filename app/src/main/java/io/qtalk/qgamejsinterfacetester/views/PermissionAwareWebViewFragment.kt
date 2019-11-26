package io.qtalk.qgamejsinterfacetester.views

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import io.qtalk.qgamejsinterfacetester.R
import io.qtalk.qgamejsinterfacetester.helpers.PermissionAwareWebChromeClient
import io.qtalk.qgamejsinterfacetester.helpers.formatUrl
import kotlinx.android.synthetic.main.fragment_permission_aware_webview.*

open class PermissionAwareWebViewFragment : Fragment(), PermissionAwareWebChromeClient.WebChromeClientPermissionCallbacks {

    companion object {

        @JvmStatic
        protected val ARG_URL_STRING = "arg-url"
        @JvmStatic
        protected val ARG_HEADERS = "headers"

        private const val RC_PERMISSION_REQUEST = 3454

        fun generateHeaderFromKeyValue(pair: Pair<String, String>) = "${pair.first}:${pair.second}"

        fun init(url: String, headers: Array<String>? = null): PermissionAwareWebViewFragment {
            return PermissionAwareWebViewFragment().apply {
                arguments = Bundle(1).also {
                    it.putString(ARG_URL_STRING, url)
                    it.putStringArray(ARG_HEADERS, headers)
                }
            }
        }
    }

    private var url: String? = null

    private val permissionAwareWebChromeClient: PermissionAwareWebChromeClient by lazy {
        PermissionAwareWebChromeClient(activity!!, this)
    }

    fun getLoadedUrl(): String? = url

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_permission_aware_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadWebView(arguments?.getString(ARG_URL_STRING).formatUrl(), arguments?.getStringArray(ARG_HEADERS))
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(url: String, headers: Array<String>?) {
        webView.webViewClient = InAppBrowser()
        webView.webChromeClient = permissionAwareWebChromeClient

        webView.settings.javaScriptEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        this.url = url

        webView.loadUrl(url, convertMapFromColonSepratedHeaders(headers))
    }

    protected fun getWebView(): WebView {
        return webView
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?) {
        // no-op
    }

    override fun onRequestAndroidPermissions(requestedPermissions: Array<String>) {
        requestPermissions(requestedPermissions, RC_PERMISSION_REQUEST)
    }

    private fun convertMapFromColonSepratedHeaders(colonSeparatedHeaders: Array<String>?): Map<String, String> {
        return colonSeparatedHeaders?.associate {
            val colonSeparatedValues = it.split(":")
            colonSeparatedValues.first() to colonSeparatedValues.last()
        } ?: emptyMap()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionAwareWebChromeClient.onPermissionRequestResult(permissions, grantResults)
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

    override fun onDetach() {
        permissionAwareWebChromeClient.setPermissionCallbacks(null)
        super.onDetach()
    }
}