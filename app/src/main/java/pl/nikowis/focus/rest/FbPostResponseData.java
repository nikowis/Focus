package pl.nikowis.focus.rest;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Nikodem on 4/25/2017.
 */

public class FbPostResponseData {

    @SerializedName("data")
    public ArrayList<FbPostResponse> fbPostResponses;

    @Override
    public String toString() {
        return "FbPostResponseData{" +
                "fbPostResponses=" + fbPostResponses +
                '}';
    }
}
