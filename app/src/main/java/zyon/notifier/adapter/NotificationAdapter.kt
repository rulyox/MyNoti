package zyon.notifier.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import zyon.notifier.R
import zyon.notifier.notification.Notification
import java.util.*

class NotificationAdapter(val clickListener: NotificationClickListener): RecyclerView.Adapter<NotificationViewHolder>() {

    private var notificationList: ArrayList<Notification>? = null

    fun setList(notificationList: ArrayList<Notification>) {

        this.notificationList = notificationList

    }

    override fun getItemCount(): Int {

        return notificationList?.size ?: 0

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NotificationViewHolder {

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item, viewGroup, false)

        return NotificationViewHolder(this, view)

    }

    override fun onBindViewHolder(viewholder: NotificationViewHolder, position: Int) {

        if(notificationList == null) return

        val notification = notificationList!![position]

        val previewBackground = viewholder.color.background as GradientDrawable
        previewBackground.setColor(Color.parseColor(notification.color))

        viewholder.title.text = notification.title
        viewholder.text.text = notification.text

    }

}
