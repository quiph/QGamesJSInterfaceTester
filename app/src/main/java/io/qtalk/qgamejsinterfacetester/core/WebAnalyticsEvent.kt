package io.qtalk.qgamejsinterfacetester.core

data class WebAnalyticsEvent (val eventName: String, val eventParameters: Map<String, String>) {
    companion object {
        const val KEY_APPLET = "Applet"
    }
}
