package pl.nikowis.focus.ui.twitter;

import android.content.Context;
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
    private static final int PAGE_COUNT = 10;
    private ContentLoaderEventsListener contentLoaderEventsListener;
    private TwitterRequestManager requestManager;
    private Long lastId = null;


    public TwitterFeedLoader(Context context, TwitterPostsAdapter twitterAdapter, ContentLoaderEventsListener listener) {
        this.context = context;
        this.twitterAdapter = twitterAdapter;
        this.visiblePostsList = twitterAdapter.getList();
        this.contentLoaderEventsListener = listener;
        visiblePostsList.clear();
        loadedPostsList = new ArrayList<>(PAGE_COUNT * 10);
        requestManager = new TwitterRequestManager(Twitter.getSessionManager().getActiveSession());
        requestManager.getHomeTilemline(null, createCallback());
        contentLoaderEventsListener.loadingMoreData();
    }

    public void loadContent() {

        for (int i = 0; i < PAGE_COUNT; i++) {
            if (loadedPostsList.size() < PAGE_COUNT + 1) {
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

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(context, "Error loading twitter data", Toast.LENGTH_SHORT).show();
                contentLoaderEventsListener.readyToDisplay();
            }
        };
    }


    public static Date getTwitterDate(String date) {
        Date res = null;
        final String TWITTER="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.ENGLISH);
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
