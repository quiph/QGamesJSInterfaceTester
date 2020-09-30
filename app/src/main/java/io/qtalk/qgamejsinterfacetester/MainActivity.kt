package io.qtalk.qgamejsinterfacetester

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.SingleChoiceListener
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import io.qtalk.qgamejsinterfacetester.core.InteractionType
import io.qtalk.qgamejsinterfacetester.helpers.PreferenceManager
import io.qtalk.qgamejsinterfacetester.helpers.QTalkTestUsers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var interactionType: InteractionType = InteractionType.IN_CALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        consumeIntent()

        fab.setOnClickListener {
            openUrlSelectionDialog()
        }

        refreshSelectedUser()

        selectUserButton.setOnClickListener {
            MaterialDialog(this)
                .title(text = "Select User")
                .show {
                    listItems(
                        items = QTalkTestUsers.values().map { it.displayName }) { _, index, _ ->
                        PreferenceManager.writeString(
                            this@MainActivity,
                            PreferenceManager.KEY_SELECTED_USER,
                            QTalkTestUsers.values()[index].userName
                        )
                        refreshSelectedUser()
                        dismiss()
                    }
                }

        }
    }

    private fun getUrlFromPrefOrIntent(): String? {
        return if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.toString().also {
                intent.data = null
                intent.action = Intent.ACTION_MAIN
            }
        } else {
            PreferenceManager.getString(this, PreferenceManager.KEY_LAST_ENTERED_URL)
        }
    }

    private fun openUrlSelectionDialog() {
        MaterialDialog(this)
            .title(text = "Enter URL")
            .cancelOnTouchOutside(true)
            .negativeButton(text = "Test URL") {
                openTestUrl()
            }
            .listItemsSingleChoice(
                R.array.interactionTypes,
                initialSelection = 1,
                waitForPositiveButton = false,
                selection = object: SingleChoiceListener {
                    override fun invoke(dialog: MaterialDialog, index: Int, text: CharSequence) {
                        interactionType = when (index) {
                            0 -> InteractionType.WEB_SHARING
                            1 -> InteractionType.IN_CALL
                            2 -> InteractionType.WEBRTC
                            else -> InteractionType.IN_CALL
                        }
                    }
                }
            )
            .input(prefill = getUrlFromPrefOrIntent())
            .positiveButton { materialDialog ->

                val urlToOpen = materialDialog.getInputField().text.toString()
                if (urlToOpen.isNotEmpty() && Patterns.WEB_URL.matcher(urlToOpen).matches()) {
                    materialDialog.dismiss()
                    WebViewActivity.startActivity(
                        this,
                        urlToOpen,
                        interactionType = interactionType,
                        shouldWriteParticipantInfo = participantInfoCheckBox.isChecked
                    )
                } else if (urlToOpen == "test-url") {
                    materialDialog.dismiss()
                    openTestUrl()
                } else {
                    Toast.makeText(this, "Invalid Url!", Toast.LENGTH_SHORT).show()
                }
            }
            .onDismiss {
                // reset interaction type.
                interactionType = InteractionType.IN_CALL
            }
            .show()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d("MainActivity", "onNewIntent: ")
        consumeIntent()
    }

    private fun consumeIntent() {
        if (intent.action == Intent.ACTION_VIEW) {
            openUrlSelectionDialog()
        }
    }

    private fun openTestUrl() {
        val testUrl = "file:///android_asset/test.html"
        WebViewActivity.startActivity(this, testUrl)
    }

    @SuppressLint("SetTextI18n")
    private fun refreshSelectedUser() {
        // test user profiles.
        val selectedUser = PreferenceManager.getString(this, PreferenceManager.KEY_SELECTED_USER)
        if (selectedUser == null) {
            mainText.text = "No User selected will return a test token!"
        } else {
            mainText.text = "Current Selected user is: \"${
                QTalkTestUsers.values().firstOrNull { it.userName == selectedUser }?.displayName
            }\""
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
