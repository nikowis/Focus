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

    public FacebookPostsAdapter( Context context) {
        this.context = context;
        list = new ArrayList<>(100);
        ft = new SimpleDateFormat(context.getString(R.string.display_date_format));
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
        holder.mTitleView.setText(shoppingItem.getPageName());
        holder.mDescriptionView.setText(shoppingItem.getDescription());
        if(shoppingItem.getDate()!= null) {
            holder.mDateView.setText(ft.format(shoppingItem.getDate()));
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

    public void setList(List<FacebookPost> list) {
        this.list = list;
    }
}
