package io.qtalk.qgamejsinterfacetester.helpers

import android.content.Context

object PreferenceManager {

    private const val PREFERENCE_FILE_NAME = "QJSTesterPrefs"

    const val KEY_SELECTED_USER = "selected_user"

    fun getString(context: Context, key: String): String? {
        return getSharedPreferences(context).getString(key, null)
    }

    fun writeString(context: Context, key: String, value: String){
        getSharedPreferences(context).edit().putString(key, value).apply()
    }

    private fun getSharedPreferences(context: Context) =
        context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
}