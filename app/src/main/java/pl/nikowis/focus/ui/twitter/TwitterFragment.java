package pl.nikowis.focus.ui.twitter;

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
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.twitter.TwitterRequestManager;
import pl.nikowis.focus.ui.base.SettingsActivity;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class TwitterFragment extends Fragment {

    @BindView(R.id.twitter_post_list)
    RecyclerView recyclerView;
    @BindView(R.id.twitter_login_button)
    TwitterLoginButton loginButton;
    @BindView(R.id.twitter_fab_load_more)
    FloatingActionButton loadMorePostsButton;

    private TwitterFeedLoader twitterFeedLoader;
    private TwitterPostsAdapter twitterAdapter;
    private Unbinder unbinder;
    private String authToken;
    private static Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedTwitternceState) {
        context = getContext();

        twitterAdapter = new TwitterPostsAdapter(getContext(), new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    TextView urlTextView = (TextView) v.findViewById(R.id.twitter_item_id);
                    String id = urlTextView.getText().toString();
                    Uri uri = Uri.parse(TwitterRequestManager.TWITTER_BASE_ID_URL + id);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("ERROR twitter intent :", e.getMessage());
                }
                return true;
            }
        });
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        authToken = prefs.getString(context.getString(R.string.key_pref_twitter_auth_token), null);

        if (authToken != null) {
            resetTwitterFeedLoader();
        }

        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(context.getString(R.string.key_pref_twitter_logout))) {
                    loginButton.setVisibility(View.VISIBLE);
                    loadMorePostsButton.setVisibility(View.GONE);
                    resetTwitterFeedLoader();
                } else if (key.equals(context.getString(R.string.key_pref_twitter_page_count))) {
                    resetTwitterFeedLoader();
                }
            }
        });

        View mainFragment = inflater.inflate(R.layout.fragment_twitter, container, false);
        unbinder = ButterKnife.bind(this, mainFragment);

        loginButton.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                Log.e("TWITTER LOGIN ######", "token:" + token + "  ;  secret:" + secret);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                prefs.edit().putString(context.getString(R.string.key_pref_twitter_auth_token), token).apply();
                prefs.edit().putString(context.getString(R.string.key_pref_twitter_auth_token_secret), secret).apply();
                twitterAdapter.notifyDataSetChanged();
                loginButton.setVisibility(View.GONE);
                resetTwitterFeedLoader();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getContext(), R.string.twitter_login_failed, Toast.LENGTH_SHORT).show();
            }
        });

        loadMorePostsButton.setVisibility(View.GONE);
        if (authToken != null) {
            loginButton.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(twitterAdapter);
        return mainFragment;
    }


    @OnClick(R.id.twitter_fab_load_more)
    public void loadContent() {
        twitterFeedLoader.loadContent();
    }

    @OnClick(R.id.twitter_fab_go_to_settings)
    public void goToSettings() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void resetTwitterFeedLoader() {
        twitterFeedLoader = new TwitterFeedLoader(context, twitterAdapter, new TwitterFeedLoader.ContentLoaderEventsListener() {
            @Override
            public void readyToDisplay() {
                Toast.makeText(context, R.string.loader_ready, Toast.LENGTH_SHORT).show();
                if (authToken != null && loadMorePostsButton != null) {
                    loadMorePostsButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void loadingMoreData() {
                Toast.makeText(getActivity(), R.string.loader_loading, Toast.LENGTH_SHORT).show();
                if (loadMorePostsButton != null) {
                    loadMorePostsButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (loginButton != null) {
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
