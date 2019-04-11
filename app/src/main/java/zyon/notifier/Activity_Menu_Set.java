package zyon.notifier;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import com.chiralcode.colorpicker.ColorPickerDialog;

public class Activity_Menu_Set extends AppCompatActivity {

    // 프리퍼런스
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    String noti_color_default;
    int noti_time_bool;
    int qa_bool;
    String qa_color;

    View color_preview;
    View color_choose;
    CheckBox TimeSet;

    Switch QA;
    View qa_color_choose;
    View qa_color_preview;

    Intent notifyQA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerReceiver(finishActivity, new IntentFilter("FINISH_ACTIVITY"));

        prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        editor = prefs.edit();
        noti_color_default = prefs.getString("notiColor", "#3F51B5");
        noti_time_bool = prefs.getInt("notiTimeBoolean", 0);
        qa_bool = prefs.getInt("quickaddBoolean", 0);
        qa_color = prefs.getString("quickaddColor", "#FF4081");

        color_preview = findViewById(R.id.color_preview);
        color_preview.setBackgroundColor( Color.parseColor(noti_color_default) );
        color_choose = findViewById(R.id.color_choose);
        TimeSet = findViewById(R.id.checkbox_showtime);

        QA = findViewById(R.id.switch_quickadd);
        qa_color_choose = findViewById(R.id.qa_color_choose);
        qa_color_preview = findViewById(R.id.qa_color_preview);
        qa_color_preview.setBackgroundColor( Color.parseColor(qa_color) );

        notifyQA = new Intent(Activity_Menu_Set.this, Service_QA.class);

        //알림 설정
        SetNoti();

        //빠른 추가 설정
        SetQA();

    }

    // 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: onBackPressed();
                return true;
            case R.id.menu_reset: Reset();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(finishActivity);

    }

    private final BroadcastReceiver finishActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finishAffinity();
        }
    };

    // 알림 설정
    void SetNoti(){

        // 색 선택
        color_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int initialColor = Color.parseColor(noti_color_default);
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(Activity_Menu_Set.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {

                        noti_color_default = String.format("#%06X", (0xFFFFFF & color));
                        editor.putString("notiColor", noti_color_default);
                        editor.apply();

                        color_preview.setBackgroundColor( Color.parseColor(noti_color_default) );

                    }
                });
                colorPickerDialog.show();

            }
        });

        // 시간 표시
        TimeSet.setChecked( (noti_time_bool != 0) );
        TimeSet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) noti_time_bool = 1;
                else noti_time_bool = 0;
                editor.putInt("notiTimeBoolean", noti_time_bool);
                editor.apply();
            }
        });

    }

    // 빠른 추가 설정
    void SetQA(){

        // 활성화
        QA.setChecked( (qa_bool != 0) );
        QA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Snackbar.make(findViewById(R.id.activity_set), getString(R.string.alert_qa_en), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }else{
                    Snackbar.make(findViewById(R.id.activity_set), getString(R.string.alert_qa_dis), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }

                if(isChecked){
                    qa_bool = 1;
                    editor.putInt("quickaddBoolean", qa_bool);
                    editor.apply();
                } else{
                    qa_bool = 0;
                    editor.putInt("quickaddBoolean", qa_bool);
                    editor.apply();
                }

                if(isChecked) notifyQA.putExtra("check", "1");
                else notifyQA.putExtra("check", "0");
                startService(notifyQA);

            }
        });

        // 색 선택
        qa_color_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int initialColor = Color.parseColor(qa_color);
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(Activity_Menu_Set.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {

                        qa_color = String.format("#%06X", (0xFFFFFF & color));
                        editor.putString("quickaddColor", qa_color);
                        editor.apply();

                        notifyQA.putExtra("check", qa_bool+"");
                        startService(notifyQA);

                        qa_color_preview.setBackgroundColor( Color.parseColor(qa_color) );

                    }
                });
                colorPickerDialog.show();

            }
        });

    }

    // 설정 초기화
    void Reset(){

        noti_color_default = "#3F51B5";
        noti_time_bool = 0;
        qa_bool = 0;
        qa_color = "#FF4081";

        editor.putString("notiColor", noti_color_default);
        editor.putInt("notiTimeBoolean", noti_time_bool);
        editor.putInt("quickaddBoolean", qa_bool);
        editor.putString("quickaddColor", qa_color);
        editor.apply();

        color_preview.setBackgroundColor( Color.parseColor(noti_color_default) );
        TimeSet.setChecked( (noti_time_bool != 0) );

        QA.setChecked( (qa_bool != 0) );
        notifyQA.putExtra("check", "0");
        startService(notifyQA);

        qa_color_preview.setBackgroundColor( Color.parseColor(qa_color) );

    }

}