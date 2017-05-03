package pl.nikowis.focus.ui.instagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.instagram.InstaFollowsDataResponse;
import pl.nikowis.focus.rest.instagram.InstagramRequestManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nikodem on 4/28/2017.
 */

public class InstagramFollowsLoader {

    private Context context;
    private final ArrayList<InstaFollowsDataResponse.InstaSingleFollowResponse> instaFollows;
    private final InstagramRequestManager requestManager;
    private final FinishedLoadingListener finishedLoadingListener;

    public InstagramFollowsLoader(Context context) {
        this.context = context;
        requestManager = InstagramRequestManager.getInstance(context);
        this.finishedLoadingListener = new FinishedLoadingListener() {
            @Override
            public void finished() {
                //nothing
            }
        };
        instaFollows = new ArrayList<>(100);
    }

    public InstagramFollowsLoader(Context context, FinishedLoadingListener finishedLoadingListener) {
        this.context = context;
        requestManager = InstagramRequestManager.getInstance(context);
        this.finishedLoadingListener = finishedLoadingListener;
        instaFollows = new ArrayList<>(100);
    }

    /**
     * Method loads all likes and saves them to preferences.
     */
    public void loadAllFollows() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String auth_token = prefs.getString(context.getString(R.string.key_pref_instagram_auth_token), null);
        if (auth_token != null) {
            requestManager.getFollowedUsers(auth_token, createNextPageCallback());
        } else {
            finishedLoadingListener.finished();
        }
    }

    private Callback<InstaFollowsDataResponse> createNextPageCallback() {
        return new Callback<InstaFollowsDataResponse>() {
            @Override
            public void onResponse(Call<InstaFollowsDataResponse> call, Response<InstaFollowsDataResponse> response) {
                instaFollows.addAll(response.body().instaFollows);
                if (response.body().pagination != null && response.body().pagination.nextUrl != null) {
                    requestManager.getNextFollowedUsers(response.body().pagination.nextUrl, createNextPageCallback());
                } else {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Set<String> pageIdsAndNames = new LinkedHashSet<>();
                    for (InstaFollowsDataResponse.InstaSingleFollowResponse follow : instaFollows) {
                        pageIdsAndNames.add(follow.id + context.getString(R.string.instagram_id_name_separator) + follow.username);
                    }
                    prefs.edit().putStringSet(context.getString(R.string.key_pref_instagram_followed_user_ids_and_names), pageIdsAndNames).apply();
                    Toast.makeText(context, R.string.instragam_load_follows_success, Toast.LENGTH_SHORT).show();
                    finishedLoadingListener.finished();
                }
            }

            @Override
            public void onFailure(Call<InstaFollowsDataResponse> call, Throwable t) {
                Toast.makeText(context, "ERROR LOADING", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public interface FinishedLoadingListener {
        void finished();
    }
}
