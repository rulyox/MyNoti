package zyon.notifier.dialog;

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

import zyon.notifier.DBHelper;
import zyon.notifier.R;
import zyon.notifier.service.NotiService;

import static zyon.notifier.MainActivity.TABLE_NAME;

public class AddDialogActivity extends Activity {

    // 데이터베이스
    SQLiteDatabase DB;

    // 프리퍼런스
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int notificationNumber;

    String title_string;
    String text_string;
    String color_string;

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
        DB = new DBHelper(this).getWritableDatabase();

        color_string = prefs.getString("notiColor", "#3F51B5");
        final View color_add_choose = findViewById(R.id.color_choose_add);
        final View color_add_preview = findViewById(R.id.color_preview_add);
        color_add_preview.setBackgroundColor( Color.parseColor(color_string) );

        final EditText DialogTitle = findViewById( R.id.dialog_title );
        final EditText DialogText = findViewById( R.id.dialog_text );

        color_add_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int initialColor = Color.parseColor(color_string);
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(AddDialogActivity.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {
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
                startService(
                        new Intent(AddDialogActivity.this, NotiService.class)
                                .putExtra("id", notificationNumber+"")
                                .putExtra("title", title_string)
                                .putExtra("text", text_string)
                                .putExtra("color", color_string)
                );

                // 알림 카운터
                notificationNumber++;
                editor.putInt("notificationNumber", notificationNumber);
                editor.commit();

                setResult(RESULT_OK, new Intent());
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
