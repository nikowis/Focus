package pl.nikowis.focus.rest.twitter;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class TwitterFollowsDataResponse {

    @SerializedName("data")
    public ArrayList<TwitterSingleFollowResponse> twitterFollows;


    public class TwitterSingleFollowResponse {
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
