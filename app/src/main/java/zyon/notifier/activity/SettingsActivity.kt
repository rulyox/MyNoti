package zyon.notifier.activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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

class SettingsActivity: AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private var notificationColor = ""
    private var qaUse = false
    private var qaColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        loadPrefs()
        initNotificationUI()
        initQAUI()

    }

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
                resetSettings()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadPrefs() {

        prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)

        notificationColor = prefs.getString("NOTIFICATION_COLOR", "#3F51B5")!!
        qaUse = prefs.getBoolean("QUICK_ADD_USE", false)
        qaColor = prefs.getString("QUICK_ADD_COLOR", "#3F51B5")!!

    }

    private fun initNotificationUI() {

        // color preview
        val previewBackground = settings_color_preview.background as GradientDrawable
        previewBackground.setColor(Color.parseColor(notificationColor))

        // color picker
        settings_color_container.setOnClickListener {

            val initialColor = Color.parseColor(notificationColor)

            val alertDialogBuilder = AlertDialog.Builder(this@SettingsActivity, R.style.DialogTheme)

            val colorPickerView = ColorPicker(this)
            colorPickerView.color = initialColor
            alertDialogBuilder.setView(colorPickerView)

            val onClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {

                    DialogInterface.BUTTON_POSITIVE -> {

                        val selectedColor: Int = colorPickerView.color

                        notificationColor = String.format("#%06X", 0xFFFFFF and selectedColor)

                        val editor = prefs.edit()
                        editor.putString("NOTIFICATION_COLOR", notificationColor)
                        editor.apply()

                        previewBackground.setColor(Color.parseColor(notificationColor))

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

    private fun initQAUI() {

        // color preview
        val previewBackground = settings_qa_color_preview.background as GradientDrawable
        previewBackground.setColor(Color.parseColor(qaColor))

        val qaIntent = Intent(this@SettingsActivity, QuickAddService::class.java)

        // switch
        settings_switch_qa.isChecked = qaUse
        settings_switch_qa.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) Toast.makeText(this, getString(R.string.alert_qa_en), Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, getString(R.string.alert_qa_dis), Toast.LENGTH_SHORT).show()

            if (isChecked) {
                qaUse = true

                val editor = prefs.edit()
                editor.putBoolean("QUICK_ADD_USE", qaUse)
                editor.apply()

            } else {
                qaUse = false

                val editor = prefs.edit()
                editor.putBoolean("QUICK_ADD_USE", qaUse)
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

                        val editor = prefs.edit()
                        editor.putString("QUICK_ADD_COLOR", qaColor)
                        editor.apply()
                        qaIntent.putExtra("check", qaUse.toString() + "")
                        startService(qaIntent)

                        previewBackground.setColor(Color.parseColor(qaColor))

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

    private fun resetSettings() {

        notificationColor = "#3F51B5"
        qaUse = false
        qaColor = "#3F51B5"

        // reset prefs
        val editor = prefs.edit()
        editor.putString("NOTIFICATION_COLOR", notificationColor)
        editor.putBoolean("QUICK_ADD_USE", qaUse)
        editor.putString("QUICK_ADD_COLOR", qaColor)
        editor.apply()

        // reset switch
        settings_switch_qa.isChecked = qaUse

        // reset colors
        val notificationPreviewBackground = settings_color_preview.background as GradientDrawable
        notificationPreviewBackground.setColor(Color.parseColor(notificationColor))

        val qaPreviewBackground = settings_qa_color_preview.background as GradientDrawable
        qaPreviewBackground.setColor(Color.parseColor(qaColor))

        // turn off quick add
        val qaIntent = Intent(this@SettingsActivity, QuickAddService::class.java)
        qaIntent.putExtra("use", false)
        startService(qaIntent)

    }

}
