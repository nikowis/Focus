package pl.nikowis.focus.ui.gmail;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikodem on 5/3/2017.
 */

public class GmailFeedLoader {
    private Context context;
    private GmailPostsAdapter gmailAdapter;
    private List<GmailMessage> visibleMailList;
    private List<String> loadedMailIds;
    private final List<GmailMessage> loadedMailList;
    private static final int PAGE_COUNT = 10;
    private GmailFeedLoader.ContentLoaderEventsListener contentLoaderEventsListener;
    private GoogleAccountCredential mCredential;


    public GmailFeedLoader(Context context, GoogleAccountCredential mCredential, GmailPostsAdapter gmailAdapter, ContentLoaderEventsListener listener) {
        this.context = context;
        this.gmailAdapter = gmailAdapter;
        this.visibleMailList = gmailAdapter.getList();
        this.contentLoaderEventsListener = listener;
        this.mCredential = mCredential;
        visibleMailList.clear();
        loadedMailIds = new ArrayList<>(PAGE_COUNT * 10);
        loadedMailList = new ArrayList<>(PAGE_COUNT * 10);
        contentLoaderEventsListener.loadingMoreData();
        loadContent();
    }

    public void loadContent() {
        if (loadedMailList.size() < PAGE_COUNT + 1) {
            contentLoaderEventsListener.loadingMoreData();
            if (loadedMailIds.size() < PAGE_COUNT + 1) {
                new MailIdsLoader(mCredential).execute();
                return;
            }
            new MailLoader(mCredential).execute();
            return;
        }
        for (int i = 0; i < PAGE_COUNT; i++) {
            visibleMailList.add(loadedMailList.remove(0));
        }
        contentLoaderEventsListener.readyToDisplay();
        gmailAdapter.notifyDataSetChanged();
    }


    private class MailIdsLoader extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;
        private String nextPage;

        public MailIdsLoader(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential
            ).setApplicationName("Gmail API Android Quickstart").build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                ListMessagesResponse messagesResponse;
                if (nextPage == null || nextPage.isEmpty()) {
                    messagesResponse = mService.users().messages().list("me").execute();
                } else {
                    messagesResponse = mService.users().messages().list("me").setPageToken(nextPage).execute();
                }

                List<String> ids = new ArrayList<>();
                List<Message> messages = messagesResponse.getMessages();
                nextPage = messagesResponse.getNextPageToken();
                for (Message mes : messages) {
                    Log.w("GMAIL LOADED MESSAGE ID", mes.getId());
                    ids.add(mes.getId());
                }

                return ids;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
                contentLoaderEventsListener.readyToDisplay();
                Toast.makeText(context, "No results returned", Toast.LENGTH_SHORT).show();
            } else {
                loadedMailIds.addAll(output);
                loadContent();
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                Log.w("ERROR GMAIL API", mLastError.getMessage());
                Toast.makeText(context, "Error connecting to gmail", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Gmail request cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MailLoader extends AsyncTask<Void, Void, List<GmailMessage>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        public MailLoader(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential
            ).setApplicationName("Gmail API Android Quickstart").build();
        }

        @Override
        protected List<GmailMessage> doInBackground(Void... params) {
            try {
                List<GmailMessage> gmailMessages = new ArrayList<>();
                List<String> selectedIds = new ArrayList<>(PAGE_COUNT+1);
                for(int i=0; i< PAGE_COUNT +1;i++) {
                    if(loadedMailIds.isEmpty()) {
                        break;
                    }
                    selectedIds.add(loadedMailIds.remove(0));
                }
                for (String id : selectedIds) {
                    Message fullMessage = mService.users().messages().get("me", id).execute();
                    String subject = "";
                    List<MessagePartHeader> headers = fullMessage.getPayload().getHeaders();
                    for(MessagePartHeader h : headers) {
                        if(h.getName().equals("Subject")){
                            subject = h.getValue();
                        }
                    }
                    gmailMessages.add(new GmailMessage(subject, fullMessage.getSnippet(), fullMessage.getInternalDate(), fullMessage.getId()));
                    Log.w("GMAIL FETCHED FULL : ", fullMessage.getSnippet());
                }
                return gmailMessages;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<GmailMessage> output) {
            loadedMailIds.clear();
            if (output == null || output.size() == 0) {
                Toast.makeText(context, "No results returned", Toast.LENGTH_SHORT).show();
                contentLoaderEventsListener.readyToDisplay();
            } else {
                loadedMailList.addAll(output);
                loadContent();
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                Log.w("ERROR GMAIL API", mLastError.getMessage());
                Toast.makeText(context, "Error connecting to gmail", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Gmail request cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface ContentLoaderEventsListener {
        void readyToDisplay();

        void loadingMoreData();
    }
}
