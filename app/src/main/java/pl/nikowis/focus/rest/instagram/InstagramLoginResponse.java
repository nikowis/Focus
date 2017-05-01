package pl.nikowis.focus.rest.instagram;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class InstagramLoginResponse {
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("user")
    public User user;

    public class User {
        @SerializedName("id")
        public String id;
        @SerializedName("username")
        public String username;
        @SerializedName("full_name")
        public String fullName;

    }
}
