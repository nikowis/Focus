package pl.nikowis.focus.rest.facebook;

import android.content.Context;

import java.util.List;

import pl.nikowis.focus.rest.base.ApiRequestManager;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by Nikodem on 4/25/2017.
 */

public class FacebookRequestManager extends ApiRequestManager {

    private static FacebookRequestManager facebookRequestManager;
    public static final String BASE_URL = "https://graph.facebook.com";

    private FacebookRequestManager(Context context) {
        super(context, BASE_URL);
    }

    public static FacebookRequestManager getInstance(Context context) {
        if (facebookRequestManager == null && context != null) {
            facebookRequestManager = new FacebookRequestManager(context);
        }
        return facebookRequestManager;
    }

    public void getUserFeed(final String userId, final String accessToken, final Callback<FbFeedDataResponse> callback) {
        final FacebookRequests facebookRequests = createRestAdapter().create(FacebookRequests.class);
        Call<FbFeedDataResponse> call = facebookRequests.getUserFeed(userId, accessToken);
        call.enqueue(callback);
    }

    public void getPageFeed(final String pageName, final String accessToken, final Callback<FbFeedDataResponse> callback) {
        final FacebookRequests facebookRequests = createRestAdapter().create(FacebookRequests.class);
        Call<FbFeedDataResponse> call = facebookRequests.getPageFeed(pageName, accessToken);
        call.enqueue(callback);
    }

    public void getPageFeed(final String fullUrl, final Callback<FbFeedDataResponse> callback) {
        final FacebookRequests facebookRequests = createRestAdapter().create(FacebookRequests.class);
        Call<FbFeedDataResponse> call = facebookRequests.getPageFeed(fullUrl);
        call.enqueue(callback);
    }

    public void getLikedPages(final String userId, final String accesqsToken, final Callback<FbLikesDataResponse> callback) {
        final FacebookRequests facebookRequests = createRestAdapter().create(FacebookRequests.class);
        Call<FbLikesDataResponse> call = facebookRequests.getUserLikes(userId, accesqsToken);
        call.enqueue(callback);
    }

    public void getLikedPages(final String fullUrl, final Callback<FbLikesDataResponse> callback) {
        final FacebookRequests facebookRequests = createRestAdapter().create(FacebookRequests.class);
        Call<FbLikesDataResponse> call = facebookRequests.getUserLikes(fullUrl);
        call.enqueue(callback);
    }
}
