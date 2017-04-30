package pl.nikowis.focus.ui.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.nikowis.focus.rest.facebook.FacebookRequestManager;
import pl.nikowis.focus.rest.facebook.FbFeedDataResponse;
import pl.nikowis.focus.ui.base.SettingsFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nikodem on 4/26/2017.
 */

public class FacebookFeedLoader {

    private Context context;
    private FacebookPostsAdapter facebookAdapter;
    private List<FacebookPost> visiblePostsList;
    private Set<String> selectedPageIdsAndNames;
    private Map<String, String> nextPagesMap;
    private Map<String, List<FacebookPost>> loadedPostsMap;
    private List<FacebookPost> queuedPostsList;
    private int pageCount = 10;
    private boolean usingCustomPages;
    private ContentLoaderEventsListener contentLoaderEventsListener;
    private int counter, currentlyLoadingPageCount;

    public FacebookFeedLoader(Context context, FacebookPostsAdapter facebookAdapter, ContentLoaderEventsListener listener) {
        this.context = context;
        this.facebookAdapter = facebookAdapter;
        this.visiblePostsList = facebookAdapter.getList();
        this.contentLoaderEventsListener = listener;
        visiblePostsList.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        usingCustomPages = prefs.getBoolean(SettingsFragment.KEY_PREF_USING_CUSTOM_PAGES, false);

        if (!usingCustomPages) {
            selectedPageIdsAndNames = prefs.getStringSet(SettingsFragment.KEY_PREF_SELECTED_PAGES, new HashSet<String>());
        } else {
            selectedPageIdsAndNames = prefs.getStringSet(SettingsFragment.KEY_PREF_SELECTED_CUSTOM_PAGES, new HashSet<String>());
        }

        nextPagesMap = new LinkedHashMap<>();
        loadedPostsMap = new LinkedHashMap<>();
        queuedPostsList = new ArrayList<>(pageCount * 10);
        for (String page : selectedPageIdsAndNames) {
            nextPagesMap.put(page, "");
            loadedPostsMap.put(page, new ArrayList<FacebookPost>(pageCount * 10));
        }
        if(selectedPageIdsAndNames.isEmpty()) {
            Toast.makeText(context, "No pages selected", Toast.LENGTH_SHORT).show();
        } else {
            contentLoaderEventsListener.loadingMoreData();
            currentlyLoadingPageCount = selectedPageIdsAndNames.size();
            for (String pageIdAndName : selectedPageIdsAndNames) {
                requestPagePosts(pageIdAndName);
            }
        }
    }

    public void loadContent() {
        try {
            calculateQueuedPostsList();
            visiblePostsList.addAll(queuedPostsList);
            queuedPostsList.clear();
            facebookAdapter.notifyDataSetChanged();
        } catch (IndexOutOfBoundsException e) {
            //too fast refreshing
            return;
        }

    }

    private void calculateQueuedPostsList() {
        Map<String, FacebookPost> latestPostsFromEachPage = constructLatestsPostsMap();

        for (int i = 0; i < pageCount; i++) {
            Map.Entry<String, FacebookPost> latest = getLatestPostEntrySet(latestPostsFromEachPage);
            queuedPostsList.add(latest.getValue());
            List<FacebookPost> remainingPagePosts = loadedPostsMap.get(latest.getKey());
            remainingPagePosts.remove(0);
            latestPostsFromEachPage.put(latest.getKey(), remainingPagePosts.get(0));

        }
    }

    @NonNull
    private Map<String, FacebookPost> constructLatestsPostsMap() {
        Map<String, FacebookPost> latestPostsFromEachPage = new LinkedHashMap<>();
        for (String key : loadedPostsMap.keySet()) {
            List<FacebookPost> facebookPosts = loadedPostsMap.get(key);
            if (facebookPosts.size() < pageCount + 1) {

                requestPagePosts(key);
            }
            latestPostsFromEachPage.put(key, facebookPosts.get(0));
        }
        return latestPostsFromEachPage;
    }

    private Map.Entry<String, FacebookPost> getLatestPostEntrySet(Map<String, FacebookPost> latestPostsFromEachPage) {
        Iterator<Map.Entry<String, FacebookPost>> iterator = latestPostsFromEachPage.entrySet().iterator();
        Map.Entry<String, FacebookPost> latestEntrySet = iterator.next();
        while (iterator.hasNext()) {
            Map.Entry<String, FacebookPost> next = iterator.next();
            if (latestEntrySet.getValue().getDate().compareTo(next.getValue().getDate()) < 0) {
                latestEntrySet = next;
            }
        }

        return latestEntrySet;
    }

    private void requestPagePosts(final String pageIdAndName) {
        FacebookRequestManager requestManager = FacebookRequestManager.getInstance(context);
        Callback<FbFeedDataResponse> callback = createCallback(pageIdAndName);
        if (nextPagesMap.get(pageIdAndName) != null) {
            if (nextPagesMap.get(pageIdAndName).isEmpty()) {
                requestManager.getPageFeed(pageIdAndName.split(SettingsFragment.ID_NAME_SEPARATOR)[0], AccessToken.getCurrentAccessToken().getToken(), callback);
            } else {
                contentLoaderEventsListener.loadingMoreData();
                currentlyLoadingPageCount++;
                requestManager.getPageFeed(nextPagesMap.get(pageIdAndName), callback);
            }
        }
    }

    @NonNull
    private Callback<FbFeedDataResponse> createCallback(final String pageIdAndName) {
        return new Callback<FbFeedDataResponse>() {
            @Override
            public void onResponse(Call<FbFeedDataResponse> call, Response<FbFeedDataResponse> response) {
                counter++;

                String pageName = getPageName(pageIdAndName);
                if (!response.isSuccessful()) {
                    loadedPostsMap.remove(pageIdAndName);
                    nextPagesMap.remove(pageIdAndName);
                    selectedPageIdsAndNames.remove(pageIdAndName);
                    Toast.makeText(context, pageName + " INCORRECT PAGE ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                String next = response.body().paging.next;
                nextPagesMap.put(pageIdAndName, next);

                List<FacebookPost> postsFromResponse = new ArrayList<>(pageCount * 10);
                for (FbFeedDataResponse.FbSinglePostResponse res : response.body().fbSinglePostResponses) {
                    if (res.message == null || res.message.isEmpty()) {
                        postsFromResponse.add(new FacebookPost(pageName, res.id, res.story, res.date));
                    } else {
                        postsFromResponse.add(new FacebookPost(pageName, res.id, res.message, res.date));
                    }
                }
                loadedPostsMap.get(pageIdAndName).addAll(postsFromResponse);
                if (counter >= currentlyLoadingPageCount) {
                    contentLoaderEventsListener.readyToDisplay();
                    counter = 0;
                    currentlyLoadingPageCount = 0;
                }
            }

            @Override
            public void onFailure(Call<FbFeedDataResponse> call, Throwable t) {
                Toast.makeText(context, "CONNECTION ERROR", Toast.LENGTH_SHORT).show();
                Log.w("REST ERROR", t.getMessage());
                Log.w("REST ERROR", t.getStackTrace().toString());
            }
        };
    }

    private String getPageName(String pageIdAndName) {
        return pageIdAndName.split(SettingsFragment.ID_NAME_SEPARATOR)[1];
    }

    public interface ContentLoaderEventsListener {
        void readyToDisplay();

        void loadingMoreData();
    }
}
