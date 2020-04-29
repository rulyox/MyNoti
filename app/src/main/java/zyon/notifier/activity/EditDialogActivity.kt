package zyon.notifier.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window

import androidx.appcompat.app.AlertDialog

import kotlinx.android.synthetic.main.dialog_edit.*

import zyon.notifier.notification.Database
import zyon.notifier.R
import zyon.notifier.service.NotificationService

import com.chiralcode.colorpicker.ColorPicker

class EditDialogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_edit)

        // database
        val db = Database(this)
        val intent = intent
        val notiId = intent.getLongExtra("id", -1)
        var title = intent.getStringExtra("title")
        var text = intent.getStringExtra("text")
        val color = intent.getStringExtra("color")
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
            db.update(notiId.toInt(), title!!, text!!, colorString!!)

            // create notification
            val notifyIntent = Intent(this@EditDialogActivity, NotificationService::class.java)
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
