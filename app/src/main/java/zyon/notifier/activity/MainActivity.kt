package zyon.notifier.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import zyon.notifier.notification.NotificationDAO
import zyon.notifier.notification.Notification
import zyon.notifier.service.ReviveService
import java.util.*

class MainActivity: AppCompatActivity() {

    companion object {

        const val ACTIVITY_ADD = 0
        const val ACTIVITY_EDIT = 1

        private val adapter = NotificationAdapter()

        fun refresh() {

            val notificationList: ArrayList<Notification> = NotificationDAO.getNotificationList()
            adapter.setList(notificationList)
            adapter.notifyDataSetChanged()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        NotificationDAO.initDB(application)

        initUI()
        refresh()
        setEmptyText()

        // revive notifications
        startService(Intent(this, ReviveService::class.java))

    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if(requestCode == ACTIVITY_ADD) {

                Toast.makeText(this, getString(R.string.alert_added), Toast.LENGTH_SHORT).show()

                setEmptyText()

            } else if(requestCode == ACTIVITY_EDIT) {

                Toast.makeText(this, getString(R.string.alert_modified), Toast.LENGTH_SHORT).show()

            }

        } else {

            Toast.makeText(this, getString(R.string.alert_canceled), Toast.LENGTH_SHORT).show()

        }

    }

    private fun initUI() {

        // add button
        main_fab.setOnClickListener {

            val addDialogIntent = Intent(this@MainActivity, AddDialogActivity::class.java)
            startActivityForResult(addDialogIntent, ACTIVITY_ADD)

        }

        // recycler view
        main_recycler.layoutManager = LinearLayoutManager(this)
        main_recycler.addItemDecoration(DividerItemDecoration(main_recycler.context, DividerItemDecoration.VERTICAL))
        main_recycler.adapter = adapter

    }

    fun setEmptyText() {

        if(adapter.itemCount == 0) {

            main_text_empty.visibility = View.VISIBLE
            main_container.visibility = View.GONE

        } else {

            main_text_empty.visibility = View.GONE
            main_container.visibility = View.VISIBLE

        }

    }

}
