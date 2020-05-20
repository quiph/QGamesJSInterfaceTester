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
        // map requests accordingly
        val androidRequests = request
            ?.resources
            ?.mapNotNull {
                when (it) {
                    PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                        Manifest.permission.RECORD_AUDIO
                    }
                    PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                        Manifest.permission.CAMERA
                    }
                    else -> {
                        null
                    }
                }
            }
            ?: listOf()

        val hasAndroidPermissions = androidRequests.all {
            context.hasPermission(it)
        }

        if (hasAndroidPermissions) {
            // todo handle other permissions?
            webkitPermissionRequest?.grant(request?.resources)
        } else {
            // don't have android permissions ask.
            if (androidRequests.isNotEmpty()) {
                webChromeClientPermissionCallbacks?.onRequestAndroidPermissions(
                    androidRequests.toTypedArray()
                )
            }
        }
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        webChromeClientPermissionCallbacks?.onConsoleMessage(consoleMessage)
        return super.onConsoleMessage(consoleMessage)
    }

    /**
     * Call this when the corresponding component, Activity/ Fragment receives a permission callback.
     * */
    fun onPermissionRequestResult(permissions: Array<out String>, grantResults: IntArray) {
        // all permissions requested were granted?
        if (permissions.size == grantResults.size && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {

            // grant permissions to webkit.
            webkitPermissionRequest
                ?.grant(
                    permissions.mapNotNull {
                        when (it) {
                            Manifest.permission.RECORD_AUDIO -> {
                                PermissionRequest.RESOURCE_AUDIO_CAPTURE
                            }
                            Manifest.permission.CAMERA -> {
                                PermissionRequest.RESOURCE_VIDEO_CAPTURE
                            }
                            else -> {
                                null
                            }
                        }
                    }
                        .toTypedArray()
                )
        }
    }

    /**
     * An interface to bridge the permission request from the WebKit side to Android permission management system
     * */
    interface WebChromeClientPermissionCallbacks {
        /**
         * This method is called when the webkit thinks that the corresponding web page being loaded, needs a particular request.
         * */
        fun onRequestAndroidPermissions(requestedPermissions: Array<String>)

        /**
         * Notifies when a log is printed from js
         * */
        fun onConsoleMessage(consoleMessage: ConsoleMessage?)
    }
}
