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

import pl.nikowis.focus.R;
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
     * Method loads all likes and saves them to preferences.
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
                if (response.body().paging != null && response.body().paging.next != null) {
                    requestManager.getLikedPages(response.body().paging.next, createNextPageCallback());
                } else {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Set<String> pageIdsAndNames = new LinkedHashSet<>();
                    for (FbLikesDataResponse.FbSingleLikeResponse like : facebookLikes) {
                        pageIdsAndNames.add(like.id + context.getString(R.string.facebook_id_name_separator) + like.name);
                    }
                    prefs.edit().putStringSet(context.getString(R.string.key_pref_facebook_liked_pages_ids_and_names), pageIdsAndNames).apply();
                    Toast.makeText(context, R.string.facebook_load_likes_success, Toast.LENGTH_SHORT).show();
                    finishedLoadingListener.finished();
                }
            }

            @Override
            public void onFailure(Call<FbLikesDataResponse> call, Throwable t) {
                Toast.makeText(context, R.string.loader_error_loading, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public interface FinishedLoadingListener {
        void finished();
    }
}
