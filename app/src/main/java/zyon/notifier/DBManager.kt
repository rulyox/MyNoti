package zyon.notifier

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(context: Context?) : SQLiteOpenHelper(context, "notification.db", null, 1) {

    private val table = "notification"

    override fun onCreate(db: SQLiteDatabase) {

        val query = String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY AUTOINCREMENT, `title` TEXT, `text` TEXT, `color` TEXT)"
                ,table)

        db.execSQL(query)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        val query = String.format("DROP TABLE IF EXISTS %s",
                table)

        db.execSQL(query)

        onCreate(db)

    }

    fun insert(id: Int, title: String, text: String, color: String) {

        val query = String.format("INSERT INTO %s VALUES (%s, '%s', '%s', '%s')",
                table, id, title, text, color)

        writableDatabase.execSQL(query)

    }

    fun delete(id: Int) {

        val query = String.format("DELETE FROM %s WHERE id = %s",
                table, id)

        writableDatabase.execSQL(query)

    }

    fun update(id: Int, title: String, text: String, color: String) {

        val query = String.format("UPDATE %s SET `title` = '%s', `text` = '%s', `color` = '%s' WHERE id = %s",
                table, title, text, color, id)

        writableDatabase.execSQL(query)

    }

    fun selectAll(): Cursor {

        val query = String.format("SELECT * FROM %s", table)

        return writableDatabase.rawQuery(query, null)

    }

}
