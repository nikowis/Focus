package pl.nikowis.focus.ui.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.base.SettingsActivity;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class InstagramFragment extends Fragment {

    @BindView(R.id.instagram_post_list)
    RecyclerView recyclerView;
    @BindView(R.id.instagram_login_button)
    Button loginButton;
    @BindView(R.id.instagram_fab_load_more)
    FloatingActionButton loadMorePostsButton;

    public static final String CLIENT_ID = "f4ea7842b9254c64804f34acb28a5fe9";
    public static final String CLIENT_SECRET = "11e41b6c8e94435fa6e4cff05b10957f";
    public static final String CALLBACK_URL = "redirect uri here";

    private InstagramFeedLoader instagramFeedLoader;
    private InstagramPostAdapter instagramAdapter;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        instagramAdapter = new InstagramPostAdapter(getContext(), new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        instagramFeedLoader = new InstagramFeedLoader();

        View mainFragment = inflater.inflate(R.layout.fragment_instagram, container, false);
        unbinder = ButterKnife.bind(this, mainFragment);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(instagramAdapter);
        return mainFragment;
    }


    @OnClick(R.id.instagram_fab_load_more)
    public void loadContent() {
        instagramFeedLoader.loadContent();
    }

    @OnClick(R.id.instagram_fab_go_to_settings)
    public void goToSettings() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
