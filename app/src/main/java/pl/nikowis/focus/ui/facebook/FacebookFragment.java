package pl.nikowis.focus.ui.facebook;

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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.base.SettingsActivity;


/**
 * Created by Nikodem on 4/22/2017.
 */

public class FacebookFragment extends Fragment {

    @BindView(R.id.facebook_post_list)
    RecyclerView recyclerView;
    @BindView(R.id.facebook_login_button)
    LoginButton loginButton;
    @BindView(R.id.facebook_fab_load_more)
    FloatingActionButton loadMorePostsButton;

    private FacebookFeedLoader facebookFeedLoader;
    private FacebookPostsAdapter facebookAdapter;
    private Unbinder unbinder;
    private CallbackManager callbackManager;
    private Profile currentProfile;
    private boolean usingCustomPages;
    private Set<String> selectedPages;
    private static Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        facebookAdapter = new FacebookPostsAdapter(getActivity(), new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    TextView idTextView = (TextView) v.findViewById(R.id.facebook_item_id);
                    String id = idTextView.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://post/"+id));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("ERROR facebook intent :", e.getMessage());
                }
                return true;
            }
        });

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        usingCustomPages = prefs.getBoolean(context.getString(R.string.key_pref_facebook_using_custom_pages), false);
        if (!usingCustomPages) {
            selectedPages = prefs.getStringSet(context.getString(R.string.key_pref_facebook_selected_pages), new HashSet<String>());
        } else {
            selectedPages = prefs.getStringSet(context.getString(R.string.key_pref_facebook_selected_custom_pages), new HashSet<String>());
        }
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(context.getString(R.string.key_pref_facebook_selected_custom_pages))) {
                    resetFacebookFeedLoader();
                    facebookAdapter.getList().clear();
                    facebookAdapter.notifyDataSetChanged();
                    selectedPages = prefs.getStringSet(context.getString(R.string.key_pref_facebook_selected_custom_pages), new HashSet<String>());
                } else if (key.equals(context.getString(R.string.key_pref_facebook_selected_pages))
                        || key.equals(context.getString(R.string.key_pref_facebook_using_custom_pages))) {
                    selectedPages = prefs.getStringSet(context.getString(R.string.key_pref_facebook_selected_pages), new HashSet<String>());
                    facebookAdapter.getList().clear();
                    facebookAdapter.notifyDataSetChanged();
                } else if (key.equals(context.getString(R.string.key_pref_facebook_logout))) {
                    loginButton.setVisibility(View.VISIBLE);
                    loadMorePostsButton.setVisibility(View.GONE);
                    resetFacebookFeedLoader();
                } else if (key.equals(context.getString(R.string.key_pref_facebook_page_count))) {
                    resetFacebookFeedLoader();
                }
            }
        });

        View mainFragment = inflater.inflate(R.layout.fragment_facebook, container, false);
        unbinder = ButterKnife.bind(this, mainFragment);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile", "user_posts", "user_likes");
        loginButton.setFragment(this);
        currentProfile = Profile.getCurrentProfile();
        loadMorePostsButton.setVisibility(View.GONE);
        if (currentProfile != null) {
            resetFacebookFeedLoader();
            loginButton.setVisibility(View.GONE);
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginButton.setVisibility(View.GONE);
                Toast.makeText(context, getString(R.string.fb_login_success_toast), Toast.LENGTH_SHORT).show();
                resetFacebookFeedLoader();
                new FacebookLikesLoader(context).loadAllLikes();
            }

            @Override
            public void onCancel() {
                //nothing
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getActivity(), R.string.fb_login_error_toast, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(facebookAdapter);
        return mainFragment;
    }

    private void resetFacebookFeedLoader() {
        facebookFeedLoader = new FacebookFeedLoader(context, facebookAdapter, new FacebookFeedLoader.ContentLoaderEventsListener() {
            @Override
            public void readyToDisplay() {
                if(currentProfile != null && loadMorePostsButton != null) {
                    loadMorePostsButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void loadingMoreData() {
                Toast.makeText(getActivity(), R.string.loader_loading, Toast.LENGTH_SHORT).show();
                if(loadMorePostsButton!=null){
                    loadMorePostsButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick(R.id.facebook_fab_load_more)
    public void loadContent() {
        facebookFeedLoader.loadContent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
