package pl.nikowis.focus.rest;


import com.google.gson.annotations.SerializedName;

public class FbPostResponse {

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
        return "FbPostResponse{" +
                "message='" + message + '\'' +
                ", story='" + story + '\'' +
                ", login='" + login + '\'' +
                ", id=" + id +
                '}';
    }
}
