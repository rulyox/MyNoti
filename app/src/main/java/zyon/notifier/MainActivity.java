package zyon.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import zyon.notifier.dialog.AddDialogActivity;
import zyon.notifier.dialog.EditDialogActivity;
import zyon.notifier.notification.NotiAdapter;
import zyon.notifier.notification.Notification;
import zyon.notifier.service.NotiService;
import zyon.notifier.service.ReviveService;

public class MainActivity extends AppCompatActivity {

    // 데이터베이스
    public final static String TABLE_NAME = "NOTI";
    SQLiteDatabase DB;

    // 리싸이클러뷰
    RecyclerView mRecyclerView;
    ArrayList<Notification> mArrayList;
    NotiAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerReceiver(finishActivity, new IntentFilter("FINISH_ACTIVITY"));

        // 추가 버튼
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent addDialogIntent = new Intent(MainActivity.this, AddDialogActivity.class);
            startActivityForResult(addDialogIntent, 1);
        });

        // 데이터베이스
        DB = new DBHelper(this).getWritableDatabase();

        setRecycler();
        setList();

        // 알림 복구
        startService( new Intent(this, ReviveService.class) );

    }

    // 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_set) {
            startActivity( new Intent(this, SettingsActivity.class) );
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            setList();

//            if(requestCode == 1) Snackbar.make(findViewById(R.id.content_parent), getString(R.string.alert_added), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
//            else if(requestCode == 2) Snackbar.make(findViewById(R.id.content_parent), getString(R.string.alert_modified), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        } else {

//            Snackbar.make(findViewById(R.id.content_parent), getString(R.string.alert_canceled), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        }

    }

    void setRecycler() {

        mRecyclerView = findViewById(R.id.list_main_recycler);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(this) );
        mRecyclerView.addItemDecoration( new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL) );

    }

    void setList() {

        mArrayList = new ArrayList<>();

        Cursor c = DB.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();

        for(int i = 0; i < c.getCount(); i++) {

            mArrayList.add(
                    new Notification(
                            c.getString( c.getColumnIndex( "KEY_COLOR" ) ),
                            c.getString( c.getColumnIndex( "KEY_TITLE" ) ),
                            c.getString( c.getColumnIndex( "KEY_TEXT" ) )
                    )
            );

            c.moveToNext();

        }

        c.close();

        mAdapter = new NotiAdapter(mArrayList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    // 알림 수정, 삭제
    public void Edit(final int pos) {

        Cursor c = DB.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();

        for(int i = 0; i < pos; i++) c.moveToNext();
        final long noti_id = c.getLong( c.getColumnIndex( "_id" ) );
        final String title_string = c.getString( c.getColumnIndex( "KEY_TITLE" ) );
        final String text_string = c.getString( c.getColumnIndex( "KEY_TEXT" ) );
        final String color_string = c.getString( c.getColumnIndex( "KEY_COLOR" ) );

        c.close();

        // 선택창
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setItems(
                new String[] { getString(R.string.main_modify), getString(R.string.main_delete) },
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(id == 0) { // 수정

                            startActivityForResult(
                                    new Intent(MainActivity.this, EditDialogActivity.class)
                                            .putExtra("title", title_string)
                                            .putExtra("text", text_string)
                                            .putExtra("color", color_string)
                                            .putExtra("noti_id", noti_id),
                                    2
                            );

                        } else if(id == 1) { // 삭제

                            // 알림 삭제
                            startService(
                                    new Intent(MainActivity.this, NotiService.class)
                                            .putExtra("id", -1*(int)noti_id+"")
                            );

                            // 데이터베이스 삭제
                            DB.execSQL( "DELETE FROM " + TABLE_NAME + " WHERE _id = " + noti_id + ";" );

                            setList();
                            Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_deleted), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                        }

                    }
                });
        alertDialogBuilder.create().show();

    }

}
