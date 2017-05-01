package pl.nikowis.focus.rest.instagram;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class Meta {
    @SerializedName("error_type")
    public String errorType;
    @SerializedName("code")
    public String code;
    @SerializedName("error_message")
    public String errorMessage;
}
