package zyon.notifier.dialog

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window

import kotlinx.android.synthetic.main.dialog_edit.*

import zyon.notifier.DBHelper
import zyon.notifier.MainActivity
import zyon.notifier.R
import zyon.notifier.service.NotiService

import com.chiralcode.colorpicker.ColorPickerDialog
import com.chiralcode.colorpicker.ColorPickerDialog.OnColorSelectedListener

class EditDialogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_edit)

        // database
        val db = DBHelper(this).writableDatabase
        val intent = intent
        var title = intent.getStringExtra("title")
        var text = intent.getStringExtra("text")
        val color = intent.getStringExtra("color")
        val notiId = intent.getLongExtra("noti_id", -1)
        var colorString = color

        color_preview_add.setBackgroundColor(Color.parseColor(colorString))

        dialog_title.setText(title)
        dialog_text.setText(text)

        color_choose_add.setOnClickListener {

            val initialColor = Color.parseColor(colorString)
            val colorPickerDialog = ColorPickerDialog(this@EditDialogActivity, initialColor, OnColorSelectedListener { color ->
                colorString = String.format("#%06X", 0xFFFFFF and color)
                color_preview_add.setBackgroundColor(Color.parseColor(colorString))
            })
            colorPickerDialog.show()

        }

        button_add.setOnClickListener {

            title = dialog_title.text.toString()
            text = dialog_text.text.toString()

            // update database
            db.execSQL("DELETE FROM " + MainActivity.TABLE_NAME + " WHERE _id = " + notiId + ";")
            db.execSQL("INSERT INTO " + MainActivity.TABLE_NAME + " VALUES ( " + notiId + ", '" + title + "', '" + text + "', '" + colorString + "' );")

            // create notification
            val notifyIntent = Intent(this@EditDialogActivity, NotiService::class.java)
            notifyIntent.putExtra("id", notiId.toString())
            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("text", text)
            notifyIntent.putExtra("color", colorString)
            startService(notifyIntent)

            setResult(RESULT_OK, Intent())
            finish()

        }

        button_cancel.setOnClickListener { finish() }

    }

}
