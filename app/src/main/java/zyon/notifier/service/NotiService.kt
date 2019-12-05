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

        // get data from preferences
        val prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)
        val showTime = prefs.getInt("notiTimeBoolean", 0) != 0

        createNoti(id, title, text, color, showTime)

        this.stopSelf()

        return startId

    }

    // 알림 생성
    private fun createNoti(id: Int, title: String?, text: String?, color: String?, showTime: Boolean) {

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
                .setShowWhen(showTime)
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