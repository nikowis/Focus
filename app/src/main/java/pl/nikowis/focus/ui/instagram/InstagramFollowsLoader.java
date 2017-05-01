package pl.nikowis.focus.ui.instagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

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
     * Method loads all likes and saves them to preferences under key
     * {@link InstagramSettings#KEY_PREF_FOLLOWED_USERS_IDS_AND_NAMES}.
     */
    public void loadAllFollows() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String auth_token = prefs.getString(InstagramSettings.KEY_PREF_INSTAGRAM_AUTH_TOKEN, null);
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
                        pageIdsAndNames.add(follow.id + InstagramSettings.ID_NAME_SEPARATOR + follow.username);
                    }
                    prefs.edit().putStringSet(InstagramSettings.KEY_PREF_FOLLOWED_USERS_IDS_AND_NAMES, pageIdsAndNames).apply();
                    Toast.makeText(context, "Succesfully loaded instagram follows", Toast.LENGTH_SHORT).show();
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
