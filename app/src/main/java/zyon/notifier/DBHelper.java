package zyon.notifier;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static zyon.notifier.MainActivity.TABLE_NAME;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context)
    {
        super(context, "Database.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {

        String query = String.format( "CREATE TABLE %s " +
                "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "%s TEXT, " + "%s TEXT, " + "%s TEXT);"
                , TABLE_NAME, "KEY_TITLE", "KEY_TEXT", "KEY_COLOR");
        db.execSQL( query );

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String query = String.format( "DROP TABLE IF EXISTS %s", TABLE_NAME );
        db.execSQL( query );
        onCreate( db );

    }

}