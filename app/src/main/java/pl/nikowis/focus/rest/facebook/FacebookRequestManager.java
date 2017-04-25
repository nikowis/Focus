package pl.nikowis.focus.rest.facebook;

import android.content.Context;

import pl.nikowis.focus.rest.base.ApiRequestManager;
import retrofit.Callback;

/**
 * Created by Nikodem on 4/25/2017.
 */

public class FacebookRequestManager extends ApiRequestManager {

    private static FacebookRequestManager facebookRequestManager;

    private FacebookRequestManager(Context context) {
        super(context, "https://graph.facebook.com");
    }

    public static FacebookRequestManager getInstance(Context context) {
        if (facebookRequestManager == null && context != null) {
            facebookRequestManager = new FacebookRequestManager(context);
        }
        return facebookRequestManager;
    }

    public void getUserFeed(final String userId, final String accessToken, final Callback<FbFeedDataResponse> callback) {
        final FacebookRequests facebookRequests = createRestAdapter().create(FacebookRequests.class);
        facebookRequests.getUserFeed(userId, accessToken, callback);
    }
}
