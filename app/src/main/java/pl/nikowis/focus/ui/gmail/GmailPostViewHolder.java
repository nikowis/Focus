package pl.nikowis.focus.ui.gmail;

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

public class GmailPostViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.gmail_item_title)
    protected TextView mTitleView;
    @BindView(R.id.gmail_item_description)
    protected TextView mDescriptionView;
    @BindView(R.id.gmail_item_date)
    protected TextView mDateView;
    @BindView(R.id.gmail_item_id)
    protected TextView id;

    public GmailPostViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
