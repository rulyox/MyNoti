package zyon.notifier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Adapter_Info extends RecyclerView.Adapter<Adapter_Info.CustomViewHolder> {

    private ArrayList<List_Info> mList;
    WeakReference<Context> mContextWeakReference;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView image;
        protected TextView title;
        protected TextView text;
        protected LinearLayout parent;

        public CustomViewHolder(View view, final Context context) {
            super(view);

            this.image = view.findViewById(R.id.list_info_image);
            this.title = view.findViewById(R.id.list_info_title);
            this.text = view.findViewById(R.id.list_info_text);
            this.parent = view.findViewById(R.id.list_info);

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( getAdapterPosition() == 2 ){
                        ((Activity_Menu_Info) context).clickList(2);
                    }

                }
            });

        }

    }

    public Adapter_Info(ArrayList<List_Info> list, Context context) {

        this.mList = list;
        this.mContextWeakReference = new WeakReference<Context>(context);

    }

    @Override
    public Adapter_Info.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = mContextWeakReference.get();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_info, viewGroup, false);
        Adapter_Info.CustomViewHolder viewHolder = new Adapter_Info.CustomViewHolder(view, context);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Info.CustomViewHolder viewholder, int position) {

        viewholder.image.setImageDrawable( mList.get(position).getImage() );
        viewholder.title.setText(mList.get(position).getTitle());
        viewholder.text.setText(mList.get(position).getText());

    }

    @Override
    public int getItemCount() { return (null != mList ? mList.size() : 0); }

}
