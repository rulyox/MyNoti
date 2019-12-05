package zyon.notifier.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder

import androidx.core.app.NotificationCompat

import zyon.notifier.R
import zyon.notifier.dialog.AddDialogActivity

class QuickAddService : Service() {

    override fun onBind(intent: Intent): IBinder? { return null }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // get data from intent
        val check = intent.getStringExtra("check")!!.toInt()

        // get data from preferences
        val prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)
        val color = prefs.getString("quickaddColor", "#FF4081")!!

        createQuickAdd(check != 0, color)

        this.stopSelf()

        return startId

    }

    private fun createQuickAdd(checked: Boolean, color: String) {

        val notification: Notification
        val notificationMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "Quick Add"

        if (checked) {

            val addIntent = Intent(this, AddDialogActivity::class.java)
            addIntent.putExtra("qa", 1)

            val clickIntent = PendingIntent.getActivity(this, 0, addIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                notification = NotificationCompat.Builder(this, channelId)
                        .setContentTitle(getString(R.string.qa))
                        .setContentText(getString(R.string.qa_text))
                        .setSmallIcon(R.drawable.button_add)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setContentIntent(clickIntent)
                        .setColor(Color.parseColor(color))
                        .setGroup("" + 0)
                        .setChannelId(channelId)
                        .build()

                notification.flags = Notification.FLAG_NO_CLEAR

                val channel = NotificationChannel(channelId, resources.getString(R.string.qa), NotificationManager.IMPORTANCE_MIN)
                notificationMgr.createNotificationChannel(channel)

                notificationMgr.notify(0, notification)

            } else {

                notification = Notification.Builder(applicationContext)
                        .setContentTitle(getString(R.string.qa))
                        .setContentText(getString(R.string.qa_text))
                        .setSmallIcon(R.drawable.button_add)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setPriority(Notification.PRIORITY_MIN)
                        .setContentIntent(clickIntent)
                        .setColor(Color.parseColor(color))
                        .setGroup("" + 0)
                        .build()

                notification.flags = Notification.FLAG_NO_CLEAR

                notificationMgr.notify(0, notification)

            }

        } else {

            notificationMgr.cancel(0)

        }

    }

}
