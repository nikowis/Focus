package pl.nikowis.focus.rest.instagram;

import android.content.Context;

import pl.nikowis.focus.rest.base.ApiRequestManager;
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
    public static final String CALLBACK_URL = "https://github.com/nikowis/Focus";
    //public static final String CALLBACK_URL = "your://redirecturi/Focus";


    private InstagramRequestManager(Context context) {
        super(context, BASE_URL);
    }

    public static InstagramRequestManager getInstance(Context context) {
        if (instagramRequestManager == null && context != null) {
            instagramRequestManager = new InstagramRequestManager(context);
        }
        return instagramRequestManager;
    }

    public void InstagramRequests(final String userId, final String accessToken, final Callback<InstagramLoginResponse> callback) {
        final InstagramRequests instagramRequests = createRestAdapter(CLIENT_ID, CLIENT_SECRET).create(InstagramRequests.class);
        //Call<InstagramLoginResponse> call = instagramRequests.getUserFeed(userId, accessToken);
        //call.enqueue(callback);
    }


}
