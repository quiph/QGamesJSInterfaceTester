package io.qtalk.qgamejsinterfacetester

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.google.gson.Gson
import io.qtalk.qgamejsinterfacetester.core.InteractionType
import io.qtalk.qgamejsinterfacetester.core.JSInterface
import io.qtalk.qgamejsinterfacetester.core.WebAnalyticsEvent
import io.qtalk.qgamejsinterfacetester.helpers.PreferenceManager
import io.qtalk.qgamejsinterfacetester.helpers.QTalkTestUsers
import io.qtalk.qgamejsinterfacetester.helpers.generateSHA1
import io.qtalk.qgamejsinterfacetester.views.PermissionAwareWebViewFragment
import kotlinx.android.synthetic.main.webview_fragment.*

class WebViewFragment : PermissionAwareWebViewFragment(), JSInterface.JSInterfaceBridge {

    companion object {
        private const val JS_INTERFACE_OBJECT_NAME = "QTalkApp"

        private const val TEST_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                    ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ" +
                    ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

        private const val ARG_URL_STRING = "arg-url"
        private const val ARG_INTERACTION_TYPE = "arg-interaction-type"
        private const val ARG_WRITE_PARTICIPANT_INFO = "arg-write-participant-info"

        private const val RTDB_CHILD_CALL_DETAILS = "clDts"
        private const val RTDB_CHILD_PARTICIPANTS = "prtcpnts"
        private const val RTDB_VALUE_CALL_ENDED_AT = "clEdAt"
        private const val RTDB_PARTICIPANTS_STATE = "stat"
        private const val RTDB_PARTICIPANTS_STATE_AUDIO_ROUTE = "audRt"
        private const val RTDB_PARTICIPANTS_STATE_MUTED = "mut"
        private const val RTDB_PARTICIPANTS_STATE_PRESENCE = "prsnc"
        private const val RTDB_PARTICIPANT_PRESENCE_EXITED = "EXITED"

        private const val RTDB_PARTICIPANT_PRESENCE_AVAILABLE = "AVAILABLE"

        private const val AUDIO_ROUTE_VALUE_SPEAKER = "ROUTE_SPEAKER"
        private const val AUDIO_ROUTE_VALUE_EARPIECE = "ROUTE_EARPIECE"

        fun init(
            url: String,
            interactionType: InteractionType = InteractionType.IN_CALL,
            shouldWriteParticipantInfo: Boolean = false
        ): WebViewFragment {
            return WebViewFragment().apply {
                arguments = Bundle(1).also {
                    it.putString(ARG_URL_STRING, url)
                    it.putString(ARG_INTERACTION_TYPE, interactionType.name)
                    it.putBoolean(ARG_WRITE_PARTICIPANT_INFO, shouldWriteParticipantInfo)
                }
            }
        }
    }

    private var isTestUrl = false

    private lateinit var interactionType: InteractionType

    private lateinit var jsInterface: JSInterface

    private lateinit var rtdbReference: DatabaseReference

    private var shouldWriteParticipantInfo: Boolean = false

    private val gson by lazy {
        Gson()
    }

    private data class TestUserObject(
        val tstId: String,
        val iPrv: Boolean = true,
        val mod: String = "OUTGOING_VOIP"
    )

    @IgnoreExtraProperties
    private data class RTDBPreviewCallDetails(
        @get:PropertyName("rtcClId")
        @set:PropertyName("rtcClId")
        var rtcCallId: String,
        @get:PropertyName("diAt")
        @set:PropertyName("diAt")
        var dialedAt: Long
    )

