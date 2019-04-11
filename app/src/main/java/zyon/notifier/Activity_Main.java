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
import java.util.Locale;

public class Activity_Main extends AppCompatActivity {

    // 데이터베이스
    final static String TABLE_NAME = "NOTI";
    DB_Helper mHelper;
    SQLiteDatabase db;
    Cursor cursor;

    // 리싸이클러뷰
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    ArrayList<List_Main> mArrayList;
    Adapter_Main mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerReceiver(finishActivity, new IntentFilter("FINISH_ACTIVITY"));

        //추가 버튼
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialog = new Intent(Activity_Main.this, Activity_Dialog_Add.class);
                startActivityForResult(dialog,1);
            }
        });

        //데이터베이스
        mHelper = new DB_Helper(this);
        db = mHelper.getWritableDatabase();
        cursor = db.rawQuery( String.format( "SELECT * FROM %s", TABLE_NAME ), null );

        setList();

        //알림 복구
        Intent revive = new Intent(this, Service_Revive.class);
        startService(revive);

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
            Intent info = new Intent(this, Activity_Menu_Info.class);
            startActivity(info);
            return true;
        }else if (id == R.id.menu_set) {
            Intent set = new Intent(this, Activity_Menu_Set.class);
            startActivity(set);
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

        if(resultCode == RESULT_OK){

            refreshList();

            if(requestCode == 1) Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_added), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            else if(requestCode == 2) Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_modified), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        }
        else{

            Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_canceled), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        }

    }

    void setList(){

        mRecyclerView = findViewById(R.id.list_main_recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mArrayList = new ArrayList<>();
        mAdapter = new Adapter_Main(mArrayList, this);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        Cursor c = cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {

            List_Main item =
                    new List_Main(c.getString( c.getColumnIndex( "KEY_COLOR" ) ), c.getString( c.getColumnIndex( "KEY_TITLE" ) ),
                            c.getString( c.getColumnIndex( "KEY_TEXT" ) ));
            mArrayList.add(item);

            c.moveToNext();

        }

        mAdapter.notifyDataSetChanged();

    }

    void refreshList(){

        mRecyclerView = findViewById(R.id.list_main_recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mArrayList = new ArrayList<>();
        mAdapter = new Adapter_Main(mArrayList, this);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        Cursor c = cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {

            List_Main item =
                    new List_Main(c.getString( c.getColumnIndex( "KEY_COLOR" ) ), c.getString( c.getColumnIndex( "KEY_TITLE" ) ),
                            c.getString( c.getColumnIndex( "KEY_TEXT" ) ));
            mArrayList.add(item);

            c.moveToNext();

        }

        mAdapter.notifyDataSetChanged();

    }

    //알림 수정, 삭제
    void Edit(final int pos){

        final Cursor c = cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();

        for(int i = 0; i < pos; i++) c.moveToNext();
        final long DBid = c.getLong( c.getColumnIndex( "_id" ) );
        final String DBtitle = c.getString( c.getColumnIndex( "KEY_TITLE" ) );
        final String DBtext = c.getString( c.getColumnIndex( "KEY_TEXT" ) );
        final String DBcolor = c.getString( c.getColumnIndex( "KEY_COLOR" ) );

        CharSequence[] items = new String[] { getString(R.string.main_modify), getString(R.string.main_delete) };

        //선택창
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Main.this);
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(id == 0) { //수정

                            Intent dialogInt = new Intent(Activity_Main.this, Activity_Dialog_Edit.class);
                            dialogInt.putExtra("title", DBtitle);
                            dialogInt.putExtra("text", DBtext);
                            dialogInt.putExtra("color", DBcolor);
                            dialogInt.putExtra("noti_id", DBid);
                            startActivityForResult(dialogInt,2);

                        }else if(id == 1) { //삭제

                            //알림 삭제
                            Intent notify = new Intent(Activity_Main.this, Service_Noti.class);
                            notify.putExtra("id", -1*(int)DBid+"");
                            startService(notify);

                            //데이터베이스 삭제
                            String query = String.format(Locale.ENGLISH, "DELETE FROM %s WHERE _id = %d;", TABLE_NAME, DBid );
                            db.execSQL( query );

                            refreshList();
                            Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_deleted), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                        }

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}