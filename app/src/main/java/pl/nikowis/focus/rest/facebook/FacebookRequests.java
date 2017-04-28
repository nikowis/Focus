package pl.nikowis.focus.rest.facebook;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface FacebookRequests {

    @GET("/{user_id}/posts")
    Call<FbFeedDataResponse> getUserFeed(@Path("user_id") String userId, @Query("access_token") String accessToken);

    @GET("/{user_id}/likes")
    Call<FbLikesDataResponse> getUserLikes(@Path("user_id") String userId, @Query("access_token") String accessToken);

    @GET("/{page_name}/posts")
    Call<FbFeedDataResponse> getPageFeed(@Path("page_name") String pageName, @Query("access_token") String accessToken);

    @GET
    Call<FbFeedDataResponse> getPageFeed(@Url String fullUrl);

    @GET
    Call<FbLikesDataResponse> getUserLikes(@Url String fullUrl);
}
