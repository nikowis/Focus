package pl.nikowis.focus.ui.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import pl.nikowis.focus.rest.twitter.TwitterFollowsDataResponse;
import pl.nikowis.focus.rest.twitter.TwitterRequestManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nikodem on 4/28/2017.
 */

public class TwitterFollowsLoader {

    private Context context;
    private final ArrayList<TwitterFollowsDataResponse.TwitterSingleFollowResponse> twitterFollows;
    private final TwitterRequestManager requestManager;
    private final FinishedLoadingListener finishedLoadingListener;

    public TwitterFollowsLoader(Context context) {
        this.context = context;
        requestManager = TwitterRequestManager.getInstance(context);
        this.finishedLoadingListener = new FinishedLoadingListener() {
            @Override
            public void finished() {
                //nothing
            }
        };
        twitterFollows = new ArrayList<>(100);
    }

    public TwitterFollowsLoader(Context context, FinishedLoadingListener finishedLoadingListener) {
        this.context = context;
        requestManager = TwitterRequestManager.getInstance(context);
        this.finishedLoadingListener = finishedLoadingListener;
        twitterFollows = new ArrayList<>(100);
    }

    /**
     * Method loads all likes and saves them to preferences under key
     * {@link TwitterSettings#KEY_PREF_FOLLOWED_USERS_IDS_AND_NAMES}.
     */
    public void loadAllFollows() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String auth_token = prefs.getString(TwitterSettings.KEY_PREF_TWITTER_AUTH_TOKEN, null);
        if (auth_token != null) {
            requestManager.getFollowedUsers(auth_token, createNextPageCallback());
        } else {
            finishedLoadingListener.finished();
        }
    }

    private Callback<TwitterFollowsDataResponse> createNextPageCallback() {
        return new Callback<TwitterFollowsDataResponse>() {
            @Override
            public void onResponse(Call<TwitterFollowsDataResponse> call, Response<TwitterFollowsDataResponse> response) {
                twitterFollows.addAll(response.body().twitterFollows);
                //TODO: if (response.body().pagination != null && response.body().pagination.nextUrl != null) {
                //TODO:    requestManager.getNextFollowedUsers(response.body().pagination.nextUrl, createNextPageCallback());
                //TODO: } else {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Set<String> pageIdsAndNames = new LinkedHashSet<>();
                    for (TwitterFollowsDataResponse.TwitterSingleFollowResponse follow : twitterFollows) {
                        pageIdsAndNames.add(follow.id + TwitterSettings.ID_NAME_SEPARATOR + follow.username);
                    }
                    prefs.edit().putStringSet(TwitterSettings.KEY_PREF_FOLLOWED_USERS_IDS_AND_NAMES, pageIdsAndNames).apply();
                    Toast.makeText(context, "Succesfully loaded twitter follows", Toast.LENGTH_SHORT).show();
                    finishedLoadingListener.finished();
                //TODO:  }
            }

            @Override
            public void onFailure(Call<TwitterFollowsDataResponse> call, Throwable t) {
                Toast.makeText(context, "ERROR LOADING", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public interface FinishedLoadingListener {
        void finished();
    }
}
