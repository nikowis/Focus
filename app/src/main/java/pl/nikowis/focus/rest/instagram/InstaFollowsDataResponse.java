package pl.nikowis.focus.rest.instagram;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class InstaFollowsDataResponse {
    @SerializedName("meta")
    public Meta meta;
    @SerializedName("data")
    public ArrayList<InstaSingleFollowResponse> instaFollows;
    @SerializedName("pagination")
    public Pagination pagination;

    public class InstaSingleFollowResponse {
        @SerializedName("username")
        public String username;
        @SerializedName("profile_picture")
        public String profile_picture;
        @SerializedName("full_name")
        public String fullName;
        @SerializedName("id")
        public String id;
    }

}
