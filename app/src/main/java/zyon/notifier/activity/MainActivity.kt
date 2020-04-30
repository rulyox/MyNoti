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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import zyon.notifier.R
import zyon.notifier.adapter.NotificationAdapter
import zyon.notifier.notification.Database
import zyon.notifier.notification.Notification
import zyon.notifier.service.ReviveService
import java.util.*

class MainActivity : AppCompatActivity() {

    private val db: Database = Database(this)

    companion object {
        lateinit var notificationAdapter: NotificationAdapter
    }

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

        notificationAdapter = NotificationAdapter(mArrayList)
        list_main_recycler.adapter = notificationAdapter
        notificationAdapter.notifyDataSetChanged()

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

}
