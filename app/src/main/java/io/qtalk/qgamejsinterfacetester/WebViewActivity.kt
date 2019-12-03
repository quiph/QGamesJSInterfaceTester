
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
        fun startActivity(context: Context, url: String, interactionType: InteractionType = InteractionType.IN_CALL){
            context.startActivity(Intent(
                context,
                WebViewActivity::class.java
            )
                .putExtra(EXTRA_WEBSITE_URL, url)
                .putExtra(EXTRA_INTERACTION_TYPE, interactionType.name)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, WebViewFragment.init(
                intent.getStringExtra(EXTRA_WEBSITE_URL),
                InteractionType.valueOf(intent.getStringExtra(EXTRA_INTERACTION_TYPE))
            ))
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
