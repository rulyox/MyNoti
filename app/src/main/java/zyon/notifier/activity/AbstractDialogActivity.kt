package zyon.notifier.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AlertDialog
import com.chiralcode.colorpicker.ColorPicker
import kotlinx.android.synthetic.main.dialog.*
import zyon.notifier.R
import zyon.notifier.notification.NotificationDAO
import zyon.notifier.service.NotificationService

abstract class AbstractDialogActivity: Activity() {

    var id = 0
    var title = ""
    var text = ""
    var color = ""
    lateinit var dao: NotificationDAO

    abstract fun initUI()

    fun initCommonUI() {

        // color preview
        val previewBackground = dialog_color_preview.background as GradientDrawable
        previewBackground.setColor(Color.parseColor(color))

        // color picker
        dialog_color_container.setOnClickListener {

            val initialColor = Color.parseColor(color)

            val alertDialogBuilder = AlertDialog.Builder(this, R.style.DialogTheme)

            val colorPickerView = ColorPicker(this)
            colorPickerView.color = initialColor
            alertDialogBuilder.setView(colorPickerView)

            val onClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {

                    DialogInterface.BUTTON_POSITIVE -> {

                        val selectedColor: Int = colorPickerView.color

                        color = String.format("#%06X", 0xFFFFFF and selectedColor)

                        previewBackground.setColor(Color.parseColor(color))

                    }

                    DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()

                }
            }
            alertDialogBuilder.setPositiveButton(this.getString(android.R.string.ok), onClickListener)
            alertDialogBuilder.setNegativeButton(this.getString(android.R.string.cancel), onClickListener)

            val colorPickerDialog = alertDialogBuilder.create()
            colorPickerDialog.show()

        }

        // cancel button
        dialog_button_cancel.setOnClickListener { finish() }

    }

    fun createNotification() {

        val notifyIntent = Intent(this, NotificationService::class.java)
        notifyIntent.putExtra("action", "create")
        notifyIntent.putExtra("id", id)
        notifyIntent.putExtra("title", title)
        notifyIntent.putExtra("text", text)
        notifyIntent.putExtra("color", color)
        startService(notifyIntent)

    }

}
