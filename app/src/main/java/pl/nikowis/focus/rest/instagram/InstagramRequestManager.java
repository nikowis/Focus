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

    private void exchangeCodeForToken(String code, Callback<InstagramLoginResponse> callback) {
        final InstagramRequests instagramRequests = createRestAdapter().create(InstagramRequests.class);

        Call<InstagramLoginResponse> call = instagramRequests.getTokenWithCodeEncoded(
                CLIENT_ID
                , CLIENT_SECRET
                , "authorization_code"
                , REDIRECT_URI
                , code
        );

        call.enqueue(callback);
    }

    public void login(final Callback<InstagramLoginResponse> callback) {
        String url = BASE_URL + "/oauth/authorize/"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=code";
        InstagramLoginDialog mDialog = new InstagramLoginDialog(this.mContext, url, new InstagramLoginDialog.OAuthDialogListener() {
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
}
