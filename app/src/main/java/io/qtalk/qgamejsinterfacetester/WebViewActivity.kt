package io.qtalk.qgamejsinterfacetester

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import io.qtalk.qgamejsinterfacetester.core.InteractionType

class WebViewActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_WEBSITE_URL = "extra-website-url"
        private const val EXTRA_INTERACTION_TYPE = "extra-interaction-type"
        private const val EXTRA_WRITE_PARTICIPANT_INFO = "extra-write-participant-info"

        fun startActivity(
            context: Context,
            url: String,
            interactionType: InteractionType = InteractionType.IN_CALL,
            shouldWriteParticipantInfo: Boolean = false
        ) {
            context.startActivity(
                Intent(
                    context,
                    WebViewActivity::class.java
                )
                    .putExtra(EXTRA_WEBSITE_URL, url)
                    .putExtra(EXTRA_INTERACTION_TYPE, interactionType.name)
                    .putExtra(EXTRA_WRITE_PARTICIPANT_INFO, shouldWriteParticipantInfo)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container, WebViewFragment.init(
                    intent.getStringExtra(EXTRA_WEBSITE_URL) ?: "www.google.com",
                    InteractionType.valueOf(
                        intent.getStringExtra(EXTRA_INTERACTION_TYPE)
                            ?: InteractionType.IN_CALL.name
                    ),
                    intent.getBooleanExtra(EXTRA_WRITE_PARTICIPANT_INFO, false)
                )
            )
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return super.onOptionsItemSelected(item)
    }
}
