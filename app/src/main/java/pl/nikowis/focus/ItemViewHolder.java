package pl.nikowis.focus;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nikodem on 3/17/2017.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_title)
    protected TextView mTitleView;
    @BindView(R.id.item_description)
    protected TextView mDescriptionView;

    public ItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
