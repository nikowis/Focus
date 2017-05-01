package pl.nikowis.focus.rest.instagram;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class InstaPostsDataResponse {
    @SerializedName("meta")
    public Meta meta;
    @SerializedName("data")
    public ArrayList<InstaSinglePostResponse> instaPosts;
    @SerializedName("pagination")
    public Pagination pagination;

    public class InstaSinglePostResponse {
        @SerializedName("user")
        public User user;
        @SerializedName("link")
        public String link;
        @SerializedName("created_time")
        public String createdTime;
        @SerializedName("images")
        public Images images;

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
