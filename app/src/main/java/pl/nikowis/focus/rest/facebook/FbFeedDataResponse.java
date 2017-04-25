package pl.nikowis.focus.rest.facebook;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Nikodem on 4/25/2017.
 */

public class FbFeedDataResponse {

    @SerializedName("data")
    public ArrayList<FbSinglePostResponse> fbSinglePostResponses;

    @SerializedName("paging")
    public Paging paging;

    @Override
    public String toString() {
        return "FbFeedDataResponse{" +
                "fbSinglePostResponses=" + fbSinglePostResponses +
                '}';
    }

    public static class FbSinglePostResponse {

        @SerializedName("message")
        public String message;
        @SerializedName("story")
        public String story;
        @SerializedName("created_time")
        public String login;
        @SerializedName("id")
        public String id;

        @Override
        public String toString() {
            return "FbSinglePostResponse{" +
                    "message='" + message + '\'' +
                    ", story='" + story + '\'' +
                    ", login='" + login + '\'' +
                    ", id=" + id +
                    '}';
        }
    }
}
