package pl.nikowis.focus.rest.instagram;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Nikodem on 4/30/2017.
 */

public interface InstagramRequests {

    @FormUrlEncoded
    @POST("/oauth/access_token/")
    Call<InstagramLoginResponse> getTokenWithCodeEncoded(
            @Field("client_id") String clientId
            ,@Field("client_secret") String clientSecret
            , @Field("grant_type") String authorization_code
            , @Field("redirect_uri") String redirect_uri
            , @Field("code") String code
    );
}

