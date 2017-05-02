package pl.nikowis.focus.ui.twitter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class TwitterPostsAdapter extends RecyclerView.Adapter<TwitterPostViewHolder> {

    private List<TwitterPost> list;
    private Context context;
    private SimpleDateFormat ft;
    private View.OnLongClickListener itemClickListener;

    public TwitterPostsAdapter(Context context, View.OnLongClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        list = new ArrayList<>();
        ft = new SimpleDateFormat(context.getString(R.string.display_date_format));
    }

    @Override
    public TwitterPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_twitter_post_item, null);
        TwitterPostViewHolder postViewHolder = new TwitterPostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(TwitterPostViewHolder holder, int position) {
        TwitterPost post = list.get(position);
        holder.itemView.setOnLongClickListener(itemClickListener);
        holder.mTitleView.setText(post.getTitle());
        holder.url.setText(post.getLink());
        holder.mDescriptionView.setText(post.getDescription());
        holder.mDateView.setText(ft.format(post.getDate()));
        holder.itemView.setOnLongClickListener(itemClickListener);

        Glide.with(context)
                .load(post.getThumbnail().url)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public List<TwitterPost> getList() {
        return list;
    }
}
