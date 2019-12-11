@file:Suppress("unused")

package io.qtalk.qgamejsinterfacetester.helpers

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import java.nio.charset.Charset
import java.security.MessageDigest

fun ByteArray.toHexString(): String {
    val hexString = StringBuilder()

    for (i in indices) {
        val hex = Integer.toHexString(0xFF and this[i].toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }

    return hexString.toString()
}

fun String.generateSHA1(): String{
    return MessageDigest.getInstance("SHA-1")
        .digest(this.toByteArray(Charset.forName("UTF-8")))
        .toHexString()
}

fun String.generateMD5(): String{
    return MessageDigest.getInstance("MD5")
        .digest(this.toByteArray(Charset.forName("UTF-8")))
        .toHexString()
}

fun String?.formatUrl(): String {
    if (this.isNullOrEmpty()) throw IllegalAccessException("Need a valid url, current url passed is $this")

    // if loading local file, load it without adding http or http:// prefix
    if (startsWith("file://")) return this

    return if (!startsWith("https://") && !startsWith("http://")) {
        if (startsWith("192.168")) {
            // is local host, do not prefix with https
            "http://$this"
        } else {
            "https://$this"
        }
    } else {
        this
    }
}

fun Context.hasPermission(permission: String): Boolean {
    // if less than marshmallow, return without checking for permission.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true

    return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}