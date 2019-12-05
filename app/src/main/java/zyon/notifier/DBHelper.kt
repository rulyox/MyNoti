package zyon.notifier

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) : SQLiteOpenHelper(context, "Database.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        val query = String.format("CREATE TABLE %s " +
                "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "%s TEXT, " + "%s TEXT, " + "%s TEXT);"
                , MainActivity.TABLE_NAME, "KEY_TITLE", "KEY_TEXT", "KEY_COLOR")

        db.execSQL(query)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        val query = String.format("DROP TABLE IF EXISTS %s", MainActivity.TABLE_NAME)
        db.execSQL(query)
        onCreate(db)

    }

}
