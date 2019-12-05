package zyon.notifier.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

import zyon.notifier.DBHelper
import zyon.notifier.MainActivity

class ReviveService : Service() {

    override fun onBind(intent: Intent): IBinder? { return null }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)
        val qaBool = prefs.getInt("quickaddBoolean", 0)

        if(qaBool != 0) {
            val notifyQA = Intent(this@ReviveService, QuickAddService::class.java)
            notifyQA.putExtra("check", "1")
            startService(notifyQA)
        }

        // 데이터베이스
        val db = DBHelper(this).writableDatabase
        val cursor = db.rawQuery("SELECT * FROM " + MainActivity.TABLE_NAME, null)
        cursor.moveToFirst()

        for(i in 0 until cursor.count) {
            val id = cursor.getLong(cursor.getColumnIndex("_id"))
            val title = cursor.getString(cursor.getColumnIndex("KEY_TITLE"))
            val text = cursor.getString(cursor.getColumnIndex("KEY_TEXT"))
            val color = cursor.getString(cursor.getColumnIndex("KEY_COLOR"))
            //알림 생성
            val notify = Intent(this@ReviveService, NotiService::class.java)
            notify.putExtra("id", id.toString() + "")
            notify.putExtra("title", title)
            notify.putExtra("text", text)
            notify.putExtra("color", color)
            startService(notify)
            cursor.moveToNext()
        }

        cursor.close()

        this.stopSelf()

        return startId

    }

}
