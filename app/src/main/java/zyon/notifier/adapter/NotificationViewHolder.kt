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
import zyon.notifier.notification.Database
import zyon.notifier.service.NotificationService

class NotificationViewHolder(view: View, adapter: NotificationAdapter): RecyclerView.ViewHolder(view) {

    private val parent by lazy { view.findViewById(R.id.list_main) as LinearLayout }
    val color by lazy { view.findViewById(R.id.list_main_color) as View }
    val title by lazy { view.findViewById(R.id.list_main_title) as TextView }
    val text by lazy { view.findViewById(R.id.list_main_text) as TextView }

    init {

        val context = view.context

        val db = Database(context)

        // click anywhere
        parent.setOnClickListener {

            val cursor = db.selectAll()

            cursor.moveToFirst()

            for (i in 0 until adapterPosition) cursor.moveToNext()
            val notiId = cursor.getLong(cursor.getColumnIndex("id"))
            val title = cursor.getString(cursor.getColumnIndex("title"))
            val text = cursor.getString(cursor.getColumnIndex("text"))
            val color = cursor.getString(cursor.getColumnIndex("color"))

            cursor.close()

            val alertDialogBuilder = AlertDialog.Builder(context, R.style.DialogTheme)
            alertDialogBuilder.setItems(arrayOf(context.getString(R.string.main_modify), context.getString(R.string.main_delete))) { dialog, id ->

                if (id == 0) { // edit

                        val editIntent = Intent(context, EditDialogActivity::class.java)
                        editIntent.putExtra("position", adapterPosition)
                        editIntent.putExtra("id", notiId)
                        editIntent.putExtra("title", title)
                        editIntent.putExtra("text", text)
                        editIntent.putExtra("color", color)
                        context.startActivity(editIntent)

                    } else if (id == 1) { // delete

                        // delete notification
                        val deleteIntent = Intent(context, NotificationService::class.java)
                        deleteIntent.putExtra("id", (-1 * notiId).toString())
                        context.startService(deleteIntent)

                        // delete database
                        db.delete(notiId.toInt())

                        // delete from list
                        adapter.deleteItem(adapterPosition)

                        Toast.makeText(context, context.getString(R.string.alert_deleted), Toast.LENGTH_SHORT).show()

                    }

            }

            alertDialogBuilder.create().show()

        }

    }

}
