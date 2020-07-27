package zyon.notifier.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.dialog.*
import zyon.notifier.R
import zyon.notifier.notification.NotificationDAO
import zyon.notifier.notification.Notification

class AddDialogActivity: AbstractDialogActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog)

        loadPrefs()
        initUI()

    }

    private fun loadPrefs() {

        prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)

        id = prefs.getInt("NOTIFICATION_COUNT", 1)
        color = prefs.getString("NOTIFICATION_COLOR", "#3F51B5") ?: "#3F51B5"

    }

    override fun initUI() {

        initCommonUI()

        // load text
        dialog_top_title.text = resources.getString(R.string.dialog_title)
        dialog_button_add.text = resources.getString(R.string.dialog_add)

        // add button
        dialog_button_add.setOnClickListener {

            title = dialog_title.text.toString()
            text = dialog_text.text.toString()

            // save notification to database
            val newNotification = Notification(id, title, text, color)
            NotificationDAO.addNotification(newNotification)

            // increase id in preferences
            val editor = prefs.edit()
            editor.putInt("NOTIFICATION_COUNT", id + 1)
            editor.apply()

            createNotification()

            // close dialog
            setResult(RESULT_OK, Intent())
            finish()

        }

    }

}
