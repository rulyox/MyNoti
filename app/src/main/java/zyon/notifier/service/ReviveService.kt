package zyon.notifier.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

import zyon.notifier.notification.Database

class ReviveService : Service() {

    override fun onBind(intent: Intent): IBinder? { return null }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)
        val qaBool = prefs.getBoolean("QUICK_ADD_USE", false)

        if(qaBool) {

            val notifyQA = Intent(this@ReviveService, QuickAddService::class.java)
            notifyQA.putExtra("use", true)
            startService(notifyQA)

        }

        // database
        val db = Database(this)
        val cursor = db.selectAll()
        cursor.moveToFirst()

        for(i in 0 until cursor.count) {

            val id = cursor.getLong(cursor.getColumnIndex("id"))
            val title = cursor.getString(cursor.getColumnIndex("title"))
            val text = cursor.getString(cursor.getColumnIndex("text"))
            val color = cursor.getString(cursor.getColumnIndex("color"))

            // create notification
            val notifyIntent = Intent(this@ReviveService, NotificationService::class.java)
            notifyIntent.putExtra("id", id.toString() + "")
            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("text", text)
            notifyIntent.putExtra("color", color)
            startService(notifyIntent)

            cursor.moveToNext()

        }

        cursor.close()

        this.stopSelf()

        return startId

    }

}
