package pl.nikowis.focus.rest.twitter;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Nikodem on 4/30/2017.
 */

public interface TwitterRequests {

    @FormUrlEncoded
    @POST("/oauth/access_token/")
    Call<TwitterLoginResponse> getTokenWithCodeEncoded(
            @Field("client_id") String clientId
            , @Field("client_secret") String clientSecret
            , @Field("grant_type") String authorization_code
            , @Field("redirect_uri") String redirect_uri
            , @Field("code") String code
    );

    @GET("/v1/users/self/follows")
    Call<TwitterFollowsDataResponse> getFollowedUsers(@Query("access_token") String accessToken);

    @GET("/v1/users/{user_id}/media/recent")
    Call<TwitterFeedDataResponse> getUserFeed(@Path("user_id") String userId, @Query("access_token") String accessToken);

    @GET
    Call<TwitterFollowsDataResponse> getNextFollowedUsers(@Url String fullUrl);

    @GET
    Call<TwitterFeedDataResponse> getUserFeed(@Url String fullUrl);
}

