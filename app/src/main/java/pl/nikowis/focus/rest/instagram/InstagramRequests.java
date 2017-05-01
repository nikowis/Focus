package pl.nikowis.focus.rest.instagram;

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

public interface InstagramRequests {

    @FormUrlEncoded
    @POST("/oauth/access_token/")
    Call<InstaLoginResponse> getTokenWithCodeEncoded(
            @Field("client_id") String clientId
            , @Field("client_secret") String clientSecret
            , @Field("grant_type") String authorization_code
            , @Field("redirect_uri") String redirect_uri
            , @Field("code") String code
    );

    @GET("/v1/users/self/follows")
    Call<InstaFollowsDataResponse> getFollowedUsers(@Query("access_token") String accessToken);

    @GET("/v1/users/{user_id}/media/recent")
    Call<InstaPostsDataResponse> getUserFeed(@Path("user_id") String userId, @Query("access_token") String accessToken);

    @GET
    Call<InstaFollowsDataResponse> getNextFollowedUsers(@Url String fullUrl);

    @GET
    Call<InstaPostsDataResponse> getUserFeed(@Url String fullUrl);
}

