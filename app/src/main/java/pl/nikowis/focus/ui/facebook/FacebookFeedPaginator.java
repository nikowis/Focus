package pl.nikowis.focus.ui.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.nikowis.focus.rest.facebook.FacebookRequestManager;
import pl.nikowis.focus.rest.facebook.FbFeedDataResponse;
import pl.nikowis.focus.ui.base.SettingsFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nikodem on 4/26/2017.
 */

class FacebookFeedPaginator {

    private Context context;
    private FacebookPostsAdapter facebookAdapter;
    private List<FacebookPost> postsList;

    public FacebookFeedPaginator(Context context, FacebookPostsAdapter facebookAdapter) {
        this.context= context;
        this.facebookAdapter = facebookAdapter;
        this.postsList = facebookAdapter.getList();
    }

    public void loadContent() {
        Bundle params = new Bundle();
        params.putString("with", "location");
        FacebookRequestManager requestManager = FacebookRequestManager.getInstance(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> pages = prefs.getStringSet(SettingsFragment.KEY_PREF_SELECTED_PAGES, new HashSet<String>());

        for (String page : pages) {
            requestPagePosts(requestManager, page);
        }
    }


    private void requestPagePosts(FacebookRequestManager requestManager, final String pageName) {
        requestManager.getPageFeed(pageName, AccessToken.getCurrentAccessToken().getToken(), new Callback<FbFeedDataResponse>() {
            @Override
            public void onResponse(Call<FbFeedDataResponse> call, Response<FbFeedDataResponse> response) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();

                for (FbFeedDataResponse.FbSinglePostResponse res : response.body().fbSinglePostResponses) {
                    postsList.add(new FacebookPost(pageName, res.message));
                }
                facebookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<FbFeedDataResponse> call, Throwable t) {
                Toast.makeText(context, "REST ERROR", Toast.LENGTH_SHORT).show();
                Log.w("REST ERROR", t.getMessage());
                Log.w("REST ERROR", t.getStackTrace().toString());
            }
        });

    }

}
