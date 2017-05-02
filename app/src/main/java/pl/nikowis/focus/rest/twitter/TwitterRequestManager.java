package pl.nikowis.focus.rest.twitter;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class TwitterRequestManager extends TwitterApiClient {

    public TwitterRequestManager(TwitterSession session) {
        super(session);
    }

    //public static final String TWITTER_BASE_ID_URL = "https://twitter.com/statuses/";
    public static final String TWITTER_BASE_ID_URL = "twitter://status?id=";
    public static final String CLIENT_ID = "mzkAp2JV5OSiZprom7VhBq3i6";
    public static final String CLIENT_SECRET = "pjN4U9K2gBfppFAVJmnv8uDqQxwz6AEZF9hvmZ6wVZEOKBx89s";

    public void getHomeTilemline(Long maxId, final Callback<List<Tweet>> callback) {
        Call<List<Tweet>> call = this.getStatusesService().homeTimeline(
                null,null, maxId, null, null, null, null
        );
        call.enqueue(callback);
    }

}
