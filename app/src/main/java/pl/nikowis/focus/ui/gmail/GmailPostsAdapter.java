package pl.nikowis.focus.ui.gmail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class GmailPostsAdapter extends RecyclerView.Adapter<GmailPostViewHolder> {

    private List<GmailMessage> list;
    private Context context;
    private SimpleDateFormat ft;
    private View.OnLongClickListener itemClickListener;

    public GmailPostsAdapter(Context context, View.OnLongClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        list = new ArrayList<>();
        ft = new SimpleDateFormat(context.getString(R.string.display_date_format));
    }

    @Override
    public GmailPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gmail_post_item, null);
        GmailPostViewHolder postViewHolder = new GmailPostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(GmailPostViewHolder holder, int position) {

        GmailMessage post = list.get(position);
        holder.itemView.setOnLongClickListener(itemClickListener);
        holder.mTitleView.setText(post.getTitle());
        holder.id.setText(post.getId());
        holder.mDescriptionView.setText(post.getDescription());
        if(post.getDate()!= null) {
            holder.mDateView.setText(ft.format(post.getDate()));
        }
        holder.itemView.setOnLongClickListener(itemClickListener);
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public List<GmailMessage> getList() {
        return list;
    }
}
