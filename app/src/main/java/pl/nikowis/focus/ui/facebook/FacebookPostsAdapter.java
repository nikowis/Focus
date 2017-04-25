package pl.nikowis.focus.ui.facebook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 3/17/2017.
 */
public class FacebookPostsAdapter extends RecyclerView.Adapter<FacebookPostViewHolder> {

    private List<FacebookPost> list;
    private Context context;

    public FacebookPostsAdapter(List<FacebookPost> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public FacebookPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_media_item, null);
        FacebookPostViewHolder shoppingViewHolder = new FacebookPostViewHolder(view);
        return shoppingViewHolder;
    }

    @Override
    public void onBindViewHolder(FacebookPostViewHolder holder, int position) {
        FacebookPost shoppingItem = list.get(position);
        holder.mTitleView.setText(shoppingItem.getTitle());
        holder.mDescriptionView.setText(shoppingItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
