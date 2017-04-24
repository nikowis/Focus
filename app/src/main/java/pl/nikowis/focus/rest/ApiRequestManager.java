package pl.nikowis.focus.rest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ApiRequestManager {

    private static final String TAG = ApiRequestManager.class.getName();

    private Context mContext;
    private String mUrl;
    private String mUserAgent;

    private static ApiRequestManager sApiRequestManager = null;

    private ApiRequestManager(Context context) {
        this.mContext = context;
        mUrl = "https://graph.facebook.com";
        createUserAgent();
    }

    private String getAppVersionName(Context ctx) {
        try {
            return ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private void createUserAgent() {
        mUserAgent = "FocusApp"
                + " (" + getAppVersionName(mContext) + ") "
                + "android"
                + ": " + Build.VERSION.RELEASE;
    }

    public static ApiRequestManager getInstance(Context context) {
        if (sApiRequestManager == null && context != null) {
            sApiRequestManager = new ApiRequestManager(context);
        }
        return sApiRequestManager;
    }

    private RestAdapter createRestAdapter(final String mimeType) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(120, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(120, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(120, TimeUnit.SECONDS);
        RestAdapter.Builder restAdapter = new RestAdapter.Builder();
        restAdapter.setEndpoint(mUrl);
        restAdapter.setClient(new OkClient(okHttpClient));
        restAdapter.setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", mUserAgent);
                request.addHeader("Content-Type", mimeType);
            }
        });
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        restAdapter.setLog(new RestAdapter.Log() {

            @Override
            public void log(String message) {
                int maxLogSize = 1000;
                for (int i = 0; i <= message.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i + 1) * maxLogSize;
                    end = end > message.length() ? message.length() : end;
                    Log.d(TAG, message.substring(start, end));
                }
            }
        });
        return restAdapter.build();
    }

    private RestAdapter createDefaultRestAdapter() {
        return createRestAdapter("application/json");
    }

    public void getUserFeed(final String userId, final String accessToken, final Callback<FbPostResponseData> callback) {
        final UserRequest userRequest = createDefaultRestAdapter().create(UserRequest.class);
        userRequest.getUserFeed(userId, accessToken, callback);
    }
}
