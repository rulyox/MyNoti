package zyon.notifier.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window

import kotlinx.android.synthetic.main.dialog_add.*

import zyon.notifier.DBHelper
import zyon.notifier.MainActivity
import zyon.notifier.R
import zyon.notifier.service.NotiService

import com.chiralcode.colorpicker.ColorPickerDialog
import com.chiralcode.colorpicker.ColorPickerDialog.OnColorSelectedListener

class AddDialogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add)

        val intent = intent
        if (intent.getIntExtra("qa", -1) == 1) sendBroadcast(Intent("FINISH_ACTIVITY"))

        // preference
        val prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)
        var notificationNumber = prefs.getInt("notificationNumber", 1)
        var colorString = prefs.getString("notiColor", "#3F51B5")

        // database
        val db = DBHelper(this).writableDatabase

        // color preview
        color_preview_add.setBackgroundColor(Color.parseColor(colorString))

        // color dialog button
        color_choose_add.setOnClickListener {

            val initialColor = Color.parseColor(colorString)

            val colorPickerDialog = ColorPickerDialog(this@AddDialogActivity, initialColor, OnColorSelectedListener { color ->

                colorString = String.format("#%06X", 0xFFFFFF and color)
                color_preview_add.setBackgroundColor(Color.parseColor(colorString))

            })

            colorPickerDialog.show()

        }

        // add button
        button_add.setOnClickListener {

            val title = dialog_title.text.toString()
            val text = dialog_text.text.toString()

            // add to database
            db.execSQL("INSERT INTO " + MainActivity.TABLE_NAME + " VALUES ( " + notificationNumber + ", '" + title + "', '" + text + "', '" + colorString + "' );")

            // create notification
            val notifyIntent = Intent(this@AddDialogActivity, NotiService::class.java)
            notifyIntent.putExtra("id", notificationNumber.toString() + "")
            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("text", text)
            notifyIntent.putExtra("color", colorString)
            startService(notifyIntent)

            // notification counter
            notificationNumber++
            val editor = prefs.edit()
            editor.putInt("notificationNumber", notificationNumber)
            editor.apply()

            setResult(RESULT_OK, Intent())
            finish()

        }

        // cancel button
        button_cancel.setOnClickListener {

            finish()

        }

    }

}
