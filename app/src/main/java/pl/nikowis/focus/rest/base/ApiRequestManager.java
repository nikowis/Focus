package pl.nikowis.focus.rest.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public abstract class ApiRequestManager {

    private static final String TAG = ApiRequestManager.class.getName();

    private Context mContext;
    private String mUrl;
    private String mUserAgent;

    private static ApiRequestManager sApiRequestManager = null;

    protected ApiRequestManager(Context context, String baseUrl) {
        this.mContext = context;
        mUrl = baseUrl;
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

    protected RestAdapter createRestAdapter() {
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
                request.addHeader("Content-Type", "application/json");
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

}
