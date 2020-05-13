package zyon.notifier.activity

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.chiralcode.colorpicker.ColorPicker
import kotlinx.android.synthetic.main.activity_settings.*
import zyon.notifier.R
import zyon.notifier.service.QuickAddService

class SettingsActivity : AppCompatActivity() {

    private var prefs: SharedPreferences? = null

    private var notiColor = ""
    private var qaShow = false
    private var qaColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
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

        notiColor = prefs!!.getString("NOTIFICATION_COLOR", "#3F51B5")!!
        qaShow = prefs!!.getBoolean("QUICK_ADD_USE", false)
        qaColor = prefs!!.getString("QUICK_ADD_COLOR", "#3F51B5")!!

    }

    // notification settings
    private fun setNoti() {

        val background: Drawable = settings_color_preview.background
        if(background is GradientDrawable) background.setColor(Color.parseColor(notiColor))

        // choose color
        settings_color_container.setOnClickListener {

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
                        editor.putString("NOTIFICATION_COLOR", notiColor)
                        editor.apply()

                        val background: Drawable = settings_color_preview.background
                        if(background is GradientDrawable) background.setColor(Color.parseColor(notiColor))

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

        val background: Drawable = settings_qa_color_preview.background
        if(background is GradientDrawable) background.setColor(Color.parseColor(qaColor))

        val qaIntent = Intent(this@SettingsActivity, QuickAddService::class.java)

        // activate
        settings_switch_qa.isChecked = qaShow
        settings_switch_qa.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) Toast.makeText(this, getString(R.string.alert_qa_en), Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, getString(R.string.alert_qa_dis), Toast.LENGTH_SHORT).show()

            if (isChecked) {
                qaShow = true

                val editor = prefs!!.edit()
                editor.putBoolean("QUICK_ADD_USE", qaShow)
                editor.apply()

            } else {
                qaShow = false

                val editor = prefs!!.edit()
                editor.putBoolean("QUICK_ADD_USE", qaShow)
                editor.apply()
            }

            if (isChecked) qaIntent.putExtra("use", true)
            else qaIntent.putExtra("use", false)
            startService(qaIntent)

        }

        // choose color
        settings_qa_color_container.setOnClickListener {

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
                        editor.putString("QUICK_ADD_COLOR", qaColor)
                        editor.apply()
                        qaIntent.putExtra("check", qaShow.toString() + "")
                        startService(qaIntent)

                        val background: Drawable = settings_qa_color_preview.background
                        if(background is GradientDrawable) background.setColor(Color.parseColor(qaColor))

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
        qaShow = false
        qaColor = "#3F51B5"

        val editor = prefs!!.edit()
        editor.putString("NOTIFICATION_COLOR", notiColor)
        editor.putBoolean("QUICK_ADD_USE", qaShow)
        editor.putString("QUICK_ADD_COLOR", qaColor)
        editor.apply()

        settings_switch_qa.isChecked = qaShow

        val notiColorBackground: Drawable = settings_color_preview.background
        if(notiColorBackground is GradientDrawable) notiColorBackground.setColor(Color.parseColor(notiColor))

        val qaColorBackground: Drawable = settings_qa_color_preview.background
        if(qaColorBackground is GradientDrawable) qaColorBackground.setColor(Color.parseColor(qaColor))

        val qaIntent = Intent(this@SettingsActivity, QuickAddService::class.java)
        qaIntent.putExtra("use", false)
        startService(qaIntent)

    }

}
