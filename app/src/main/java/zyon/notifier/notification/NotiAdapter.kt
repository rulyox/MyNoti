package zyon.notifier.notification

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import java.util.*
import java.lang.ref.WeakReference

import zyon.notifier.MainActivity
import zyon.notifier.R

class NotiAdapter(private val mList: ArrayList<Notification>?, context: Context) : RecyclerView.Adapter<NotiAdapter.CustomViewHolder>() {

    private val mContextWeakReference = WeakReference(context)

    inner class CustomViewHolder(view: View, context: Context?) : RecyclerView.ViewHolder(view) {

        private var parent: LinearLayout = view.findViewById(R.id.list_main)
        val color: View = view.findViewById(R.id.list_main_color)
        val title: TextView = view.findViewById(R.id.list_main_title)
        val text: TextView = view.findViewById(R.id.list_main_text)

        init {

            // click anywhere
            parent.setOnClickListener {

                (context as MainActivity?)!!.Edit(adapterPosition)

            }

        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {

        val context = mContextWeakReference.get()
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_main, viewGroup, false)

        return CustomViewHolder(view, context)

    }

    override fun onBindViewHolder(viewholder: CustomViewHolder, position: Int) {

        viewholder.color.setBackgroundColor(Color.parseColor(mList!![position].color))
        viewholder.title.text = mList[position].title
        viewholder.text.text = mList[position].text

    }

    override fun getItemCount(): Int { return mList?.size ?: 0 }

}
