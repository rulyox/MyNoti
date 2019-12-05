package zyon.notifier

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import java.util.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

import com.google.android.material.snackbar.Snackbar

import zyon.notifier.dialog.AddDialogActivity
import zyon.notifier.dialog.EditDialogActivity
import zyon.notifier.notification.NotiAdapter
import zyon.notifier.notification.Notification
import zyon.notifier.service.NotiService
import zyon.notifier.service.ReviveService

class MainActivity : AppCompatActivity() {

    companion object {

        const val TABLE_NAME = "NOTI"

    }

    private var db: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        registerReceiver(finishActivity, IntentFilter("FINISH_ACTIVITY"))

        // database
        db = DBHelper(this).writableDatabase

        setUI()
        setList()

        // revive notifications
        startService(Intent(this, ReviveService::class.java))

    }

    // menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_set) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(finishActivity)
    }

    private val finishActivity: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finishAffinity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            setList()
//            if(requestCode == 1) Snackbar.make(findViewById(R.id.content_parent), getString(R.string.alert_added), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
//            else if(requestCode == 2) Snackbar.make(findViewById(R.id.content_parent), getString(R.string.alert_modified), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
//            Snackbar.make(findViewById(R.id.content_parent), getString(R.string.alert_canceled), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }

    }

    private fun setUI() {

        // add button
        fab.setOnClickListener {

            val addDialogIntent = Intent(this@MainActivity, AddDialogActivity::class.java)
            startActivityForResult(addDialogIntent, 1)

        }

        // recycler view
        list_main_recycler.layoutManager = LinearLayoutManager(this)
        list_main_recycler.addItemDecoration(DividerItemDecoration(list_main_recycler.context, DividerItemDecoration.VERTICAL))

    }

    private fun setList() {

        val mArrayList: ArrayList<Notification> = ArrayList()

        val cursor = db!!.rawQuery("SELECT * FROM $TABLE_NAME", null)
        cursor.moveToFirst()
        for (i in 0 until cursor.count) {

            val notification = Notification(
                    cursor.getString(cursor.getColumnIndex("KEY_COLOR")),
                    cursor.getString(cursor.getColumnIndex("KEY_TITLE")),
                    cursor.getString(cursor.getColumnIndex("KEY_TEXT"))
            )

            mArrayList.add(notification)

            cursor.moveToNext()

        }
        cursor.close()

        val mAdapter = NotiAdapter(mArrayList, this)
        list_main_recycler.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

    }

    // edit or delete notification
    fun notiClicked(pos: Int) {

        val cursor = db!!.rawQuery("SELECT * FROM $TABLE_NAME", null)
        cursor.moveToFirst()
        for (i in 0 until pos) cursor.moveToNext()
        val notiId = cursor.getLong(cursor.getColumnIndex("_id"))
        val title = cursor.getString(cursor.getColumnIndex("KEY_TITLE"))
        val text = cursor.getString(cursor.getColumnIndex("KEY_TEXT"))
        val color = cursor.getString(cursor.getColumnIndex("KEY_COLOR"))
        cursor.close()

        // choose
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilder.setItems(arrayOf(getString(R.string.main_modify), getString(R.string.main_delete))) { dialog, id ->

            if (id == 0) { // edit

                val editIntent = Intent(this@MainActivity, EditDialogActivity::class.java)
                editIntent.putExtra("title", title)
                editIntent.putExtra("text", text)
                editIntent.putExtra("color", color)
                editIntent.putExtra("noti_id", notiId)
                startActivityForResult(editIntent, 2)

            } else if (id == 1) { // delete

                // delete notification
                val deleteIntent = Intent(this@MainActivity, NotiService::class.java)
                deleteIntent.putExtra("id", (-1 * notiId).toString())
                startService(deleteIntent)

                // delete database
                db!!.execSQL("DELETE FROM $TABLE_NAME WHERE _id = $notiId;")
                setList()
                Snackbar.make(findViewById(R.id.layout_main), getString(R.string.alert_deleted), Snackbar.LENGTH_SHORT).setAction("Action", null).show()

            }

        }

        alertDialogBuilder.create().show()

    }

}
