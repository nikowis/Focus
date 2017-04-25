package pl.nikowis.focus.ui.facebook;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.facebook.FacebookRequestManager;
import pl.nikowis.focus.rest.facebook.FbSinglePostResponse;
import pl.nikowis.focus.rest.facebook.FbFeedDataResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class FacebookFragment extends Fragment {

    private List<FacebookPost> list;
    @BindView(R.id.shopping_list)
    RecyclerView recyclerView;
    private FacebookPostsAdapter facebookAdapter;
    private Unbinder unbinder;
    @BindView(R.id.facebook_login_button)
    LoginButton loginButton;
    CallbackManager callbackManager;
    private Profile currentProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        list = new ArrayList<>();
        View mainFragment = inflater.inflate(R.layout.fragment_facebook, container, false);
        unbinder = ButterKnife.bind(this, mainFragment);

        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email", "public_profile", "user_posts");
        loginButton.setFragment(this);

        currentProfile = Profile.getCurrentProfile();
        if (currentProfile != null) {
            loginButton.setVisibility(View.GONE);
            list.add(new FacebookPost(currentProfile.getFirstName(), currentProfile.getLastName()));
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginButton.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.fb_login_success_toast) + loginResult.getAccessToken().toString(), Toast.LENGTH_SHORT).show();
                list.add(new FacebookPost(currentProfile.getFirstName(), currentProfile.getLastName()));
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

        facebookAdapter = new FacebookPostsAdapter(list, getActivity());

        recyclerView.setAdapter(facebookAdapter);
        return mainFragment;
    }

    @OnClick(R.id.facebook_fab_load_more)
    public void load() {
        Bundle params = new Bundle();
        params.putString("with", "location");
        FacebookRequestManager requestManager = FacebookRequestManager.getInstance(getContext());
        requestManager.getPageFeed("Quebonafide", AccessToken.getCurrentAccessToken().getToken(), new Callback<FbFeedDataResponse>() {
            @Override
            public void success(FbFeedDataResponse fbFeedDataResponse, Response response) {
                Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                Log.w("asf", fbFeedDataResponse.toString());
                for(FbSinglePostResponse res : fbFeedDataResponse.fbSinglePostResponses) {
                    list.add(new FacebookPost(" ", res.toString()));
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