package zyon.notifier;

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

public class Activity_Dialog_Edit extends Activity {

    // 데이터베이스
    final static String TABLE_NAME = "NOTI";
    DB_Helper mHelper;
    SQLiteDatabase db;

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
        setContentView(R.layout.dialog_edit);

        // 데이터베이스
        mHelper = new DB_Helper(this);
        db = mHelper.getWritableDatabase();

        set();

    }

    void set(){

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        String color = intent.getStringExtra("color");
        final long noti_id = intent.getLongExtra("noti_id", -1);

        color_string = color;
        color_add_choose = findViewById(R.id.color_choose_add);
        color_add_preview = findViewById(R.id.color_preview_add);
        color_add_preview.setBackgroundColor( Color.parseColor(color_string) );

        DialogTitle = findViewById( R.id.dialog_title );
        DialogText = findViewById( R.id.dialog_text );

        DialogTitle.setText( title );
        DialogText.setText( text );

        color_add_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int initialColor = Color.parseColor(color_string);
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(Activity_Dialog_Edit.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {
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
                db.execSQL( "DELETE FROM " + TABLE_NAME + " WHERE _id = " + noti_id + ";" );
                db.execSQL( "INSERT INTO " + TABLE_NAME + " VALUES ( " + noti_id + ", '" + title_string + "', '" + text_string + "', '" + color_string + "' );" );

                // 알림 생성
                Intent notify = new Intent(Activity_Dialog_Edit.this, Service_Noti.class);
                notify.putExtra("id", (int)noti_id+""); notify.putExtra("title", title_string);
                notify.putExtra("text", text_string); notify.putExtra("color", color_string);
                startService(notify);

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
