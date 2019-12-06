package zyon.notifier.dialog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window

import androidx.appcompat.app.AlertDialog

import kotlinx.android.synthetic.main.dialog_edit.*

import zyon.notifier.DBHelper
import zyon.notifier.MainActivity
import zyon.notifier.R
import zyon.notifier.service.NotiService

import com.chiralcode.colorpicker.ColorPicker

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

            // open color picker dialog
            val initialColor = Color.parseColor(colorString)

            val alertDialogBuilder = AlertDialog.Builder(this@EditDialogActivity, R.style.DialogTheme)

            val colorPickerView = ColorPicker(this)
            colorPickerView.color = initialColor
            alertDialogBuilder.setView(colorPickerView)

            val onClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {

                    DialogInterface.BUTTON_POSITIVE -> {

                        val selectedColor: Int = colorPickerView.color

                        colorString = String.format("#%06X", 0xFFFFFF and selectedColor)
                        color_preview_add.setBackgroundColor(Color.parseColor(colorString))

                    }

                    DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()

                }
            }
            alertDialogBuilder.setPositiveButton(this.getString(android.R.string.ok), onClickListener)
            alertDialogBuilder.setNegativeButton(this.getString(android.R.string.cancel), onClickListener)

            val colorPickerDialog = alertDialogBuilder.create()
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
