package pl.nikowis.focus.ui.instagram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.instagram.InstaLoginResponse;
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
    private InstagramPostsAdapter instagramAdapter;
    private Unbinder unbinder;
    private String authToken;
    private boolean usingCustomPages;
    private Set<String> selectedPages;
    private static Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        instagramAdapter = new InstagramPostsAdapter(getContext(), new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    TextView urlTextView = (TextView) v.findViewById(R.id.instagram_item_url);
                    String url = urlTextView.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("ERROR insta intent :", e.getMessage());
                }
                return true;
            }
        });
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        authToken = prefs.getString(InstagramSettings.KEY_PREF_INSTAGRAM_AUTH_TOKEN, null);

        if(authToken!=null) {
            resetInstragramFeedLoader();
        }


        usingCustomPages = prefs.getBoolean(InstagramSettings.KEY_PREF_USING_CUSTOM_USERS, false);
        if (!usingCustomPages) {
            selectedPages = prefs.getStringSet(InstagramSettings.KEY_PREF_SELECTED_USERS, new HashSet<String>());
        } else {
            selectedPages = prefs.getStringSet(InstagramSettings.KEY_PREF_SELECTED_CUSTOM_USERS, new HashSet<String>());
        }
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(InstagramSettings.KEY_PREF_SELECTED_CUSTOM_USERS)) {
                    resetInstragramFeedLoader();
                    instagramAdapter.getList().clear();
                    instagramAdapter.notifyDataSetChanged();
                    selectedPages = prefs.getStringSet(InstagramSettings.KEY_PREF_SELECTED_CUSTOM_USERS, new HashSet<String>());
                } else if (key.equals(InstagramSettings.KEY_PREF_SELECTED_USERS)
                        || key.equals(InstagramSettings.KEY_PREF_USING_CUSTOM_USERS)) {
                    selectedPages = prefs.getStringSet(InstagramSettings.KEY_PREF_SELECTED_USERS, new HashSet<String>());
                    instagramAdapter.getList().clear();
                    instagramAdapter.notifyDataSetChanged();
                } else if (key.equals(InstagramSettings.KEY_PREF_LOGOUT)) {
                    loginButton.setVisibility(View.VISIBLE);
                    loadMorePostsButton.setVisibility(View.GONE);
                    resetInstragramFeedLoader();
                }
            }
        });

        View mainFragment = inflater.inflate(R.layout.fragment_instagram, container, false);
        unbinder = ButterKnife.bind(this, mainFragment);

        loadMorePostsButton.setVisibility(View.GONE);
        if (authToken != null) {
            loginButton.setVisibility(View.GONE);
        }

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
        requestManager.login(getContext(), new Callback<InstaLoginResponse>() {
            @Override
            public void onResponse(Call<InstaLoginResponse> call, Response<InstaLoginResponse> response) {
                InstaLoginResponse body = response.body();
                authToken = body.accessToken;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                prefs.edit().putString(InstagramSettings.KEY_PREF_INSTAGRAM_AUTH_TOKEN, authToken).apply();
                prefs.edit().putString(InstagramSettings.KEY_PREF_INSTAGRAM_USER_ID, body.user.id).apply();
                instagramAdapter.notifyDataSetChanged();
                new InstagramFollowsLoader(context).loadAllFollows();
                loginButton.setVisibility(View.GONE);
                resetInstragramFeedLoader();
            }

            @Override
            public void onFailure(Call<InstaLoginResponse> call, Throwable t) {

            }
        });
    }

    private void resetInstragramFeedLoader() {
        instagramFeedLoader = new InstagramFeedLoader(context, instagramAdapter, new InstagramFeedLoader.ContentLoaderEventsListener() {
            @Override
            public void readyToDisplay() {
                Toast.makeText(context, "Ready!", Toast.LENGTH_SHORT).show();
                if (authToken != null && loadMorePostsButton != null) {
                    loadMorePostsButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void loadingMoreData() {
                Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();
                if (loadMorePostsButton != null) {
                    loadMorePostsButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
