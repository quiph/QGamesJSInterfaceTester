package io.qtalk.qgamejsinterfacetester.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient

class PermissionAwareWebChromeClient(
    private val context: Context,
    private var webChromeClientPermissionCallbacks: WebChromeClientPermissionCallbacks?
) : WebChromeClient() {

    private var webkitPermissionRequest: PermissionRequest? = null

    fun setPermissionCallbacks(webChromeClientPermissionCallbacks: WebChromeClientPermissionCallbacks?) {
        this.webChromeClientPermissionCallbacks = webChromeClientPermissionCallbacks
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        webkitPermissionRequest = request

        // WebKit has it's own permission string, we match the incoming permissions from the WebKit side,
        // ask the respective permissions on Android.
        request?.resources?.forEach {
            when (it) {
                PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                    askForWebkitPermission(it, Manifest.permission.RECORD_AUDIO)
                }
                PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                    askForWebkitPermission(it, Manifest.permission.CAMERA)
                }
            }
        }
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        webChromeClientPermissionCallbacks?.onConsoleMessage(consoleMessage)
        return super.onConsoleMessage(consoleMessage)
    }

    private fun askForWebkitPermission(webkitPermission: String, androidPermission: String) {

        if (context.hasPermission(androidPermission)) {
            try {
                webkitPermissionRequest!!.grant(arrayOf(webkitPermission))
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        } else {
            webChromeClientPermissionCallbacks?.onRequestAndroidPermissions(
                arrayOf(
                    androidPermission
                )
            )
        }
    }

    /**
     * Call this when the corresponding component, Activity/ Fragment receives a permission callback.
     * */
    fun onPermissionRequestResult(permissions: Array<out String>, grantResults: IntArray) {
        permissions.forEachIndexed { index, s ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                when (s) {
                    Manifest.permission.RECORD_AUDIO -> {
                        webkitPermissionRequest?.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
                    }
                    Manifest.permission.CAMERA -> {
                        webkitPermissionRequest?.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                    }
                }
            }
        }
    }

    /**
     * An interface to bridge the permission request from the WebKit side to Android permission management system
     * */
    interface WebChromeClientPermissionCallbacks {
        /**
         * This method is called when the webkit thinks that the corresponding webpage being loaded, needs a particular request.
         * */
        fun onRequestAndroidPermissions(requestedPermissions: Array<String>)

        /**
         * Notifies when a log is printed from js
         * */
        fun onConsoleMessage(consoleMessage: ConsoleMessage?)
    }
}