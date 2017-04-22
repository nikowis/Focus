package pl.nikowis.focus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Nikodem on 3/17/2017.
 */
public class MediaFacebookAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<MediaItem> list;
    private Context context;

    public MediaFacebookAdapter(List<MediaItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_media_item, null);
        ItemViewHolder shoppingViewHolder = new ItemViewHolder(view);
        return shoppingViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        MediaItem shoppingItem = list.get(position);
        holder.mTitleView.setText(shoppingItem.getTitle());
        holder.mDescriptionView.setText(shoppingItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
