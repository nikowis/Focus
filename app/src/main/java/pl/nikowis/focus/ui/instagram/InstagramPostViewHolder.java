package pl.nikowis.focus.ui.instagram;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class InstagramPostViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.instagram_item_title)
    protected TextView mTitleView;
    @BindView(R.id.instagram_item_description)
    protected TextView mDescriptionView;
    @BindView(R.id.instagram_item_date)
    protected TextView mDateView;
    @BindView(R.id.instagram_item_url)
    protected TextView url;

    public InstagramPostViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
