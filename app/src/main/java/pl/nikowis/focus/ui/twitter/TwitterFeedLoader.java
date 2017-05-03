package pl.nikowis.focus.ui.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.twitter.TwitterRequestManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class TwitterFeedLoader {

    private Context context;
    private TwitterPostsAdapter twitterAdapter;
    private List<TwitterPost> visiblePostsList;
    private List<TwitterPost> loadedPostsList;
    private int pageCount;
    private ContentLoaderEventsListener contentLoaderEventsListener;
    private TwitterRequestManager requestManager;
    private Long lastId = null;


    public TwitterFeedLoader(Context context, TwitterPostsAdapter twitterAdapter, ContentLoaderEventsListener listener) {
        this.context = context;
        this.twitterAdapter = twitterAdapter;
        this.visiblePostsList = twitterAdapter.getList();
        this.contentLoaderEventsListener = listener;
        visiblePostsList.clear();
        loadedPostsList = new ArrayList<>(pageCount * 10);
        requestManager = new TwitterRequestManager(Twitter.getSessionManager().getActiveSession());
        requestManager.getHomeTilemline(null, createCallback());
        contentLoaderEventsListener.loadingMoreData();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        pageCount = Integer.parseInt(prefs.getString(context.getString(R.string.key_pref_twitter_page_count), "10"));
    }

    public void loadContent() {

        for (int i = 0; i < pageCount; i++) {
            if (loadedPostsList.size() < pageCount + 1) {
                contentLoaderEventsListener.loadingMoreData();
                requestManager.getHomeTilemline(lastId, createCallback());
                return;
            }

            visiblePostsList.add(loadedPostsList.remove(0));
        }
        twitterAdapter.notifyDataSetChanged();
    }

    @NonNull
    private Callback<List<Tweet>> createCallback() {
        return new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Tweet tweet : response.body()) {

                        if (lastId != null && lastId == tweet.id) {
                            //ignore duplicate post
                        } else {
                            loadedPostsList.add(new TwitterPost(tweet.user.name, tweet.text, getTwitterDate(tweet.createdAt), tweet.idStr));
                            lastId = tweet.id;
                        }
                    }
                    loadContent();
                    contentLoaderEventsListener.readyToDisplay();
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(context, R.string.twitter_error_loading_data, Toast.LENGTH_SHORT).show();
                contentLoaderEventsListener.readyToDisplay();
            }
        };
    }


    public Date getTwitterDate(String date) {
        Date res = null;
        final String format = context.getString(R.string.twitter_api_date_format);
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        sf.setLenient(true);
        try {
            res = sf.parse(date);
        } catch (ParseException ex) {
            //ignore
        }
        return res;
    }

    public interface ContentLoaderEventsListener {
        void readyToDisplay();

        void loadingMoreData();
    }
}
