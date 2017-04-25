package pl.nikowis.focus.ui.facebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.facebook.FacebookRequestManager;
import pl.nikowis.focus.rest.facebook.FbFeedDataResponse;
import pl.nikowis.focus.ui.base.SettingsActivity;
import pl.nikowis.focus.ui.base.SettingsFragment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class FacebookFragment extends Fragment {

    @BindView(R.id.shopping_list)
    RecyclerView recyclerView;
    @BindView(R.id.facebook_login_button)
    LoginButton loginButton;

    private List<FacebookPost> postsList;
    private List<String> pagesList;
    private FacebookPostsAdapter facebookAdapter;
    private Unbinder unbinder;
    private CallbackManager callbackManager;
    private Profile currentProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        postsList = new ArrayList<>();
        pagesList = new ArrayList<>();

        View mainFragment = inflater.inflate(R.layout.fragment_facebook, container, false);
        unbinder = ButterKnife.bind(this, mainFragment);

        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email", "public_profile", "user_posts", "user_likes");
        loginButton.setFragment(this);

        currentProfile = Profile.getCurrentProfile();
        if (currentProfile != null) {
            loginButton.setVisibility(View.GONE);
            postsList.add(new FacebookPost(currentProfile.getFirstName(), currentProfile.getLastName()));
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginButton.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.fb_login_success_toast) + loginResult.getAccessToken().toString(), Toast.LENGTH_SHORT).show();
                postsList.add(new FacebookPost(currentProfile.getFirstName(), currentProfile.getLastName()));
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

        facebookAdapter = new FacebookPostsAdapter(postsList, getActivity());

        recyclerView.setAdapter(facebookAdapter);
        return mainFragment;
    }

    @OnClick(R.id.facebook_fab_load_more)
    public void loadContent() {
        Bundle params = new Bundle();
        params.putString("with", "location");
        FacebookRequestManager requestManager = FacebookRequestManager.getInstance(getContext());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> pages = prefs.getStringSet(SettingsFragment.KEY_PREF_SELECTED_PAGES, new HashSet<String>());

        for(String page : pages) {
            requestPagePosts(requestManager, page);
        }

    }

    private void requestPagePosts(FacebookRequestManager requestManager, final String page) {
        requestManager.getPageFeed(page, AccessToken.getCurrentAccessToken().getToken(), new Callback<FbFeedDataResponse>() {
            @Override
            public void success(FbFeedDataResponse fbFeedDataResponse, Response response) {
                Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                Log.w("asf", fbFeedDataResponse.toString());
                for(FbFeedDataResponse.FbSinglePostResponse res : fbFeedDataResponse.fbSinglePostResponses) {
                    postsList.add(new FacebookPost(page, res.message));
                }
                facebookAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getContext(), "REST ERROR", Toast.LENGTH_SHORT).show();
                Log.w("REST ERROR", error.getUrl());
                Log.w("REST ERROR", error.getMessage());
            }
        });
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