    private fun writeParticipantInfoAndCallDetailsToRTDB() {

        // while writing the information to RTDB, we create the key as the user id
        val selectedUser = getSelectedTestUser()?.userIdRemote
            ?: run {
                Toast.makeText(activity!!, "No user selected!", Toast.LENGTH_SHORT).show()
                return
            }

        val loadedUrl = getLoadedUrl()!!

        val callId = Uri.parse(loadedUrl).getQueryParameter("id") ?: run {
            Toast.makeText(activity!!, "No Call ID provided, check entered URL!", Toast.LENGTH_LONG)
                .show()
            return
        }

        Log.d(
            "WebViewFragment",
            "writeParticipantInfoAndCallDetailsToRTDB: ${callId}, $selectedUser"
        )

        val database = FirebaseDatabase.getInstance()

        // get reference to clDts/prtcpnts which is where the participants are store in by the main QTalk app.
        val ref = database.getReference("qtalkDebugAndStaging/calls").child(callId)

        rtdbReference = ref

        // store the participant info with the key as the user id
        ref.child(RTDB_CHILD_PARTICIPANTS).child(selectedUser)
            .setValue(TestUserObject(selectedUser))

        // push dummy state
        getParticipantStateReference().apply {
            child(RTDB_PARTICIPANTS_STATE_AUDIO_ROUTE).setValue(AUDIO_ROUTE_VALUE_SPEAKER)
            child(RTDB_PARTICIPANTS_STATE_PRESENCE).setValue(RTDB_PARTICIPANT_PRESENCE_AVAILABLE)
            child(RTDB_PARTICIPANTS_STATE_MUTED).setValue(0)
        }

        // store call details
        ref.child(RTDB_CHILD_CALL_DETAILS).setValue(
            RTDBPreviewCallDetails(
                callId,
                System.currentTimeMillis()
            )
        )

    }

