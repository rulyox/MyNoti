package zyon.notifier

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_set.*

import com.google.android.material.snackbar.Snackbar

import zyon.notifier.service.QuickAddService

import com.chiralcode.colorpicker.ColorPickerDialog
import com.chiralcode.colorpicker.ColorPickerDialog.OnColorSelectedListener

class SettingsActivity : AppCompatActivity() {

    private var prefs: SharedPreferences? = null

    private var notiColor = ""
    private var notiTimeShow = 0
    private var qaShow = 0
    private var qaColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        registerReceiver(finishActivity, IntentFilter("FINISH_ACTIVITY"))

        setPrefs()

        setNoti()

        setQA()

    }

    // menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_set, menu)

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.menu_reset -> {
                reset()
                return true
            }

        }

        return super.onOptionsItemSelected(item)

    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(finishActivity)

    }

    private val finishActivity: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finishAffinity()
        }
    }

    // preference settings
    private fun setPrefs() {

        prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)

        notiColor = prefs!!.getString("notiColor", "#3F51B5")!!
        notiTimeShow = prefs!!.getInt("notiTimeBoolean", 0)
        qaShow = prefs!!.getInt("quickaddBoolean", 0)
        qaColor = prefs!!.getString("quickaddColor", "#FF4081")!!

    }

    // notification settings
    private fun setNoti() {

        color_preview.setBackgroundColor(Color.parseColor(notiColor))

        // choose color
        color_choose.setOnClickListener {

            val initialColor = Color.parseColor(notiColor)
            val colorPickerDialog = ColorPickerDialog(this@SettingsActivity, initialColor, OnColorSelectedListener { color ->
                notiColor = String.format("#%06X", 0xFFFFFF and color)

                val editor = prefs!!.edit()
                editor.putString("notiColor", notiColor)
                editor.apply()

                color_preview!!.setBackgroundColor(Color.parseColor(notiColor))
            })
            colorPickerDialog.show()

        }

        // show time
        checkbox_showtime.isChecked = notiTimeShow != 0
        checkbox_showtime.setOnCheckedChangeListener { buttonView, isChecked ->

            notiTimeShow = if (isChecked) 1 else 0

            val editor = prefs!!.edit()
            editor.putInt("notiTimeBoolean", notiTimeShow)
            editor.apply()

        }

    }

    // quick add settings
    private fun setQA() {

        qa_color_preview.setBackgroundColor(Color.parseColor(qaColor))

        val qaIntent = Intent(this@SettingsActivity, QuickAddService::class.java)

        // activate
        switch_quickadd.isChecked = qaShow != 0
        switch_quickadd.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                Snackbar.make(findViewById(R.id.activity_set), getString(R.string.alert_qa_en), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            } else {
                Snackbar.make(findViewById(R.id.activity_set), getString(R.string.alert_qa_dis), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            }

            if (isChecked) {
                qaShow = 1

                val editor = prefs!!.edit()
                editor.putInt("quickaddBoolean", qaShow)
                editor.apply()

            } else {
                qaShow = 0

                val editor = prefs!!.edit()
                editor.putInt("quickaddBoolean", qaShow)
                editor.apply()
            }

            if (isChecked) qaIntent.putExtra("check", "1")
            else qaIntent.putExtra("check", "0")
            startService(qaIntent)

        }

        // choose color
        qa_color_choose.setOnClickListener {
            val initialColor = Color.parseColor(qaColor)
            val colorPickerDialog = ColorPickerDialog(this@SettingsActivity, initialColor, OnColorSelectedListener { color ->
                qaColor = String.format("#%06X", 0xFFFFFF and color)
                val editor = prefs!!.edit()
                editor.putString("quickaddColor", qaColor)
                editor.apply()
                qaIntent.putExtra("check", qaShow.toString() + "")
                startService(qaIntent)
                qa_color_preview!!.setBackgroundColor(Color.parseColor(qaColor))
            })
            colorPickerDialog.show()
        }

    }

    // reset settings to default
    private fun reset() {

        notiColor = "#3F51B5"
        notiTimeShow = 0
        qaShow = 0
        qaColor = "#FF4081"

        val editor = prefs!!.edit()
        editor.putString("notiColor", notiColor)
        editor.putInt("notiTimeBoolean", notiTimeShow)
        editor.putInt("quickaddBoolean", qaShow)
        editor.putString("quickaddColor", qaColor)
        editor.apply()

        color_preview!!.setBackgroundColor(Color.parseColor(notiColor))
        checkbox_showtime.isChecked = notiTimeShow != 0
        switch_quickadd.isChecked = qaShow != 0
        qa_color_preview!!.setBackgroundColor(Color.parseColor(qaColor))

        val qaIntent = Intent(this@SettingsActivity, QuickAddService::class.java)
        qaIntent.putExtra("check", "0")
        startService(qaIntent)

    }

}
