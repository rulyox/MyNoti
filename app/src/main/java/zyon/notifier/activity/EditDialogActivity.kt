package zyon.notifier.activity

import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.dialog.*
import zyon.notifier.R
import zyon.notifier.notification.DAO
import zyon.notifier.notification.Notification

class EditDialogActivity: AbstractDialogActivity() {

    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog)

        dao = DAO(this)

        loadIntentData()
        initUI()

    }

    private fun loadIntentData() {

        val intent = intent

        position = intent.getIntExtra("position", 0)
        id = intent.getIntExtra("id", 0)
        title = intent.getStringExtra("title") ?: ""
        text = intent.getStringExtra("text") ?: ""
        color = intent.getStringExtra("color") ?: "#3F51B5"

    }

    override fun initUI() {

        initCommonUI()

        // load text
        dialog_top_title.text = resources.getString(R.string.main_modify)
        dialog_button_add.text = resources.getString(R.string.main_modify)

        // load data from previous notification
        dialog_title.setText(title)
        dialog_text.setText(text)

        dialog_button_add.setOnClickListener {

            title = dialog_title.text.toString()
            text = dialog_text.text.toString()

            // update database
            dao.updateNotification(id, title, text, color)

            val newNotification = Notification(id, title, text, color)
            MainActivity.updateAdapter(position, newNotification)

            createNotification()

            finish()

        }

    }

}
