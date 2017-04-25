package pl.nikowis.focus.rest.facebook;


import com.google.gson.annotations.SerializedName;

public class FbSinglePostResponse {

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
