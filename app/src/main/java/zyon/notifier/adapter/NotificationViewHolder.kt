package zyon.notifier.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import zyon.notifier.R

class NotificationViewHolder(adapter: NotificationAdapter, view: View): RecyclerView.ViewHolder(view) {

    val title by lazy { view.findViewById(R.id.item_title) as TextView }
    val text by lazy { view.findViewById(R.id.item_text) as TextView }
    val color by lazy { view.findViewById(R.id.item_color) as View }

    init {

        view.setOnClickListener {

            adapter.clickListener.onItemClick(adapterPosition, it)

        }

    }

}
