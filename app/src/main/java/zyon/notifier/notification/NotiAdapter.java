package zyon.notifier.notification;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zyon.notifier.MainActivity;
import zyon.notifier.R;

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.CustomViewHolder> {

    private ArrayList<Notification> mList;
    WeakReference<Context> mContextWeakReference;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected View color;
        protected TextView title;
        protected TextView text;
        protected LinearLayout parent;

        public CustomViewHolder(View view, final Context context) {
            super(view);

            this.color = view.findViewById(R.id.list_main_color);
            this.title = view.findViewById(R.id.list_main_title);
            this.text = view.findViewById(R.id.list_main_text);
            this.parent = view.findViewById(R.id.list_main);

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) context).Edit( getAdapterPosition() );
                }
            });

        }

    }

    public NotiAdapter(ArrayList<Notification> list, Context context) {

        this.mList = list;
        this.mContextWeakReference = new WeakReference<Context>(context);

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = mContextWeakReference.get();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_main, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view, context);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.color.setBackgroundColor( Color.parseColor( mList.get(position).getColor() ) );
        viewholder.title.setText(mList.get(position).getTitle());
        viewholder.text.setText(mList.get(position).getText());

    }

    @Override
    public int getItemCount() { return (null != mList ? mList.size() : 0); }

}
