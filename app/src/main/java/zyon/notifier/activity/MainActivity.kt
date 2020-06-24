package zyon.notifier.activity

import android.app.Activity
import android.content.Intent
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
import zyon.notifier.adapter.NotificationAdapterObject
import zyon.notifier.notification.DAO
import zyon.notifier.notification.Notification
import zyon.notifier.service.ReviveService
import java.util.*

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        initUI()
        setList()

        // revive notifications
        startService(Intent(this, ReviveService::class.java))

    }

    override fun onResume() {
        super.onResume()

        setList()

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

            setList()

            if(requestCode == 1) Toast.makeText(this, getString(R.string.alert_added), Toast.LENGTH_SHORT).show()
            else if(requestCode == 2) Toast.makeText(this, getString(R.string.alert_modified), Toast.LENGTH_SHORT).show()

        } else {

            Toast.makeText(this, getString(R.string.alert_canceled), Toast.LENGTH_SHORT).show()

        }

    }

    private fun initUI() {

        // add button
        main_fab.setOnClickListener {

            val addDialogIntent = Intent(this@MainActivity, AddDialogActivity::class.java)
            startActivityForResult(addDialogIntent, 1)

        }

        // recycler view
        main_recycler.layoutManager = LinearLayoutManager(this)
        main_recycler.addItemDecoration(DividerItemDecoration(main_recycler.context, DividerItemDecoration.VERTICAL))

    }

    private fun setList() {

        val dao = DAO(this)
        val notificationList: ArrayList<Notification> = dao.getNotificationList()

        NotificationAdapterObject.init(notificationList)
        main_recycler.adapter = NotificationAdapterObject.get()
        NotificationAdapterObject.refresh()

        setEmptyText()

    }

    fun setEmptyText() {

        if(NotificationAdapterObject.get().itemCount == 0) {

            main_text_empty.visibility = View.VISIBLE
            main_container.visibility = View.GONE

        } else {

            main_text_empty.visibility = View.GONE
            main_container.visibility = View.VISIBLE

        }

    }

}
