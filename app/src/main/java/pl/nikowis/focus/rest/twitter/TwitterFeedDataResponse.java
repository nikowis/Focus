package pl.nikowis.focus.rest.twitter;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class TwitterFeedDataResponse {

    @SerializedName("data")
    public ArrayList<TwitterSinglePostResponse> twitterPosts;


    public class TwitterSinglePostResponse {
        @SerializedName("caption")
        public Caption caption;
        @SerializedName("user")
        public User user;
        @SerializedName("link")
        public String link;
        @SerializedName("created_time")
        public long createdTime;
        @SerializedName("images")
        public Images images;

        public class Caption {
            @SerializedName("text")
            public String text;
        }

        public class User {
            @SerializedName("username")
            public String username;
        }

        public class Images {
            @SerializedName("thumbnail")
            public Thumbnail thumbnail;

            public class Thumbnail {
                @SerializedName("url")
                public String url;
                @SerializedName("width")
                public String width;
                @SerializedName("height")
                public String height;
            }
        }


    }

}
