package zyon.notifier.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import zyon.notifier.R

class NotificationService: Service() {

    override fun onBind(intent: Intent): IBinder? { return null }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // get data from intent
        val action = intent.getStringExtra("action")
        val id = intent.getIntExtra("id", 0)

        if(action == "create") {

            val title = intent.getStringExtra("title")
            val text = intent.getStringExtra("text")
            val color = intent.getStringExtra("color")

            createNotification(id, title, text, color)

        } else if(action == "remove") {

            removeNotification(id)

        }

        this.stopSelf()

        return startId

    }

    private fun createNotification(id: Int, title: String?, text: String?, color: String?) {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "Notification"

        // create notification
        val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.icon_noti)
                .setAutoCancel(false)
                .setOngoing(true)
                .setShowWhen(false)
                .setColor(Color.parseColor(color))
                .setGroup(id.toString())
                .setChannelId(channelId)
                .build()

        notification.flags = Notification.FLAG_NO_CLEAR

        // notification settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, resources.getString(R.string.main_notifications), NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        // notify
        notificationManager.notify(id, notification)

    }

    private fun removeNotification(id: Int) {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(id)

        return

    }

}
