package pl.nikowis.focus.ui.instagram;

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

import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.instagram.InstaFeedDataResponse;
import pl.nikowis.focus.rest.instagram.InstagramRequestManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class InstagramFeedLoader {

    private Context context;
    private InstagramPostsAdapter instagramAdapter;
    private List<InstagramPost> visiblePostsList;
    private Set<String> selectedUserIdsAndNames;
    private Map<String, String> nextUsersMap;
    private Map<String, List<InstagramPost>> loadedPostsMap;
    private List<InstagramPost> queuedPostsList;
    private int pageCount;
    private boolean usingCustomUsers;
    private ContentLoaderEventsListener contentLoaderEventsListener;
    private int counter, currentlyLoadingUserCount;
    private String authToken = null;

    public InstagramFeedLoader(Context context, InstagramPostsAdapter instagramAdapter, ContentLoaderEventsListener listener) {
        this.context = context;
        this.instagramAdapter = instagramAdapter;
        this.visiblePostsList = instagramAdapter.getList();
        this.contentLoaderEventsListener = listener;
        visiblePostsList.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        authToken = prefs.getString(context.getString(R.string.key_pref_instagram_auth_token), null);
        usingCustomUsers = prefs.getBoolean(context.getString(R.string.key_pref_instagram_using_custom_users), false);
        pageCount = Integer.parseInt(prefs.getString(context.getString(R.string.key_pref_facebook_page_count), "10"));

        if (!usingCustomUsers) {
            selectedUserIdsAndNames = prefs.getStringSet(context.getString(R.string.key_pref_instagram_selected_users), new HashSet<String>());
        } else {
            selectedUserIdsAndNames = prefs.getStringSet(context.getString(R.string.key_pref_instagram_selected_custom_users), new HashSet<String>());
        }

        nextUsersMap = new LinkedHashMap<>();
        loadedPostsMap = new LinkedHashMap<>();
        queuedPostsList = new ArrayList<>(pageCount * 10);
        for (String user : selectedUserIdsAndNames) {
            nextUsersMap.put(user, "");
            loadedPostsMap.put(user, new ArrayList<InstagramPost>(pageCount * 10));
        }
        if (selectedUserIdsAndNames.isEmpty()) {
            Toast.makeText(context, R.string.instagram_no_users_selected, Toast.LENGTH_SHORT).show();
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
                instagramAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, R.string.nothing_to_load, Toast.LENGTH_SHORT).show();
            }
        } catch (IndexOutOfBoundsException e) {
            //too fast refreshing
            return;
        }

    }

    private void calculateQueuedPostsList() {
        Map<String, InstagramPost> latestPostsFromEachUser = constructLatestsPostsMap();

        for (int i = 0; i < pageCount; i++) {
            Map.Entry<String, InstagramPost> latest = getLatestPostEntrySet(latestPostsFromEachUser);
            if (latest == null) {
                break;
            }
            queuedPostsList.add(latest.getValue());
            List<InstagramPost> remainingUserPosts = loadedPostsMap.get(latest.getKey());
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
    private Map<String, InstagramPost> constructLatestsPostsMap() {
        Map<String, InstagramPost> latestPostsFromEachUser = new LinkedHashMap<>();
        for (String key : loadedPostsMap.keySet()) {
            List<InstagramPost> instagramPosts = loadedPostsMap.get(key);
            if (instagramPosts.size() < pageCount + 1) {
                requestUserPosts(key);
            }
            latestPostsFromEachUser.put(key, instagramPosts.get(0));
        }
        return latestPostsFromEachUser;
    }

    private Map.Entry<String, InstagramPost> getLatestPostEntrySet(Map<String, InstagramPost> latestPostsFromEachUser) {
        Iterator<Map.Entry<String, InstagramPost>> iterator = latestPostsFromEachUser.entrySet().iterator();
        Map.Entry<String, InstagramPost> latestEntrySet = null;
        if (iterator.hasNext()) {
            latestEntrySet = iterator.next();

            while (iterator.hasNext()) {
                Map.Entry<String, InstagramPost> next = iterator.next();
                if (latestEntrySet.getValue().getDate().compareTo(next.getValue().getDate()) < 0) {
                    latestEntrySet = next;
                }
            }
        }
        return latestEntrySet;
    }

    private void requestUserPosts(final String userIdAndName) {
        InstagramRequestManager requestManager = InstagramRequestManager.getInstance(context);
        Callback<InstaFeedDataResponse> callback = createCallback(userIdAndName);
        if (nextUsersMap.get(userIdAndName) != null) {
            if (nextUsersMap.get(userIdAndName).isEmpty()) {
                requestManager.getUserFeed(userIdAndName.split(context.getString(R.string.instagram_id_name_separator))[0], authToken, callback);
            } else {
                contentLoaderEventsListener.loadingMoreData();
                currentlyLoadingUserCount++;
                requestManager.getUserFeed(nextUsersMap.get(userIdAndName), callback);
            }
        }
    }

    @NonNull
    private Callback<InstaFeedDataResponse> createCallback(final String userIdAndName) {
        return new Callback<InstaFeedDataResponse>() {
            @Override
            public void onResponse(Call<InstaFeedDataResponse> call, Response<InstaFeedDataResponse> response) {
                counter++;

                String userName = getUserName(userIdAndName);
                if (!response.isSuccessful()) {
                    loadedPostsMap.remove(userIdAndName);
                    nextUsersMap.remove(userIdAndName);
                    selectedUserIdsAndNames.remove(userIdAndName);
                    Toast.makeText(context, userName + context.getString(R.string.instagram_incorrect_user_id), Toast.LENGTH_SHORT).show();
                    return;
                }
                String next = response.body().pagination.nextUrl;
                nextUsersMap.put(userIdAndName, next);

                List<InstagramPost> postsFromResponse = new ArrayList<>(pageCount * 10);
                for (InstaFeedDataResponse.InstaSinglePostResponse res : response.body().instaPosts) {
                    if (res.caption == null || res.caption.text == null) {
                        postsFromResponse.add(new InstagramPost(res.user.username
                                , new Date(res.createdTime), res.link, res.images.thumbnail));
                    } else {
                        postsFromResponse.add(new InstagramPost(res.user.username, res.caption.text
                                , new Date(res.createdTime), res.link, res.images.thumbnail));
                    }
                }
                loadedPostsMap.get(userIdAndName).addAll(postsFromResponse);
                if (counter >= currentlyLoadingUserCount) {
                    loadContent();
                    contentLoaderEventsListener.readyToDisplay();
                    counter = 0;
                    currentlyLoadingUserCount = 0;
                }
            }

            @Override
            public void onFailure(Call<InstaFeedDataResponse> call, Throwable t) {
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
                Log.w("REST ERROR", t.getMessage());
                Log.w("REST ERROR", t.getStackTrace().toString());
            }
        };
    }

    private String getUserName(String userIdAndName) {
        return userIdAndName.split(context.getString(R.string.instagram_id_name_separator))[1];
    }

    public interface ContentLoaderEventsListener {
        void readyToDisplay();

        void loadingMoreData();
    }
}
