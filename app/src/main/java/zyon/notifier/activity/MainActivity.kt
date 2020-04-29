package zyon.notifier.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import java.util.*

import kotlinx.android.synthetic.main.activity_main.*
import zyon.notifier.notification.Database
import zyon.notifier.R

import zyon.notifier.notification.NotificationAdapter
import zyon.notifier.notification.Notification
import zyon.notifier.service.NotificationService
import zyon.notifier.service.ReviveService

class MainActivity : AppCompatActivity() {

    private val db: Database = Database(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        registerReceiver(finishActivity, IntentFilter("FINISH_ACTIVITY"))

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

            if(requestCode == 1) Toast.makeText(this, getString(R.string.alert_added), Toast.LENGTH_SHORT).show()
            else if(requestCode == 2) Toast.makeText(this, getString(R.string.alert_modified), Toast.LENGTH_SHORT).show()

        } else {

            Toast.makeText(this, getString(R.string.alert_canceled), Toast.LENGTH_SHORT).show()

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

        showNoText()

        val mArrayList: ArrayList<Notification> = ArrayList()

        val cursor = db.selectAll()

        cursor.moveToFirst()

        for (i in 0 until cursor.count) {

            val notification = Notification(
                    cursor.getString(cursor.getColumnIndex("color")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("text"))
            )

            mArrayList.add(notification)

            cursor.moveToNext()

        }

        cursor.close()

        val mAdapter = NotificationAdapter(mArrayList, this)
        list_main_recycler.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

    }

    private fun showNoText() {

        val cursor = db.selectAll()

        cursor.moveToFirst()

        if(cursor.count == 0) {

            main_text_no_notifications.visibility = View.VISIBLE
            main_notifications_parent.visibility = View.GONE

        } else {

            main_text_no_notifications.visibility = View.GONE
            main_notifications_parent.visibility = View.VISIBLE

        }

        cursor.close()

    }

    // edit or delete notification
    fun notiClicked(pos: Int) {

        val cursor = db.selectAll()

        cursor.moveToFirst()

        for (i in 0 until pos) cursor.moveToNext()
        val notiId = cursor.getLong(cursor.getColumnIndex("id"))
        val title = cursor.getString(cursor.getColumnIndex("title"))
        val text = cursor.getString(cursor.getColumnIndex("text"))
        val color = cursor.getString(cursor.getColumnIndex("color"))

        cursor.close()

        // choose
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity, R.style.DialogTheme)
        alertDialogBuilder.setItems(arrayOf(getString(R.string.main_modify), getString(R.string.main_delete))) { dialog, id ->

            if (id == 0) { // edit

                val editIntent = Intent(this@MainActivity, EditDialogActivity::class.java)
                editIntent.putExtra("id", notiId)
                editIntent.putExtra("title", title)
                editIntent.putExtra("text", text)
                editIntent.putExtra("color", color)
                startActivityForResult(editIntent, 2)

            } else if (id == 1) { // delete

                // delete notification
                val deleteIntent = Intent(this@MainActivity, NotificationService::class.java)
                deleteIntent.putExtra("id", (-1 * notiId).toString())
                startService(deleteIntent)

                // delete database
                db.delete(notiId.toInt())
                setList()

                Toast.makeText(this, getString(R.string.alert_deleted), Toast.LENGTH_SHORT).show()

            }

        }

        alertDialogBuilder.create().show()

    }

}
