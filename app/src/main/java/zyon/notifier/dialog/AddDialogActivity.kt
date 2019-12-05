package zyon.notifier.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window

import kotlinx.android.synthetic.main.dialog_add.*

import zyon.notifier.DBHelper
import zyon.notifier.MainActivity
import zyon.notifier.R
import zyon.notifier.service.NotiService

import com.chiralcode.colorpicker.ColorPickerDialog
import com.chiralcode.colorpicker.ColorPickerDialog.OnColorSelectedListener

class AddDialogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add)

        val intent = intent
        if (intent.getIntExtra("qa", -1) == 1) sendBroadcast(Intent("FINISH_ACTIVITY"))

        // 프리퍼런스
        val prefs = getSharedPreferences(Activity::class.java.simpleName, Context.MODE_PRIVATE)
        var notificationNumber = prefs.getInt("notificationNumber", 1)
        var colorString = prefs.getString("notiColor", "#3F51B5")

        // 데이터베이스
        val db = DBHelper(this).writableDatabase

        // color preview
        color_preview_add.setBackgroundColor(Color.parseColor(colorString))

        // color dialog button
        color_choose_add.setOnClickListener {

            val initialColor = Color.parseColor(colorString)

            val colorPickerDialog = ColorPickerDialog(this@AddDialogActivity, initialColor, OnColorSelectedListener { color ->
                colorString = String.format("#%06X", 0xFFFFFF and color)
                color_preview_add.setBackgroundColor(Color.parseColor(colorString))
            })

            colorPickerDialog.show()

        }

        // add button
        button_add.setOnClickListener {

            val title = dialog_title.text.toString()
            val text = dialog_text.text.toString()

            //추가되는 데이터 아이디(알림 카운터) : notificationNumber
            // 데이터베이스에 추가
            db.execSQL("INSERT INTO " + MainActivity.TABLE_NAME + " VALUES ( " + notificationNumber + ", '" + title + "', '" + text + "', '" + colorString + "' );")

            // 알림 생성
            val notiIntent = Intent(this@AddDialogActivity, NotiService::class.java)
            notiIntent.putExtra("id", notificationNumber.toString() + "")
            notiIntent.putExtra("title", title)
            notiIntent.putExtra("text", text)
            notiIntent.putExtra("color", colorString)
            startService(notiIntent)

            // 알림 카운터
            notificationNumber++
            val editor = prefs.edit()
            editor.putInt("notificationNumber", notificationNumber)
            editor.apply()
            setResult(RESULT_OK, Intent())
            finish()

        }

        // cancel button
        button_cancel.setOnClickListener { finish() }

    }

}
