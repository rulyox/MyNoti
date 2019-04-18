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
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Activity_Main extends AppCompatActivity {

    // 데이터베이스
    final static String TABLE_NAME = "NOTI";
    SQLiteDatabase DB;

    // 리싸이클러뷰
    RecyclerView mRecyclerView;
    ArrayList<List_Main> mArrayList;
    Adapter_Main mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerReceiver(finishActivity, new IntentFilter("FINISH_ACTIVITY"));

        // 추가 버튼
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(Activity_Main.this, Activity_Dialog_Add.class),
                        1
                );
            }
        });

        // 데이터베이스
        DB = new DB_Helper(this).getWritableDatabase();

        setRecycler();
        setList();

        // 알림 복구
        startService( new Intent(this, Service_Revive.class) );

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

        if (id == R.id.menu_info) {
            startActivity( new Intent(this, Activity_Menu_Info.class) );
            return true;
        }else if (id == R.id.menu_set) {
            startActivity( new Intent(this, Activity_Menu_Set.class) );
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

            if(requestCode == 1) Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_added), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            else if(requestCode == 2) Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_modified), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        } else {

            Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_canceled), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

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
                    new List_Main(
                            c.getString( c.getColumnIndex( "KEY_COLOR" ) ),
                            c.getString( c.getColumnIndex( "KEY_TITLE" ) ),
                            c.getString( c.getColumnIndex( "KEY_TEXT" ) )
                    )
            );

            c.moveToNext();

        }

        c.close();

        mAdapter = new Adapter_Main(mArrayList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    // 알림 수정, 삭제
    void Edit(final int pos) {

        Cursor c = DB.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();

        for(int i = 0; i < pos; i++) c.moveToNext();
        final long DBid = c.getLong( c.getColumnIndex( "_id" ) );
        final String DBtitle = c.getString( c.getColumnIndex( "KEY_TITLE" ) );
        final String DBtext = c.getString( c.getColumnIndex( "KEY_TEXT" ) );
        final String DBcolor = c.getString( c.getColumnIndex( "KEY_COLOR" ) );

        c.close();

        CharSequence[] items = new String[] { getString(R.string.main_modify), getString(R.string.main_delete) };

        // 선택창
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Main.this);
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(id == 0) { // 수정

                            startActivityForResult(
                                    new Intent(Activity_Main.this, Activity_Dialog_Edit.class)
                                            .putExtra("title", DBtitle)
                                            .putExtra("text", DBtext)
                                            .putExtra("color", DBcolor)
                                            .putExtra("noti_id", DBid),
                                    2
                            );

                        } else if(id == 1) { // 삭제

                            // 알림 삭제
                            startService(
                                    new Intent(Activity_Main.this, Service_Noti.class)
                                            .putExtra("id", -1*(int)DBid+"")
                            );

                            // 데이터베이스 삭제
                            DB.execSQL( "DELETE FROM " + TABLE_NAME + " WHERE _id = " + DBid + ";" );

                            setList();
                            Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_deleted), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                        }

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}