    private fun getSelectedTestUser(): QTalkTestUsers? {
        return QTalkTestUsers
            .values()
            .firstOrNull {
                it.userName == PreferenceManager.getString(
                    activity!!,
                    PreferenceManager.KEY_SELECTED_USER
                )
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.webview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadedUrl = getLoadedUrl()!!

        isTestUrl = loadedUrl.startsWith("file:///")

        if (!isTestUrl) {
            PreferenceManager.writeString(
                activity!!,
                PreferenceManager.KEY_LAST_ENTERED_URL,
                loadedUrl
            )
        }

        interactionType = InteractionType.valueOf(
            (arguments ?: return).getString(
                ARG_INTERACTION_TYPE,
                InteractionType.IN_CALL.name
            )
        )

        shouldWriteParticipantInfo = arguments?.getBoolean(ARG_WRITE_PARTICIPANT_INFO, false) ?: false



        Log.d("WebViewFragment", "onViewCreated: interaction type: $interactionType")

        if (interactionType == InteractionType.WEB_SHARING) {
            // write to QTalks RTDB here as participant information needs to be added in the array.
            writeParticipantInfoAndCallDetailsToRTDB()

            webShareCallButtons.visibility = View.VISIBLE

            val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.isSpeakerphoneOn = true
            audioManager.isMicrophoneMute = false

            audioStateButton.isSelected = true
            muteCallButton.isSelected = false

            endCallButton.setOnClickListener {
                rtdbReference
                    .child(RTDB_CHILD_CALL_DETAILS)
                    .child(RTDB_VALUE_CALL_ENDED_AT)
                    .setValue(System.currentTimeMillis())

                getParticipantStateReference()
                    .child(RTDB_PARTICIPANTS_STATE_PRESENCE)
                    .setValue(RTDB_PARTICIPANT_PRESENCE_EXITED)

                activity?.finish()
            }

            audioStateButton.setOnClickListener {
                audioManager.isSpeakerphoneOn = !audioManager.isSpeakerphoneOn
                audioStateButton.isSelected = audioManager.isSpeakerphoneOn
                getParticipantStateReference()
                    .child(RTDB_PARTICIPANTS_STATE_AUDIO_ROUTE)
                    .setValue(
                        if (audioManager.isSpeakerphoneOn) {
                            AUDIO_ROUTE_VALUE_SPEAKER
                        } else {
                            AUDIO_ROUTE_VALUE_EARPIECE
                        }
                    )
            }

            muteCallButton.setOnClickListener {
                audioManager.isMicrophoneMute = !audioManager.isMicrophoneMute
                muteCallButton.isSelected = audioManager.isMicrophoneMute
                getParticipantStateReference()
                    .child(RTDB_PARTICIPANTS_STATE_MUTED)
                    .setValue(
                        if (audioManager.isMicrophoneMute)
                            1
                        else
                            0
                    )
            }
        } else {
            if (shouldWriteParticipantInfo) {
                writeParticipantInfoAndCallDetailsToRTDB()
            }
        }

        jsInterface = JSInterface(this)

        webView.addJavascriptInterface(jsInterface, JS_INTERFACE_OBJECT_NAME)

        // enables remote debugging capabilities via chrome dev tools
        // ref: https://developers.google.com/web/tools/chrome-devtools/remote-debugging/webviews
        WebView.setWebContentsDebuggingEnabled(true)

        webView.settings.mediaPlaybackRequiresUserGesture = false

        clearLog.setOnClickListener {
            logText.text = ""
        }
    }

    private fun getParticipantStateReference(): DatabaseReference {
        return rtdbReference
            .child(RTDB_CHILD_PARTICIPANTS)
            .child(getSelectedTestUser()?.userIdRemote!!)
            .child(RTDB_PARTICIPANTS_STATE)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?) {
        super.onConsoleMessage(consoleMessage)
        consoleMessage?.apply {
            logText.append("${message()} ------ ${sourceId()}:${lineNumber()}\n")
            logsScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        jsInterface.setJSInterfaceBridge(null)
        super.onDestroyView()
    }


    override fun getInteractionType(): String {
        return interactionType.name
    }

    override fun getUserAuthToken(): String {
        return getSelectedUserFromPref()?.generateSHA1() ?: TEST_TOKEN
    }

    private fun getSelectedUserFromPref() =
        PreferenceManager.getString(activity!!, PreferenceManager.KEY_SELECTED_USER)

    override fun notifyGameRoundStarted() {
        Toast.makeText(context, "Game round started notified!", Toast.LENGTH_SHORT).show()
    }

    override fun notifyGameRoundEnded() {
        Toast.makeText(context, "Game round ended notified!", Toast.LENGTH_SHORT).show()
    }

    override fun updateGamePrompts(prompts: String) {
        if (isTestUrl) {
            webView.handler.post {
                webView.evaluateJavascript("setPromptToText($prompts)") {
                    Log.d(
                        "JSInterface",
                        "onReceiveValue"
                    )
                }
            }
        } else {
            Toast.makeText(context, "Prompts recieved: $prompts", Toast.LENGTH_LONG).show()
        }
    }

    override fun pushAnalyticsEvent(eventJson: String) {
        Log.d("WebViewFragment", "pushAnalyticsEvent: $eventJson")
        val analyticsEvent =
            gson.fromJson<WebAnalyticsEvent>(eventJson, WebAnalyticsEvent::class.java)

        if (analyticsEvent.eventParameters.containsKey(WebAnalyticsEvent.KEY_APPLET)) {
            Toast.makeText(
                context,
                """
                |Analytics Event Pushed:
                |Event name: ${analyticsEvent.eventName}
                |Params: 
                |${analyticsEvent
                    .eventParameters
                    .entries
                    .joinToString(separator = "\n\t", prefix = "\t") {
                        "${it.key}:${it.value}"
                    }
                }
            |""".trimMargin(),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(context, "[Error] Applet key not provided!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun clearGamePrompts() {
        if (isTestUrl) {
            webView.handler.post {
                webView.evaluateJavascript("setPromptToText(\"\")") {
                    Log.d(
                        "JSInterface",
                        "onReceiveValue"
                    )
                }
            }
        } else {
            Toast.makeText(context, "Prompts cleared", Toast.LENGTH_LONG).show()
        }
    }

    override fun saveBase64Image(base64EncodedImageString: String) {
        Log.d("JSInterfaceBridge", "onBase64ImageSaved() called $base64EncodedImageString")
    }

    // test only
    override fun clearWebViewCache() {
        Log.d("JSInterfaceBridge", "clearWebViewCache: ")
        webView.handler.post {
            Toast.makeText(context, "Cache cleared!", Toast.LENGTH_LONG).show()
            webView.clearCache(true)
        }
    }
}