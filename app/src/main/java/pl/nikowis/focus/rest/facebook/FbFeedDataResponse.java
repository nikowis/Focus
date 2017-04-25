package pl.nikowis.focus.rest.facebook;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Nikodem on 4/25/2017.
 */

public class FbFeedDataResponse {

    @SerializedName("data")
    public ArrayList<FbSinglePostResponse> fbSinglePostResponses;

    @Override
    public String toString() {
        return "FbFeedDataResponse{" +
                "fbSinglePostResponses=" + fbSinglePostResponses +
                '}';
    }
}
