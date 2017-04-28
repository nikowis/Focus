package pl.nikowis.focus.ui.facebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.base.SettingsActivity;
import pl.nikowis.focus.ui.base.SettingsFragment;


/**
 * Created by Nikodem on 4/22/2017.
 */

public class FacebookFragment extends Fragment {

    @BindView(R.id.shopping_list)
    RecyclerView recyclerView;
    @BindView(R.id.facebook_login_button)
    LoginButton loginButton;

    private FacebookFeedLoader facebookFeedLoader;
    private FacebookPostsAdapter facebookAdapter;
    private Unbinder unbinder;
    private CallbackManager callbackManager;
    private Profile currentProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        facebookAdapter = new FacebookPostsAdapter(getContext());

        facebookFeedLoader = new FacebookFeedLoader(getContext(), facebookAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals(SettingsFragment.KEY_PREF_SELECTED_CUSTOM_PAGES)) {
                    facebookFeedLoader = new FacebookFeedLoader(getContext(), facebookAdapter);
                }
            }
        });

        View mainFragment = inflater.inflate(R.layout.fragment_facebook, container, false);
        unbinder = ButterKnife.bind(this, mainFragment);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile", "user_posts", "user_likes");
        loginButton.setFragment(this);
        currentProfile = Profile.getCurrentProfile();
        if (currentProfile != null) {
            loginButton.setVisibility(View.GONE);
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginButton.setVisibility(View.GONE);
                Toast.makeText(getContext(), getString(R.string.fb_login_success_toast) + loginResult.getAccessToken().toString(), Toast.LENGTH_SHORT).show();
                FacebookLikesLoader likesLoader = new FacebookLikesLoader(getContext());
                likesLoader.loadAllLikes();
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

    @OnClick(R.id.facebook_fab_load_more)
    public void loadContent() {
        facebookFeedLoader.loadContent();
    }

    @OnClick(R.id.facebook_fab_add_pages)
    public void addNewPage() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
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
