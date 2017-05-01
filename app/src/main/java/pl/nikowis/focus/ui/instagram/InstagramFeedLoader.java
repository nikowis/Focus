package pl.nikowis.focus.ui.instagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class InstagramFeedLoader {

    private InstagramPostAdapter instagramAdapter;
    private Context context;
    private List<InstagramPost> visiblePostsList;
    private Set<String> selectedPageIdsAndNames;
    private Map<String, String> nextPagesMap;
    private Map<String, List<InstagramPost>> loadedPostsMap;
    private List<InstagramPost> queuedPostsList;
    private int pageCount = 10;
    private boolean usingCustomPages;
    private ContentLoaderEventsListener contentLoaderEventsListener;
    private int counter, currentlyLoadingPageCount;

    public InstagramFeedLoader(Context context, InstagramPostAdapter instagramAdapter, ContentLoaderEventsListener listener) {
        this.context = context;
        this.instagramAdapter = instagramAdapter;
        this.visiblePostsList = instagramAdapter.getList();
        this.contentLoaderEventsListener = listener;
        visiblePostsList.clear();
    }

    public void loadContent() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String authorizationToken = prefs.getString(InstagramSettings.KEY_PREF_INSTAGRAM_AUTH_TOKEN, null);
        String userId = prefs.getString(InstagramSettings.KEY_PREF_INSTAGRAM_USER_ID, null);
    }

    public interface ContentLoaderEventsListener {
        void readyToDisplay();

        void loadingMoreData();
    }
}
