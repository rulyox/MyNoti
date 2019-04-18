package zyon.notifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.chiralcode.colorpicker.ColorPickerDialog;

public class Activity_Dialog_Add extends Activity {

    // 데이터베이스
    final static String TABLE_NAME = "NOTI";
    SQLiteDatabase DB;

    // 프리퍼런스
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int notificationNumber;

    String title_string;
    String text_string;
    String color_string;

    View color_add_choose;
    View color_add_preview;
    EditText DialogTitle;
    EditText DialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add);

        Intent intent = getIntent();
        if( intent.getIntExtra("qa", -1) == 1 ) sendBroadcast(new Intent("FINISH_ACTIVITY"));

        // 프리퍼런스
        prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        editor = prefs.edit();
        notificationNumber = prefs.getInt("notificationNumber", 1);

        // 데이터베이스
        DB = new DB_Helper(this).getWritableDatabase();

        set();

    }

    void set() {

        color_string = prefs.getString("notiColor", "#3F51B5");
        color_add_choose = findViewById(R.id.color_choose_add);
        color_add_preview = findViewById(R.id.color_preview_add);
        color_add_preview.setBackgroundColor( Color.parseColor(color_string) );

        DialogTitle = findViewById( R.id.dialog_title );
        DialogText = findViewById( R.id.dialog_text );

        color_add_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int initialColor = Color.parseColor(color_string);
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(Activity_Dialog_Add.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {

                        color_string = String.format("#%06X", (0xFFFFFF & color));
                        color_add_preview.setBackgroundColor( Color.parseColor(color_string) );

                    }
                });
                colorPickerDialog.show();

            }
        });

        Button add = findViewById(R.id.button_add);
        add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                title_string = DialogTitle.getText().toString();
                text_string = DialogText.getText().toString();

                //추가되는 데이터 아이디(알림 카운터) : notificationNumber

                // 데이터베이스에 추가
                DB.execSQL( "INSERT INTO " + TABLE_NAME + " VALUES ( " + notificationNumber + ", '" + title_string + "', '" + text_string + "', '" + color_string + "' );" );

                // 알림 생성
                Intent notify = new Intent(Activity_Dialog_Add.this, Service_Noti.class);
                notify.putExtra("id", notificationNumber+""); notify.putExtra("title", title_string);
                notify.putExtra("text", text_string); notify.putExtra("color", color_string);
                startService(notify);

                // 알림 카운터
                notificationNumber++;
                editor.putInt("notificationNumber", notificationNumber);
                editor.commit();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        Button cancel = findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

    }

}
