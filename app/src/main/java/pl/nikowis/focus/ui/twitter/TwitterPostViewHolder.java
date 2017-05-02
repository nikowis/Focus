package pl.nikowis.focus.ui.twitter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class TwitterPostViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.twitter_item_title)
    protected TextView mTitleView;
    @BindView(R.id.twitter_item_description)
    protected TextView mDescriptionView;
    @BindView(R.id.twitter_item_date)
    protected TextView mDateView;
    @BindView(R.id.twitter_item_url)
    protected TextView url;
    @BindView(R.id.twitter_item_image_view)
    protected ImageView imageView;

    public TwitterPostViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
