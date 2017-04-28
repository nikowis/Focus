package pl.nikowis.focus.ui.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import pl.nikowis.focus.rest.facebook.FacebookRequestManager;
import pl.nikowis.focus.rest.facebook.FbLikesDataResponse;
import pl.nikowis.focus.ui.base.SettingsFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nikodem on 4/28/2017.
 */

public class FacebookLikesLoader {

    private Context context;
    private final ArrayList<FbLikesDataResponse.FbSingleLikeResponse> facebookLikes;
    private final FacebookRequestManager requestManager;
    private final FinishedLoadingListener finishedLoadingListener;

    public FacebookLikesLoader(Context context) {
        this.context = context;
        requestManager = FacebookRequestManager.getInstance(context);
        this.finishedLoadingListener = new FinishedLoadingListener() {
            @Override
            public void finished() {
                //nothing
            }
        };
        facebookLikes = new ArrayList<>(100);
    }

    public FacebookLikesLoader(Context context, FinishedLoadingListener finishedLoadingListener) {
        this.context = context;
        requestManager = FacebookRequestManager.getInstance(context);
        this.finishedLoadingListener = finishedLoadingListener;
        facebookLikes = new ArrayList<>(100);
    }

    /**
     * Method loads all likes and saves them to preferences under keys
     * {@link SettingsFragment#KEY_PREF_LIKED_PAGES_IDS} and {@link SettingsFragment#KEY_PREF_LIKED_PAGES_NAMES}.
     */
    public void loadAllLikes() {
        Profile currentProfile = Profile.getCurrentProfile();
        if (currentProfile != null) {
            requestManager.getLikedPages(currentProfile.getId(), AccessToken.getCurrentAccessToken().getToken(), createNextPageCallback());
        } else {
            finishedLoadingListener.finished();
        }
    }

    private Callback<FbLikesDataResponse> createNextPageCallback() {
        return new Callback<FbLikesDataResponse>() {
            @Override
            public void onResponse(Call<FbLikesDataResponse> call, Response<FbLikesDataResponse> response) {
                facebookLikes.addAll(response.body().fbSingleLikeResponses);
                Log.w("LIKES NUMBER -------> ", String.valueOf(facebookLikes.size()));
                if (response.body().paging != null && response.body().paging.next != null) {
                    requestManager.getLikedPages(response.body().paging.next, createNextPageCallback());
                } else {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Set<String> pageIds = new LinkedHashSet<>();
                    Set<String> pageNames = new LinkedHashSet<>();
                    for (FbLikesDataResponse.FbSingleLikeResponse like : facebookLikes) {
                        pageIds.add(like.id);
                        pageNames.add(like.name);
                    }
                    prefs.edit().putStringSet(SettingsFragment.KEY_PREF_LIKED_PAGES_IDS, pageIds).apply();
                    prefs.edit().putStringSet(SettingsFragment.KEY_PREF_LIKED_PAGES_NAMES, pageNames).apply();
                    Toast.makeText(context, "Succesfully loaded facebook likes", Toast.LENGTH_SHORT).show();
                    finishedLoadingListener.finished();
                }
            }

            @Override
            public void onFailure(Call<FbLikesDataResponse> call, Throwable t) {
                Toast.makeText(context, "ERROR LOADING", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public interface FinishedLoadingListener {
        void finished();
    }
}
