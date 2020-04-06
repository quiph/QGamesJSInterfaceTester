package io.qtalk.qgamejsinterfacetester

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import io.qtalk.qgamejsinterfacetester.core.InteractionType
import io.qtalk.qgamejsinterfacetester.core.JSInterface
import io.qtalk.qgamejsinterfacetester.helpers.PermissionAwareWebChromeClient
import io.qtalk.qgamejsinterfacetester.views.PermissionAwareWebViewFragment
import kotlinx.android.synthetic.main.webview_homepage.*
import kotlinx.android.synthetic.main.webview_homepage.clearLog
import kotlinx.android.synthetic.main.webview_homepage.webView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URI

/**
 * A simple [Fragment] subclass.
 */
class HomePageTest : PermissionAwareWebViewFragment(), JSInterface.JSInterfaceBridge {

    companion object {
        private const val JS_INTERFACE_OBJECT_NAME = "QTalkApp"
        const val DEFAULT_HOMEPAGE_URL = "file:///android_asset/homepage.html"
        val urlList = listOf(
            "https://www.youtube.com/",
            "https://www.amazon.in/",
            "https://www.quora.com/",
            "https://www.myntra.com/",
            "https://www.instagram.com/",
            "https://twitter.com/"
        )
        private const val ARG_URL_STRING = "arg-url"
        private const val ARG_INTERACTION_TYPE = "arg-interaction-type"

        fun init(
            url: String,
            interactionType: InteractionType = InteractionType.IN_CALL
        ): HomePageTest {
            return HomePageTest().apply {
                arguments = Bundle(1).also {
                    it.putString(ARG_URL_STRING, url)
                    it.putString(ARG_INTERACTION_TYPE, interactionType.name)
                }
            }
        }
    }

    private val permissionAwareWebChromeClient: PermissionAwareWebChromeClient by lazy {
        PermissionAwareWebChromeClient(activity!!, this)
    }

    private var mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var jsInterface: JSInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.webview_homepage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
        webView.loadUrl(DEFAULT_HOMEPAGE_URL)

        clearUrl.setOnClickListener {
            urlText.text.clear()
        }
        clearLog.setOnClickListener{
            logTextHome.text = ""
        }
        btnSubmit.setOnClickListener{
            if(urlText.text.isNotEmpty()) {
                webView.loadUrl(urlText.text.toString())
            }
        }
    }

    private val webViewClient by lazy {
        object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadFavIcons(urlList)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.databaseEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setAppCacheEnabled(false)
        webView.clearCache(true)
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.isVerticalScrollBarEnabled = true
        webView.webViewClient = webViewClient
        webView.webChromeClient = permissionAwareWebChromeClient
        jsInterface = JSInterface(this)
        webView.addJavascriptInterface(jsInterface, JS_INTERFACE_OBJECT_NAME)
    }

    override fun getUserAuthToken(): String {
        TODO("Not yet implemented")
    }

    override fun notifyGameRoundStarted() {
        TODO("Not yet implemented")
    }

    override fun notifyGameRoundEnded() {
        TODO("Not yet implemented")
    }

    override fun updateGamePrompts(prompts: String) {
        TODO("Not yet implemented")
    }

    override fun clearGamePrompts() {
        TODO("Not yet implemented")
    }

    override fun saveBase64Image(base64EncodedImageString: String) {
        TODO("Not yet implemented")
    }

    override fun getInteractionType(): String {
        TODO("Not yet implemented")
    }

    override fun pushAnalyticsEvent(eventJson: String) {
        TODO("Not yet implemented")
    }

    override fun getUrlList(): String{
        return Gson().toJson(urlList)
    }

    override fun clearWebViewCache() {
        TODO("Not yet implemented")
    }

    private fun loadFavIcons(urlList: List<String>) {
        mainScope.launch(Dispatchers.IO) {
            for (url in urlList) {
                Glide.with(this@HomePageTest).asBitmap().load(getFavIconURL(url)).also {
                    Log.d("HomePageTest", "onPageFinished: $url")
                    it.into(object: CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val obj = IconProps(url, bitmapToBase64(resource))
                            val gson = Gson().toJson(obj)
                            Log.d("HomePageTest", "onResourceReady: $gson")
                            launch(Dispatchers.Main) {
                                webView.evaluateJavascript("javascript:onFaviconLoaded($gson)",null)
                            }
                        }
                    })
                }
            }
        }
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?) {
        super.onConsoleMessage(consoleMessage)
        consoleMessage?.apply {
            logTextHome.append("${message()} ------ ${sourceId()}:${lineNumber()}\n")
            logsScrollViewHome.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap) : String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val byteArrayImage = baos.toByteArray()
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
    }

    private fun getFavIconURL(url: String?): String {
        val uri = URI(url)
        val domain = uri.host.toString()
        return ( "https://staging.remote.qtalk.io/icons/icon?url=$domain&size=32..96..200")
    }

    data class IconProps(val url: String, val faviconBase64: String)
}
