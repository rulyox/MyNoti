package zyon.notifier.dialog;

import android.app.Activity;
import android.content.Intent;
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

public class EditDialogActivity extends Activity {

    // 데이터베이스
    SQLiteDatabase DB;

    String title_string;
    String text_string;
    String color_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit);

        // 데이터베이스
        DB = new DBHelper(this).getWritableDatabase();

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        String color = intent.getStringExtra("color");
        final long noti_id = intent.getLongExtra("noti_id", -1);

        color_string = color;
        final View color_add_choose = findViewById(R.id.color_choose_add);
        final View color_add_preview = findViewById(R.id.color_preview_add);
        color_add_preview.setBackgroundColor( Color.parseColor(color_string) );

        final EditText DialogTitle = findViewById( R.id.dialog_title );
        final EditText DialogText = findViewById( R.id.dialog_text );

        DialogTitle.setText( title );
        DialogText.setText( text );

        color_add_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int initialColor = Color.parseColor(color_string);
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(EditDialogActivity.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {
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

                // 데이터베이스 수정
                DB.execSQL( "DELETE FROM " + TABLE_NAME + " WHERE _id = " + noti_id + ";" );
                DB.execSQL( "INSERT INTO " + TABLE_NAME + " VALUES ( " + noti_id + ", '" + title_string + "', '" + text_string + "', '" + color_string + "' );" );

                // 알림 생성
                startService(
                        new Intent(EditDialogActivity.this, NotiService.class)
                                .putExtra("id", (int)noti_id+"")
                                .putExtra("title", title_string)
                                .putExtra("text", text_string)
                                .putExtra("color", color_string)
                );

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
