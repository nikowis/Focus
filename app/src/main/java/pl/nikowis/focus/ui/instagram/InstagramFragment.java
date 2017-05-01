package pl.nikowis.focus.ui.instagram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.instagram.InstagramLoginResponse;
import pl.nikowis.focus.rest.instagram.InstagramRequestManager;
import pl.nikowis.focus.ui.base.SettingsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private InstagramFeedLoader instagramFeedLoader;
    private InstagramPostAdapter instagramAdapter;
    private Unbinder unbinder;
    List<InstagramPost> list;
    private String accessToken;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        instagramAdapter = new InstagramPostAdapter(getContext(), new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        list = instagramAdapter.getList();

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

    @OnClick(R.id.instagram_login_button)
    public void loginInstagram() {
        InstagramRequestManager requestManager = InstagramRequestManager.getInstance(getContext());
        requestManager.login(new Callback<InstagramLoginResponse>() {
            @Override
            public void onResponse(Call<InstagramLoginResponse> call, Response<InstagramLoginResponse> response) {
                InstagramLoginResponse body = response.body();
                accessToken = body.accessToken;
                list.add(new InstagramPost(accessToken, body.user.fullName));
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                prefs.edit().putString(InstagramSettings.KEY_PREF_INSTAGRAM_AUTH_TOKEN, accessToken).apply();

                instagramAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<InstagramLoginResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
