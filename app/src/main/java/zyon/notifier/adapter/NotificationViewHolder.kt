package zyon.notifier.adapter

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import zyon.notifier.R
import zyon.notifier.activity.EditDialogActivity
import zyon.notifier.activity.MainActivity
import zyon.notifier.notification.DAO
import zyon.notifier.notification.Notification
import zyon.notifier.service.NotificationService

class NotificationViewHolder(view: View, adapter: NotificationAdapter): RecyclerView.ViewHolder(view) {

    companion object {
        const val DIALOG_CHOOSE_EDIT = 0
        const val DIALOG_CHOOSE_DELETE = 1
    }

    private val parent by lazy { view.findViewById(R.id.item_parent) as LinearLayout }
    val title by lazy { view.findViewById(R.id.item_title) as TextView }
    val text by lazy { view.findViewById(R.id.item_text) as TextView }
    val color by lazy { view.findViewById(R.id.item_color) as View }

    init {

        val context = view.context
        val dao = DAO(context)

        // click anywhere
        parent.setOnClickListener {

            val notificationList: ArrayList<Notification> = dao.getNotificationList()
            val notification: Notification = notificationList[adapterPosition]

            val alertDialogBuilder = AlertDialog.Builder(context, R.style.DialogTheme)
            alertDialogBuilder.setItems(arrayOf(context.getString(R.string.main_modify), context.getString(R.string.main_delete))) { dialog, id ->

                if (id == DIALOG_CHOOSE_EDIT) {

                        // start dialog activity
                        val editIntent = Intent(context, EditDialogActivity::class.java)
                        editIntent.putExtra("position", adapterPosition)
                        editIntent.putExtra("id", notification.id)
                        editIntent.putExtra("title", notification.title)
                        editIntent.putExtra("text", notification.text)
                        editIntent.putExtra("color", notification.color)
                        context.startActivity(editIntent)

                    } else if (id == DIALOG_CHOOSE_DELETE) {

                        // delete notification
                        val deleteIntent = Intent(context, NotificationService::class.java)
                        deleteIntent.putExtra("action", "remove")
                        deleteIntent.putExtra("id", notification.id)
                        context.startService(deleteIntent)

                        // delete database
                        dao.deleteNotification(notification.id)

                        // delete from list
                        NotificationAdapterObject.delete(adapterPosition)

                        // show empty text if empty
                        (context as MainActivity).setEmptyText()

                        Toast.makeText(context, context.getString(R.string.alert_deleted), Toast.LENGTH_SHORT).show()

                    }

            }

            alertDialogBuilder.create().show()

        }

    }

}
