package zyon.notifier.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder

import androidx.core.app.NotificationCompat

import zyon.notifier.R

class NotiService : Service() {

    override fun onBind(intent: Intent): IBinder? { return null }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // get data from intent
        val id = intent.getStringExtra("id")!!.toInt()
        val title = intent.getStringExtra("title")
        val text = intent.getStringExtra("text")
        val color = intent.getStringExtra("color")

        createNoti(id, title, text, color)

        this.stopSelf()

        return startId

    }

    // create notification
    private fun createNoti(id: Int, title: String?, text: String?, color: String?) {

        val notificationMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "Notification"

        if(id < 0) {

            notificationMgr.cancel(-1 * id)
            return

        }

        val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.icon_noti)
                .setAutoCancel(false)
                .setOngoing(true)
                .setShowWhen(false)
                .setColor(Color.parseColor(color))
                .setGroup("" + id)
                .setChannelId(channelId)
                .build()

        notification.flags = Notification.FLAG_NO_CLEAR

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(channelId, resources.getString(R.string.main_notifications), NotificationManager.IMPORTANCE_LOW)
            notificationMgr.createNotificationChannel(channel)

        }

        notificationMgr.notify(id, notification)

    }

}
