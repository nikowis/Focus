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
    private Set<String> pages;
    private Map<String, String> nextPagesMap;
    private Map<String, List<FacebookPost>> loadedPostsMap;
    private List<FacebookPost> queuedPostsList;
    private int pageCount = 10;
    private boolean loadingMoreElementsFromFacebook=true;

    public FacebookFeedLoader(Context context, FacebookPostsAdapter facebookAdapter) {
        this.context = context;
        this.facebookAdapter = facebookAdapter;
        this.visiblePostsList = facebookAdapter.getList();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        pages = prefs.getStringSet(SettingsFragment.KEY_PREF_SELECTED_PAGES, new HashSet<String>());
        nextPagesMap = new LinkedHashMap<>();
        loadedPostsMap = new LinkedHashMap<>();
        queuedPostsList = new ArrayList<>(pageCount*10);
        for (String page : pages) {
            nextPagesMap.put(page, "");
            loadedPostsMap.put(page, new ArrayList<FacebookPost>(pageCount*10));
        }
        for (String page : pages) {
            requestPagePosts(page);
        }
    }

    public void loadContent() {
        if(!loadingMoreElementsFromFacebook) {
            calculateQueuedPostsList();
            visiblePostsList.addAll(queuedPostsList);
            queuedPostsList.clear();
            facebookAdapter.notifyDataSetChanged();
        }
    }

    private void calculateQueuedPostsList() {
        //get first posts in order
        //if list finishes synchronously get more posts
        Map<String, FacebookPost> latestPostsFromEachPage = constructLatestsPostsMap();

        for (int i = 0; i < pageCount; i++) {
            //wybierz najwcześniesjzy usuń go i dopełnij z jego listy
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

    private void requestPagePosts(final String pageName) {
        FacebookRequestManager requestManager = FacebookRequestManager.getInstance(context);
        Callback<FbFeedDataResponse> callback = createCallback(pageName);
        if (nextPagesMap.get(pageName).isEmpty()) {
            requestManager.getPageFeed(pageName, AccessToken.getCurrentAccessToken().getToken(), callback);
        } else {
            requestManager.getPageFeed(nextPagesMap.get(pageName), callback);
        }
    }

    @NonNull
    private Callback<FbFeedDataResponse> createCallback(final String pageName) {
        return new Callback<FbFeedDataResponse>() {
            @Override
            public void onResponse(Call<FbFeedDataResponse> call, Response<FbFeedDataResponse> response) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                String next = response.body().paging.next;
                nextPagesMap.put(pageName, next);

                List<FacebookPost> postsFromResponse = new ArrayList<>(pageCount*10);
                for (FbFeedDataResponse.FbSinglePostResponse res : response.body().fbSinglePostResponses) {
                    postsFromResponse.add(new FacebookPost(pageName, res.message, res.date));
                }
                loadedPostsMap.get(pageName).addAll(postsFromResponse);
                loadingMoreElementsFromFacebook = false;
            }

            @Override
            public void onFailure(Call<FbFeedDataResponse> call, Throwable t) {
                Toast.makeText(context, "REST ERROR", Toast.LENGTH_SHORT).show();
                Log.w("REST ERROR", t.getMessage());
                Log.w("REST ERROR", t.getStackTrace().toString());
            }
        };
    }


}
