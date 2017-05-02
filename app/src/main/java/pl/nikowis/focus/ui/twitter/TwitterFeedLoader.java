package pl.nikowis.focus.ui.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.nikowis.focus.rest.twitter.TwitterFeedDataResponse;
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
    private Set<String> selectedUserIdsAndNames;
    private Map<String, String> nextUsersMap;
    private Map<String, List<TwitterPost>> loadedPostsMap;
    private List<TwitterPost> queuedPostsList;
    private static final int PAGE_COUNT = 10;
    private boolean usingCustomUsers;
    private ContentLoaderEventsListener contentLoaderEventsListener;
    private int counter, currentlyLoadingUserCount;
    private String authToken = null;

    public TwitterFeedLoader(Context context, TwitterPostsAdapter twitterAdapter, ContentLoaderEventsListener listener) {
        this.context = context;
        this.twitterAdapter = twitterAdapter;
        this.visiblePostsList = twitterAdapter.getList();
        this.contentLoaderEventsListener = listener;
        visiblePostsList.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        authToken = prefs.getString(TwitterSettings.KEY_PREF_TWITTER_AUTH_TOKEN, null);
        usingCustomUsers = prefs.getBoolean(TwitterSettings.KEY_PREF_USING_CUSTOM_USERS, false);

        if (!usingCustomUsers) {
            selectedUserIdsAndNames = prefs.getStringSet(TwitterSettings.KEY_PREF_SELECTED_USERS, new HashSet<String>());
        } else {
            selectedUserIdsAndNames = prefs.getStringSet(TwitterSettings.KEY_PREF_SELECTED_CUSTOM_USERS, new HashSet<String>());
        }

        nextUsersMap = new LinkedHashMap<>();
        loadedPostsMap = new LinkedHashMap<>();
        queuedPostsList = new ArrayList<>(PAGE_COUNT * 10);
        for (String user : selectedUserIdsAndNames) {
            nextUsersMap.put(user, "");
            loadedPostsMap.put(user, new ArrayList<TwitterPost>(PAGE_COUNT * 10));
        }
        if (selectedUserIdsAndNames.isEmpty()) {
            Toast.makeText(context, "No users selected", Toast.LENGTH_SHORT).show();
        } else {
            contentLoaderEventsListener.loadingMoreData();
            currentlyLoadingUserCount = selectedUserIdsAndNames.size();
            for (String userIdAndName : selectedUserIdsAndNames) {
                requestUserPosts(userIdAndName);
            }
        }
    }

    public void loadContent() {
        try {
            if (loadedPostsMap.size() > 0) {
                calculateQueuedPostsList();
                visiblePostsList.addAll(queuedPostsList);
                queuedPostsList.clear();
                twitterAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Nothing to load", Toast.LENGTH_SHORT).show();
            }
        } catch (IndexOutOfBoundsException e) {
            //too fast refreshing
            return;
        }

    }

    private void calculateQueuedPostsList() {
        Map<String, TwitterPost> latestPostsFromEachUser = constructLatestsPostsMap();

        for (int i = 0; i < PAGE_COUNT; i++) {
            Map.Entry<String, TwitterPost> latest = getLatestPostEntrySet(latestPostsFromEachUser);
            if (latest == null) {
                break;
            }
            queuedPostsList.add(latest.getValue());
            List<TwitterPost> remainingUserPosts = loadedPostsMap.get(latest.getKey());
            remainingUserPosts.remove(0);
            if (remainingUserPosts.size() > 0) {
                latestPostsFromEachUser.put(latest.getKey(), remainingUserPosts.get(0));
            } else {
                latestPostsFromEachUser.remove(latest.getKey());
                loadedPostsMap.remove(latest.getKey());
                nextUsersMap.remove(latest.getKey());
            }
        }
    }

    @NonNull
    private Map<String, TwitterPost> constructLatestsPostsMap() {
        Map<String, TwitterPost> latestPostsFromEachUser = new LinkedHashMap<>();
        for (String key : loadedPostsMap.keySet()) {
            List<TwitterPost> twitterPosts = loadedPostsMap.get(key);
            if (twitterPosts.size() < PAGE_COUNT + 1) {
                requestUserPosts(key);
            }
            latestPostsFromEachUser.put(key, twitterPosts.get(0));
        }
        return latestPostsFromEachUser;
    }

    private Map.Entry<String, TwitterPost> getLatestPostEntrySet(Map<String, TwitterPost> latestPostsFromEachUser) {
        Iterator<Map.Entry<String, TwitterPost>> iterator = latestPostsFromEachUser.entrySet().iterator();
        Map.Entry<String, TwitterPost> latestEntrySet = null;
        if (iterator.hasNext()) {
            latestEntrySet = iterator.next();

            while (iterator.hasNext()) {
                Map.Entry<String, TwitterPost> next = iterator.next();
                if (latestEntrySet.getValue().getDate().compareTo(next.getValue().getDate()) < 0) {
                    latestEntrySet = next;
                }
            }
        }
        return latestEntrySet;
    }

    private void requestUserPosts(final String userIdAndName) {
        TwitterRequestManager requestManager = TwitterRequestManager.getInstance(context);
        Callback<TwitterFeedDataResponse> callback = createCallback(userIdAndName);
        if (nextUsersMap.get(userIdAndName) != null) {
            if (nextUsersMap.get(userIdAndName).isEmpty()) {
                requestManager.getUserFeed(userIdAndName.split(TwitterSettings.ID_NAME_SEPARATOR)[0], authToken, callback);
            } else {
                contentLoaderEventsListener.loadingMoreData();
                currentlyLoadingUserCount++;
                requestManager.getUserFeed(nextUsersMap.get(userIdAndName), callback);
            }
        }
    }

    @NonNull
    private Callback<TwitterFeedDataResponse> createCallback(final String userIdAndName) {
        return new Callback<TwitterFeedDataResponse>() {
            @Override
            public void onResponse(Call<TwitterFeedDataResponse> call, Response<TwitterFeedDataResponse> response) {
                counter++;

                String userName = getUserName(userIdAndName);
                if (!response.isSuccessful()) {
                    loadedPostsMap.remove(userIdAndName);
                    nextUsersMap.remove(userIdAndName);
                    selectedUserIdsAndNames.remove(userIdAndName);
                    Toast.makeText(context, userName + " INCORRECT USER ID", Toast.LENGTH_SHORT).show();
                    return;
                }
               //TODO: String next = response.body().pagination.nextUrl;
                //TODO: nextUsersMap.put(userIdAndName, next);

                List<TwitterPost> postsFromResponse = new ArrayList<>(PAGE_COUNT * 10);
                for (TwitterFeedDataResponse.TwitterSinglePostResponse res : response.body().twitterPosts) {
                    if (res.caption == null || res.caption.text == null) {
                        postsFromResponse.add(new TwitterPost(res.user.username
                                , new Date(res.createdTime), res.link, res.images.thumbnail));
                    } else {
                        postsFromResponse.add(new TwitterPost(res.user.username, res.caption.text
                                , new Date(res.createdTime), res.link, res.images.thumbnail));
                    }
                }
                loadedPostsMap.get(userIdAndName).addAll(postsFromResponse);
                if (counter >= currentlyLoadingUserCount) {
                    contentLoaderEventsListener.readyToDisplay();
                    counter = 0;
                    currentlyLoadingUserCount = 0;
                }
            }

            @Override
            public void onFailure(Call<TwitterFeedDataResponse> call, Throwable t) {
                Toast.makeText(context, "CONNECTION ERROR", Toast.LENGTH_SHORT).show();
                Log.w("REST ERROR", t.getMessage());
                Log.w("REST ERROR", t.getStackTrace().toString());
            }
        };
    }

    private String getUserName(String userIdAndName) {
        return userIdAndName.split(TwitterSettings.ID_NAME_SEPARATOR)[1];
    }

    public interface ContentLoaderEventsListener {
        void readyToDisplay();

        void loadingMoreData();
    }
}
