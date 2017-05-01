package pl.nikowis.focus.rest.instagram;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class Pagination {
    @SerializedName("next_url")
    public String nextUrl;
    @SerializedName("next_max_id")
    public String nextMaxId;
}
