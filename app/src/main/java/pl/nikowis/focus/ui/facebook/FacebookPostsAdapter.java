package pl.nikowis.focus.ui.facebook;

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
 * Created by Nikodem on 3/17/2017.
 */
public class FacebookPostsAdapter extends RecyclerView.Adapter<FacebookPostViewHolder> {

    private List<FacebookPost> list;
    private Context context;
    private SimpleDateFormat ft;
    private View.OnLongClickListener itemClickListener;

    public FacebookPostsAdapter(Context context,View.OnLongClickListener itemClickListener) {
        this.context = context;
        list = new ArrayList<>(100);
        ft = new SimpleDateFormat(context.getString(R.string.display_date_format));
        this.itemClickListener = itemClickListener;
    }

    @Override
    public FacebookPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_facebook_post_item, null);
        FacebookPostViewHolder postViewHolder = new FacebookPostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(FacebookPostViewHolder holder, int position) {
        FacebookPost post = list.get(position);
        holder.itemView.setOnLongClickListener(itemClickListener);
        holder.mTitleView.setText(post.getPageName());
        holder.id.setText(post.getId());
        holder.mDescriptionView.setText(post.getDescription());
        if(post.getDate()!= null) {
            holder.mDateView.setText(ft.format(post.getDate()));
        } else {
            holder.mDateView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public List<FacebookPost> getList() {
        return list;
    }

}
