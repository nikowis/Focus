package pl.nikowis.focus.rest.facebook;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Nikodem on 4/25/2017.
 */

public class FbLikesDataResponse {
    @SerializedName("data")
    public ArrayList<FbSingleLikeResponse> fbSingleLikeResponses;

    @SerializedName("paging")
    public Paging paging;

    @Override
    public String toString() {
        return "FbLikesDataResponse{" +
                "fbSinglePostResponses=" + fbSingleLikeResponses +
                '}';
    }

    public class FbSingleLikeResponse {
        @SerializedName("name")
        public String name;
        @SerializedName("created_time")
        public String date;
        @SerializedName("id")
        public String id;

        @Override
        public String toString() {
            return "FbSingleLikeResponse{" +
                    "name='" + name + '\'' +
                    ", date='" + date + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
}
