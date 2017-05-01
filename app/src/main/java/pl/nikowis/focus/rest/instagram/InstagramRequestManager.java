package pl.nikowis.focus.rest.instagram;

import android.content.Context;

import pl.nikowis.focus.rest.base.ApiRequestManager;
import pl.nikowis.focus.ui.instagram.InstagramLoginDialog;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class InstagramRequestManager extends ApiRequestManager {

    public static final String BASE_URL = "https://api.instagram.com";
    private static InstagramRequestManager instagramRequestManager;
    public static final String CLIENT_ID = "f4ea7842b9254c64804f34acb28a5fe9";
    public static final String CLIENT_SECRET = "11e41b6c8e94435fa6e4cff05b10957f";
    public static final String REDIRECT_URI = "https://github.com/nikowis/Focus";

    private InstagramRequestManager(Context context) {
        super(context, BASE_URL);
    }

    public static InstagramRequestManager getInstance(Context context) {
        if (instagramRequestManager == null && context != null) {
            instagramRequestManager = new InstagramRequestManager(context);
        }
        return instagramRequestManager;
    }

    private void exchangeCodeForToken(String code, Callback<InstaLoginResponse> callback) {
        final InstagramRequests instagramRequests = createRestAdapter().create(InstagramRequests.class);

        Call<InstaLoginResponse> call = instagramRequests.getTokenWithCodeEncoded(
                CLIENT_ID
                , CLIENT_SECRET
                , "authorization_code"
                , REDIRECT_URI
                , code
        );

        call.enqueue(callback);
    }

    public void login(Context context, final Callback<InstaLoginResponse> callback) {
        String url = BASE_URL + "/oauth/authorize/"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=code"
                + "&scope=basic+public_content+follower_list";
        InstagramLoginDialog mDialog = new InstagramLoginDialog(context, url, new InstagramLoginDialog.OAuthDialogListener() {
            @Override
            public void onComplete(String code) {
                exchangeCodeForToken(code, callback);
            }

            @Override
            public void onError(String error) {

            }
        });
        mDialog.show();
    }


    public void getUserFeed(final String userId, final String authToken, final Callback<InstaFeedDataResponse> callback) {
        final InstagramRequests instagramRequests = createRestAdapter().create(InstagramRequests.class);
        Call<InstaFeedDataResponse> call = instagramRequests.getUserFeed(userId, authToken);
        call.enqueue(callback);
    }

    public void getUserFeed(final String fullUrl, final Callback<InstaFeedDataResponse> callback) {
        final InstagramRequests instagramRequests = createRestAdapter().create(InstagramRequests.class);
        Call<InstaFeedDataResponse> call = instagramRequests.getUserFeed(fullUrl);
        call.enqueue(callback);
    }

    public void getFollowedUsers(final String authToken, final Callback<InstaFollowsDataResponse> callback) {
        final InstagramRequests instagramRequests = createRestAdapter().create(InstagramRequests.class);
        Call<InstaFollowsDataResponse> call = instagramRequests.getFollowedUsers(authToken);
        call.enqueue(callback);
    }

    public void getNextFollowedUsers(final String fullUrl, final Callback<InstaFollowsDataResponse> callback) {
        final InstagramRequests instagramRequests = createRestAdapter().create(InstagramRequests.class);
        Call<InstaFollowsDataResponse> call = instagramRequests.getNextFollowedUsers(fullUrl);
        call.enqueue(callback);
    }
}
