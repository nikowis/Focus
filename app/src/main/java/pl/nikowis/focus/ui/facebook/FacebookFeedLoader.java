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
import java.util.LinkedHashSet;
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
    private Set<String> selectedPageIds;
    private Map<String, String> nextPagesMap;
    private Map<String, List<FacebookPost>> loadedPostsMap;
    private List<FacebookPost> queuedPostsList;
    private int pageCount = 10;
    private boolean loadingMoreElementsFromFacebook;
    private boolean loadedFirstRecords;
    private Set<String> pagesIds;
    private Set<String> pagesNames;

    public FacebookFeedLoader(Context context, FacebookPostsAdapter facebookAdapter) {
        this.context = context;
        this.facebookAdapter = facebookAdapter;
        this.visiblePostsList = facebookAdapter.getList();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        selectedPageIds = prefs.getStringSet(SettingsFragment.KEY_PREF_SELECTED_PAGES, new HashSet<String>());

        pagesIds = prefs.getStringSet(SettingsFragment.KEY_PREF_LIKED_PAGES_IDS, new HashSet<String>());
        pagesNames = prefs.getStringSet(SettingsFragment.KEY_PREF_LIKED_PAGES_NAMES, new HashSet<String>());

        nextPagesMap = new LinkedHashMap<>();
        loadedPostsMap = new LinkedHashMap<>();
        queuedPostsList = new ArrayList<>(pageCount * 10);
        for (String page : selectedPageIds) {
            nextPagesMap.put(page, "");
            loadedPostsMap.put(page, new ArrayList<FacebookPost>(pageCount * 10));
        }
        for (String pageId : selectedPageIds) {
            requestPagePosts(pageId);
        }
    }

    public void loadContent() {
        if (!loadedFirstRecords) {
            for (List<FacebookPost> list : loadedPostsMap.values()) {
                list.clear();
            }
            loadingMoreElementsFromFacebook = true;
            for (String page : selectedPageIds) {
                requestPagePosts(page);
            }
        } else if (!loadingMoreElementsFromFacebook) {
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
                loadingMoreElementsFromFacebook = true;
                requestPagePosts(key);
            }
            latestPostsFromEachPage.put(key, facebookPosts.get(0));
        }
        return latestPostsFromEachPage;
    }

    //działa poprawnie
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

    private void requestPagePosts(final String pageId) {
        FacebookRequestManager requestManager = FacebookRequestManager.getInstance(context);
        Callback<FbFeedDataResponse> callback = createCallback(pageId);
        if (nextPagesMap.get(pageId).isEmpty()) {
            requestManager.getPageFeed(pageId, AccessToken.getCurrentAccessToken().getToken(), callback);
        } else {
            requestManager.getPageFeed(nextPagesMap.get(pageId), callback);
        }
    }

    @NonNull
    private Callback<FbFeedDataResponse> createCallback(final String pageId) {
        return new Callback<FbFeedDataResponse>() {
            @Override
            public void onResponse(Call<FbFeedDataResponse> call, Response<FbFeedDataResponse> response) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                if (!response.isSuccessful()) {
                    loadedPostsMap.remove(pageId);
                    nextPagesMap.remove(pageId);
                    selectedPageIds.remove(pageId);
                    Toast.makeText(context, pageId + " INCORRECT ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                String next = response.body().paging.next;
                nextPagesMap.put(pageId, next);
                String pageName = getPageName(pageId);
                List<FacebookPost> postsFromResponse = new ArrayList<>(pageCount * 10);
                for (FbFeedDataResponse.FbSinglePostResponse res : response.body().fbSinglePostResponses) {
                    postsFromResponse.add(new FacebookPost(pageName, res.message, res.date));
                }
                loadedPostsMap.get(pageId).addAll(postsFromResponse);
                loadingMoreElementsFromFacebook = false;
                loadedFirstRecords = true;
            }

            @Override
            public void onFailure(Call<FbFeedDataResponse> call, Throwable t) {
                Toast.makeText(context, "CONNECTION ERROR", Toast.LENGTH_SHORT).show();
                Log.w("REST ERROR", t.getMessage());
                Log.w("REST ERROR", t.getStackTrace().toString());
            }
        };
    }

    private String getPageName(String pageId) {
        int index = getIndex(pagesIds, pageId);
        int i = 0;
        String res = "";
        for (String name:pagesNames) {
            if(i==index) {
                res = name;
                break;
            }
            i++;
        }
        return res;
    }

    public static int getIndex(Set<? extends Object> set, Object value) {
        int result = 0;
        for (Object entry:set) {
            if (entry.equals(value)) return result;
            result++;
        }
        return -1;
    }
}
