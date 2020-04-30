package zyon.notifier.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.chiralcode.colorpicker.ColorPicker
import kotlinx.android.synthetic.main.dialog_edit.*
import zyon.notifier.R
import zyon.notifier.notification.Database
import zyon.notifier.notification.Notification
import zyon.notifier.service.NotificationService

class EditDialogActivity : Activity() {

    private val db = Database(this)

    private var position: Int = -1
    private var notiId: Long = -1
    private var title = ""
    private var text = ""
    private var color = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_edit)

        loadIntentData()
        initUI()

    }

    private fun loadIntentData() {

        val intent = intent
        position = intent.getIntExtra("position", -1)
        notiId = intent.getLongExtra("id", -1)
        title = intent.getStringExtra("title") ?: ""
        text = intent.getStringExtra("text") ?: ""
        color = intent.getStringExtra("color") ?: ""

    }

    private fun initUI() {

        // set data from previous notification
        dialog_title.setText(title)
        dialog_text.setText(text)
        color_preview_add.setBackgroundColor(Color.parseColor(color))

        color_choose_add.setOnClickListener {

            // open color picker dialog
            val initialColor = Color.parseColor(color)

            val alertDialogBuilder = AlertDialog.Builder(this@EditDialogActivity, R.style.DialogTheme)

            val colorPickerView = ColorPicker(this)
            colorPickerView.color = initialColor
            alertDialogBuilder.setView(colorPickerView)

            val onClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {

                    DialogInterface.BUTTON_POSITIVE -> {

                        val selectedColor: Int = colorPickerView.color

                        color = String.format("#%06X", 0xFFFFFF and selectedColor)
                        color_preview_add.setBackgroundColor(Color.parseColor(color))

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
            db.update(notiId.toInt(), title, text, color)

            // create notification
            val notifyIntent = Intent(this@EditDialogActivity, NotificationService::class.java)
            notifyIntent.putExtra("id", notiId.toString())
            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("text", text)
            notifyIntent.putExtra("color", color)
            startService(notifyIntent)

            val newNotification = Notification(color, title, text)

            MainActivity.notificationAdapter.updateItem(position, newNotification)

            finish()

        }

        button_cancel.setOnClickListener { finish() }

    }

}
