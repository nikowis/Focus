package pl.nikowis.focus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class FacebookFragment extends Fragment {

    private List<MediaItem> list;
    @BindView(R.id.shopping_list)
    RecyclerView recyclerView;
    private MediaFacebookAdapter facebookAdapter;
    private Unbinder unbinder;
    @BindView(R.id.facebook_login_button)
    LoginButton loginButton;
    CallbackManager callbackManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        list = new ArrayList<>();
        View mainFragment = inflater.inflate(R.layout.fragment_facebook, container, false);
        unbinder = ButterKnife.bind(this, mainFragment);

        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.setFragment(this);

        Profile currentProfile = Profile.getCurrentProfile();
        if(currentProfile != null) {
            loginButton.setVisibility(View.GONE);
            list.add(new MediaItem(currentProfile.getFirstName(), currentProfile.getLastName()));
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginButton.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.fb_login_success_toast) + loginResult.getAccessToken().toString(), Toast.LENGTH_SHORT).show();
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

        facebookAdapter = new MediaFacebookAdapter(list, getActivity());

        recyclerView.setAdapter(facebookAdapter);
        return mainFragment;
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
