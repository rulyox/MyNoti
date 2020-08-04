package zyon.notifier.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import zyon.notifier.R
import zyon.notifier.adapter.NotificationAdapter
import zyon.notifier.adapter.NotificationClickListener
import zyon.notifier.notification.Notification
import zyon.notifier.notification.NotificationDAO
import zyon.notifier.service.NotificationService
import zyon.notifier.service.ReviveService
import java.util.*

class MainActivity: AppCompatActivity() {

    companion object {

        const val ACTIVITY_ADD = 0
        const val ACTIVITY_EDIT = 1
        const val DIALOG_CHOOSE_EDIT = 0
        const val DIALOG_CHOOSE_DELETE = 1

    }

    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        // init dao and db
        NotificationDAO.initDB(application)

        initAdapter()
        initUI()

        // revive notifications
        startService(Intent(this, ReviveService::class.java))

    }

    override fun onResume() {
        super.onResume()

        // update notifications added with quick add
        refreshAdapter()
        setEmptyText()

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

                refreshAdapter()
                setEmptyText()

            } else if(requestCode == ACTIVITY_EDIT) {

                Toast.makeText(this, getString(R.string.alert_modified), Toast.LENGTH_SHORT).show()

                refreshAdapter()

            }

        } else {

            Toast.makeText(this, getString(R.string.alert_canceled), Toast.LENGTH_SHORT).show()

        }

    }

    private fun initAdapter() {

        val clickListener = object: NotificationClickListener {

            override fun onItemClick(position: Int, view: View) {

                val notificationList: ArrayList<Notification> = NotificationDAO.getNotificationList()
                val notification: Notification = notificationList[position]

                val alertDialogBuilder = AlertDialog.Builder(this@MainActivity, R.style.DialogTheme)
                alertDialogBuilder.setItems(arrayOf(this@MainActivity.getString(R.string.main_modify), this@MainActivity.getString(R.string.main_delete))) { _, id ->

                    if (id == DIALOG_CHOOSE_EDIT) {

                        // start dialog activity
                        val editIntent = Intent(this@MainActivity, EditDialogActivity::class.java)
                        editIntent.putExtra("position", position)
                        editIntent.putExtra("id", notification.id)
                        editIntent.putExtra("title", notification.title)
                        editIntent.putExtra("text", notification.text)
                        editIntent.putExtra("color", notification.color)
                        startActivityForResult(editIntent, ACTIVITY_EDIT)

                    } else if (id == DIALOG_CHOOSE_DELETE) {

                        // delete notification
                        val deleteIntent = Intent(this@MainActivity, NotificationService::class.java)
                        deleteIntent.putExtra("action", "remove")
                        deleteIntent.putExtra("id", notification.id)
                        startService(deleteIntent)

                        // delete database
                        NotificationDAO.deleteNotification(notification.id)

                        refreshAdapter()
                        setEmptyText()

                        Toast.makeText(this@MainActivity, this@MainActivity.getString(R.string.alert_deleted), Toast.LENGTH_SHORT).show()

                    }

                }

                alertDialogBuilder.create().show()

            }

        }

        adapter = NotificationAdapter(clickListener)

        refreshAdapter()

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

        setEmptyText()

    }

    private fun refreshAdapter() {

        val notificationList: ArrayList<Notification> = NotificationDAO.getNotificationList()
        adapter.setList(notificationList)
        adapter.notifyDataSetChanged()

    }

    private fun setEmptyText() {

        if(adapter.itemCount == 0) {

            main_text_empty.visibility = View.VISIBLE
            main_container.visibility = View.GONE

        } else {

            main_text_empty.visibility = View.GONE
            main_container.visibility = View.VISIBLE

        }

    }

}
