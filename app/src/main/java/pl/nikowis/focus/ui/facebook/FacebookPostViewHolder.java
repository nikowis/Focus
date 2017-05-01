package pl.nikowis.focus.ui.facebook;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 3/17/2017.
 */
public class FacebookPostViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.facebook_item_title)
    protected TextView mTitleView;
    @BindView(R.id.facebook_item_description)
    protected TextView mDescriptionView;
    @BindView(R.id.facebook_item_date)
    protected TextView mDateView;
    @BindView(R.id.facebook_item_id)
    protected TextView id;

    public FacebookPostViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
