package zyon.notifier.dialog

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window

import kotlinx.android.synthetic.main.dialog_edit.*

import zyon.notifier.DBHelper
import zyon.notifier.MainActivity
import zyon.notifier.R
import zyon.notifier.service.NotiService

import com.chiralcode.colorpicker.ColorPickerDialog
import com.chiralcode.colorpicker.ColorPickerDialog.OnColorSelectedListener

class EditDialogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_edit)

        // 데이터베이스
        val db = DBHelper(this).writableDatabase
        val intent = intent
        val title = intent.getStringExtra("title")
        val text = intent.getStringExtra("text")
        val color = intent.getStringExtra("color")
        val noti_id = intent.getLongExtra("noti_id", -1)
        var color_string = color

        color_preview_add.setBackgroundColor(Color.parseColor(color_string))

        dialog_title.setText(title)
        dialog_text.setText(text)

        color_choose_add.setOnClickListener {
            val initialColor = Color.parseColor(color_string)
            val colorPickerDialog = ColorPickerDialog(this@EditDialogActivity, initialColor, OnColorSelectedListener { color ->
                color_string = String.format("#%06X", 0xFFFFFF and color)
                color_preview_add.setBackgroundColor(Color.parseColor(color_string))
            })
            colorPickerDialog.show()
        }

        button_add.setOnClickListener {
            val title_string = dialog_title.text.toString()
            val text_string = dialog_text.text.toString()
            // 데이터베이스 수정
            db.execSQL("DELETE FROM " + MainActivity.TABLE_NAME + " WHERE _id = " + noti_id + ";")
            db.execSQL("INSERT INTO " + MainActivity.TABLE_NAME + " VALUES ( " + noti_id + ", '" + title_string + "', '" + text_string + "', '" + color_string + "' );")
            // 알림 생성
            startService(
                    Intent(this@EditDialogActivity, NotiService::class.java)
                            .putExtra("id", noti_id.toString() + "")
                            .putExtra("title", title_string)
                            .putExtra("text", text_string)
                            .putExtra("color", color_string)
            )
            setResult(RESULT_OK, Intent())
            finish()
        }

        button_cancel.setOnClickListener { finish() }
    }

}
