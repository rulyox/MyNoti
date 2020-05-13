package zyon.notifier.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import zyon.notifier.R
import zyon.notifier.activity.AddDialogActivity

class QuickAddService: Service() {

    override fun onBind(intent: Intent): IBinder? { return null }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // load data from intent
        val useQA = intent.getBooleanExtra("use", false)

        // load data from prefs
        val prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)
        val color = prefs.getString("QUICK_ADD_COLOR", "#3F51B5") ?: "#3F51B5"

        if(useQA) enableQA(color)
        else disableQA()

        this.stopSelf()

        return startId

    }

    private fun enableQA(color: String) {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "QuickAdd"

        val addDialogIntent = Intent(this, AddDialogActivity::class.java)
        addDialogIntent.putExtra("qa", true)

        val clickIntent = PendingIntent.getActivity(this, 0, addDialogIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // create notification
        val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.qa))
                .setContentText(getString(R.string.qa_text))
                .setSmallIcon(R.drawable.button_add)
                .setAutoCancel(false)
                .setOngoing(true)
                .setShowWhen(false)
                .setPriority(-2) // Notification.PRIORITY_MIN for API < 26
                .setContentIntent(clickIntent)
                .setColor(Color.parseColor(color))
                .setGroup("0")
                .setChannelId(channelId)
                .build()

        notification.flags = Notification.FLAG_NO_CLEAR

        // notification settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, resources.getString(R.string.qa), NotificationManager.IMPORTANCE_MIN)
            notificationManager.createNotificationChannel(channel)
        }

        // notify
        notificationManager.notify(0, notification)

    }

    private fun disableQA() {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(0)

    }

}
