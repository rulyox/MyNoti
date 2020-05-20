package zyon.notifier.adapter

import zyon.notifier.notification.Notification
import java.util.ArrayList

object NotificationAdapterObject {

    private lateinit var adapter: NotificationAdapter

    fun init(notificationList: ArrayList<Notification>) {

        adapter = NotificationAdapter(notificationList)

    }

    fun get(): NotificationAdapter {

        return adapter

    }

    fun refresh() {

        adapter.notifyDataSetChanged()

    }

    fun update(position: Int, notification: Notification) {

        adapter.updateItem(position, notification)

    }

    fun delete(position: Int) {

        adapter.deleteItem(position)

    }

}
