package pl.nikowis.focus.rest.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    protected Retrofit createRestAdapter() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
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
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        Retrofit.Builder restAdapter = new Retrofit.Builder();
        restAdapter.baseUrl(mUrl);
        restAdapter.client(okHttpClient);
        restAdapter.addConverterFactory(GsonConverterFactory.create());
        return restAdapter.build();
    }

}
