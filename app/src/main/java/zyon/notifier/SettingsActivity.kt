package zyon.notifier

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_set.*

import zyon.notifier.service.QuickAddService

import com.chiralcode.colorpicker.ColorPicker

class SettingsActivity : AppCompatActivity() {

    private var prefs: SharedPreferences? = null

    private var notiColor = ""
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
        qaShow = prefs!!.getInt("quickaddBoolean", 0)
        qaColor = prefs!!.getString("quickaddColor", "#FF4081")!!

    }

    // notification settings
    private fun setNoti() {

        color_preview.setBackgroundColor(Color.parseColor(notiColor))

        // choose color
        color_choose.setOnClickListener {

            // open color picker dialog
            val initialColor = Color.parseColor(notiColor)

            val alertDialogBuilder = AlertDialog.Builder(this@SettingsActivity, R.style.DialogTheme)

            val colorPickerView = ColorPicker(this)
            colorPickerView.color = initialColor
            alertDialogBuilder.setView(colorPickerView)

            val onClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {

                    DialogInterface.BUTTON_POSITIVE -> {

                        val selectedColor: Int = colorPickerView.color

                        notiColor = String.format("#%06X", 0xFFFFFF and selectedColor)
                        val editor = prefs!!.edit()
                        editor.putString("notiColor", notiColor)
                        editor.apply()
                        color_preview!!.setBackgroundColor(Color.parseColor(notiColor))

                    }

                    DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()

                }
            }
            alertDialogBuilder.setPositiveButton(this.getString(android.R.string.ok), onClickListener)
            alertDialogBuilder.setNegativeButton(this.getString(android.R.string.cancel), onClickListener)

            val colorPickerDialog = alertDialogBuilder.create()
            colorPickerDialog.show()

        }

    }

    // quick add settings
    private fun setQA() {

        qa_color_preview.setBackgroundColor(Color.parseColor(qaColor))

        val qaIntent = Intent(this@SettingsActivity, QuickAddService::class.java)

        // activate
        switch_quickadd.isChecked = qaShow != 0
        switch_quickadd.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) Toast.makeText(this, getString(R.string.alert_qa_en), Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, getString(R.string.alert_qa_dis), Toast.LENGTH_SHORT).show()

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

            // open color picker dialog
            val initialColor = Color.parseColor(qaColor)

            val alertDialogBuilder = AlertDialog.Builder(this@SettingsActivity, R.style.DialogTheme)

            val colorPickerView = ColorPicker(this)
            colorPickerView.color = initialColor
            alertDialogBuilder.setView(colorPickerView)

            val onClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {

                    DialogInterface.BUTTON_POSITIVE -> {

                        val selectedColor: Int = colorPickerView.color

                        qaColor = String.format("#%06X", 0xFFFFFF and selectedColor)
                        val editor = prefs!!.edit()
                        editor.putString("quickaddColor", qaColor)
                        editor.apply()
                        qaIntent.putExtra("check", qaShow.toString() + "")
                        startService(qaIntent)
                        qa_color_preview!!.setBackgroundColor(Color.parseColor(qaColor))

                    }

                    DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()

                }
            }
            alertDialogBuilder.setPositiveButton(this.getString(android.R.string.ok), onClickListener)
            alertDialogBuilder.setNegativeButton(this.getString(android.R.string.cancel), onClickListener)

            val colorPickerDialog = alertDialogBuilder.create()
            colorPickerDialog.show()

        }

    }

    // reset settings to default
    private fun reset() {

        notiColor = "#3F51B5"
        qaShow = 0
        qaColor = "#FF4081"

        val editor = prefs!!.edit()
        editor.putString("notiColor", notiColor)
        editor.putInt("quickaddBoolean", qaShow)
        editor.putString("quickaddColor", qaColor)
        editor.apply()

        color_preview!!.setBackgroundColor(Color.parseColor(notiColor))
        switch_quickadd.isChecked = qaShow != 0
        qa_color_preview!!.setBackgroundColor(Color.parseColor(qaColor))

        val qaIntent = Intent(this@SettingsActivity, QuickAddService::class.java)
        qaIntent.putExtra("check", "0")
        startService(qaIntent)

    }

}
