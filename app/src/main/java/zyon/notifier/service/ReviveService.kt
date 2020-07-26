package zyon.notifier.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import zyon.notifier.notification.NotificationDAO
import zyon.notifier.notification.Notification
import java.util.*

class ReviveService: Service() {

    override fun onBind(intent: Intent): IBinder? { return null }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        NotificationDAO.initDB(application)

        loadQA()
        loadNotification()

        this.stopSelf()

        return startId

    }

    private fun loadQA() {

        // load data from prefs
        val prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)
        val useQA = prefs.getBoolean("QUICK_ADD_USE", false)

        if(useQA) {

            val notifyQA = Intent(this@ReviveService, QuickAddService::class.java)
            notifyQA.putExtra("use", true)
            startService(notifyQA)

        }

    }

    private fun loadNotification() {

        val notificationList: ArrayList<Notification> = NotificationDAO.getNotificationList()

        for(notification in notificationList) {

            // create notification
            val notifyIntent = Intent(this@ReviveService, NotificationService::class.java)
            notifyIntent.putExtra("action", "create")
            notifyIntent.putExtra("id", notification.id)
            notifyIntent.putExtra("title", notification.title)
            notifyIntent.putExtra("text", notification.text)
            notifyIntent.putExtra("color", notification.color)
            startService(notifyIntent)

        }

    }

}
