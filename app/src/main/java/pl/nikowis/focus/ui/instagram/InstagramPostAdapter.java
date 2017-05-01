package pl.nikowis.focus.ui.instagram;

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

public class InstagramPostAdapter extends RecyclerView.Adapter<InstagramPostViewHolder> {

    private List<InstagramPost> list;
    private Context context;
    private SimpleDateFormat ft;
    private View.OnLongClickListener itemClickListener;

    public InstagramPostAdapter(Context context, View.OnLongClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        list = new ArrayList<>();
    }

    @Override
    public InstagramPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_instagram_post_item, null);
        InstagramPostViewHolder postViewHolder = new InstagramPostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(InstagramPostViewHolder holder, int position) {
        InstagramPost post = list.get(position);
        holder.itemView.setOnLongClickListener(itemClickListener);
        holder.mTitleView.setText(post.getTitle());
        holder.id.setText(post.getId());
        holder.mDescriptionView.setText(post.getDescription());
        holder.itemView.setOnLongClickListener(itemClickListener);

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public List<InstagramPost> getList() {
        return list;
    }
}
