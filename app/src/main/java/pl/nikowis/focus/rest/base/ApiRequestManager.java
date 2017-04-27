package pl.nikowis.focus.rest.base;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class ApiRequestManager {

    private static final String TAG = ApiRequestManager.class.getName();

    private Context mContext;
    private String mUrl;

    private static ApiRequestManager sApiRequestManager = null;

    protected ApiRequestManager(Context context, String baseUrl) {
        this.mContext = context;
        mUrl = baseUrl;
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
        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        restAdapter.addConverterFactory(GsonConverterFactory.create(gson));
        return restAdapter.build();
    }

}
