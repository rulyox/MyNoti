package zyon.notifier.activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.chiralcode.colorpicker.ColorPicker
import kotlinx.android.synthetic.main.dialog_add.*
import zyon.notifier.R
import zyon.notifier.notification.Database
import zyon.notifier.service.NotificationService

class AddDialogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add)

        val intent = intent
        if(intent.getBooleanExtra("qa", false)) sendBroadcast(Intent("FINISH_ACTIVITY"))

        // preference
        val prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)
        var notificationNumber = prefs.getInt("NOTIFICATION_COUNT", 1)
        var colorString = prefs.getString("NOTIFICATION_COLOR", "#3F51B5")

        // database
        val db = Database(this)

        // color preview
        val background: Drawable = dialog_color_preview.background
        if(background is GradientDrawable) background.setColor(Color.parseColor(colorString))

        // color dialog button
        dialog_color_container.setOnClickListener {

            // open color picker dialog
            val initialColor = Color.parseColor(colorString)

            val alertDialogBuilder = AlertDialog.Builder(this@AddDialogActivity, R.style.DialogTheme)

            val colorPickerView = ColorPicker(this)
            colorPickerView.color = initialColor
            alertDialogBuilder.setView(colorPickerView)

            val onClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {

                    DialogInterface.BUTTON_POSITIVE -> {

                        val selectedColor: Int = colorPickerView.color

                        colorString = String.format("#%06X", 0xFFFFFF and selectedColor)

                        val background: Drawable = dialog_color_preview.background
                        if(background is GradientDrawable) background.setColor(Color.parseColor(colorString))

                    }

                    DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()

                }
            }
            alertDialogBuilder.setPositiveButton(this.getString(android.R.string.ok), onClickListener)
            alertDialogBuilder.setNegativeButton(this.getString(android.R.string.cancel), onClickListener)

            val colorPickerDialog = alertDialogBuilder.create()
            colorPickerDialog.show()

        }

        // add button
        dialog_button_add.setOnClickListener {

            val title = dialog_title.text.toString()
            val text = dialog_text.text.toString()

            // add to database
            db.insert(notificationNumber, title, text, colorString!!)

            // create notification
            val notifyIntent = Intent(this@AddDialogActivity, NotificationService::class.java)
            notifyIntent.putExtra("id", notificationNumber.toString() + "")
            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("text", text)
            notifyIntent.putExtra("color", colorString)
            startService(notifyIntent)

            // notification counter
            notificationNumber++
            val editor = prefs.edit()
            editor.putInt("NOTIFICATION_COUNT", notificationNumber)
            editor.apply()

            setResult(RESULT_OK, Intent())
            finish()

        }

        // cancel button
        dialog_button_cancel.setOnClickListener {

            finish()

        }

    }

}
