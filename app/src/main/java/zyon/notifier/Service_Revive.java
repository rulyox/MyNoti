package zyon.notifier;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import static zyon.notifier.Activity_Main.TABLE_NAME;

public class Service_Revive extends Service {

    // 데이터베이스
    DB_Helper mHelper;
    SQLiteDatabase db;
    Cursor cursor;

    SharedPreferences prefs;
    int qa_bool;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        qa_bool = prefs.getInt("quickaddBoolean", 0);
        if(qa_bool != 0){
            Intent notifyQA = new Intent(Service_Revive.this, Service_QA.class);
            notifyQA.putExtra("check", "1");
            startService(notifyQA);
        }

        // 데이터베이스
        mHelper = new DB_Helper(this);
        db = mHelper.getWritableDatabase();
        cursor = db.rawQuery( String.format( "SELECT * FROM %s", TABLE_NAME ), null );

        Cursor c = cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {

            long id = c.getLong(c.getColumnIndex("_id"));
            String title_string = c.getString( c.getColumnIndex( "KEY_TITLE" ) );
            String text_string = c.getString( c.getColumnIndex( "KEY_TEXT" ) );
            String color_string = c.getString( c.getColumnIndex( "KEY_COLOR" ) );

            //알림 생성
            Intent notify = new Intent(Service_Revive.this, Service_Noti.class);
            notify.putExtra("id", id+""); notify.putExtra("title", title_string);
            notify.putExtra("text", text_string); notify.putExtra("color", color_string);
            startService(notify);

            c.moveToNext();

        }

        this.stopSelf();

        return startId;

    }

}