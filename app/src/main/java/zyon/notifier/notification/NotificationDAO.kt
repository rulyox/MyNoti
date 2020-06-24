package zyon.notifier.notification

import android.content.Context
import java.util.ArrayList

class NotificationDAO(context: Context) {

    private var db = NotificationDatabase(context)

    fun getNotificationList(): ArrayList<Notification> {

        val notificationList: ArrayList<Notification> = ArrayList()

        val cursor = db.selectAll()

        cursor.moveToFirst()

        for (i in 0 until cursor.count) {

            val notification = Notification(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("text")),
                    cursor.getString(cursor.getColumnIndex("color"))
            )

            notificationList.add(notification)

            cursor.moveToNext()

        }

        cursor.close()

        return notificationList

    }

    fun getNotification(id: Int): Notification? {

        val cursor = db.select(id)

        if(cursor.count > 0) {

            val notification = Notification(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("text")),
                    cursor.getString(cursor.getColumnIndex("color"))
            )

            return notification

        } else {

            return null

        }

    }

    fun addNotification(notification: Notification) {

        val id = notification.id
        val title = notification.title
        val text = notification.text
        val color = notification.color

        db.insert(id, title, text, color)

    }

    fun deleteNotification(id: Int) {

        db.delete(id)

    }

    fun updateNotification(id: Int, title: String, text: String, color: String) {

        db.update(id, title, text, color)

    }

}
