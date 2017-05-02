package pl.nikowis.focus.rest.twitter;

import android.content.Context;

import pl.nikowis.focus.rest.base.ApiRequestManager;
import pl.nikowis.focus.ui.twitter.TwitterLoginDialog;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class TwitterRequestManager extends ApiRequestManager {

    public static final String BASE_URL = "https://api.twitter.com";
    private static TwitterRequestManager twitterRequestManager;
    public static final String CLIENT_ID = "mzkAp2JV5OSiZprom7VhBq3i6";
    public static final String CLIENT_SECRET = "pjN4U9K2gBfppFAVJmnv8uDqQxwz6AEZF9hvmZ6wVZEOKBx89s";
    public static final String REDIRECT_URI = "https://github.com/nikowis/Focus/TwitterCallback";

    private TwitterRequestManager(Context context) {
        super(context, BASE_URL);
    }

    public static TwitterRequestManager getInstance(Context context) {
        if (twitterRequestManager == null && context != null) {
            twitterRequestManager = new TwitterRequestManager(context);
        }
        return twitterRequestManager;
    }

    private void exchangeCodeForToken(String code, Callback<TwitterLoginResponse> callback) {
        final TwitterRequests twitterRequests = createRestAdapter().create(TwitterRequests.class);

        Call<TwitterLoginResponse> call = twitterRequests.getTokenWithCodeEncoded(
                CLIENT_ID
                , CLIENT_SECRET
                , "authorization_code"
                , REDIRECT_URI
                , code
        );

        call.enqueue(callback);
    }

    public void login(Context context, final Callback<TwitterLoginResponse> callback) {
        String url = BASE_URL + "/oauth/authorize/"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=code"
                + "&scope=basic+public_content+follower_list";
        TwitterLoginDialog mDialog = new TwitterLoginDialog(context, url, new TwitterLoginDialog.OAuthDialogListener() {
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


    public void getUserFeed(final String userId, final String authToken, final Callback<TwitterFeedDataResponse> callback) {
        final TwitterRequests twitterRequests = createRestAdapter().create(TwitterRequests.class);
        Call<TwitterFeedDataResponse> call = twitterRequests.getUserFeed(userId, authToken);
        call.enqueue(callback);
    }

    public void getUserFeed(final String fullUrl, final Callback<TwitterFeedDataResponse> callback) {
        final TwitterRequests twitterRequests = createRestAdapter().create(TwitterRequests.class);
        Call<TwitterFeedDataResponse> call = twitterRequests.getUserFeed(fullUrl);
        call.enqueue(callback);
    }

    public void getFollowedUsers(final String authToken, final Callback<TwitterFollowsDataResponse> callback) {
        final TwitterRequests twitterRequests = createRestAdapter().create(TwitterRequests.class);
        Call<TwitterFollowsDataResponse> call = twitterRequests.getFollowedUsers(authToken);
        call.enqueue(callback);
    }

    public void getNextFollowedUsers(final String fullUrl, final Callback<TwitterFollowsDataResponse> callback) {
        final TwitterRequests twitterRequests = createRestAdapter().create(TwitterRequests.class);
        Call<TwitterFollowsDataResponse> call = twitterRequests.getNextFollowedUsers(fullUrl);
        call.enqueue(callback);
    }
}
