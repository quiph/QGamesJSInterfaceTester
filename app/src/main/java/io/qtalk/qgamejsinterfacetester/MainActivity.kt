package io.qtalk.qgamejsinterfacetester

import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            MaterialDialog(this).title(text = "Enter URL")
                .cancelOnTouchOutside(true)
                .input { materialDialog, charSequence ->
                    if (charSequence.isNotEmpty() && Patterns.WEB_URL.matcher(charSequence).matches()){
                        materialDialog.dismiss()
                        WebViewActivity.startActivity(this, charSequence.toString())
                    }else if (charSequence.toString() == "test-url"){
                        materialDialog.dismiss()
                        WebViewActivity.startActivity(this, "file:///android_asset/test.html")
                    }
                    else{
                        Toast.makeText(this, "Invalid Url!", Toast.LENGTH_SHORT).show()
                    }
                }
                .show()
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
