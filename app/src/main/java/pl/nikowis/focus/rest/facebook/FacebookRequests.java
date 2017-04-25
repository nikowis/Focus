package pl.nikowis.focus.rest.facebook;


import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface FacebookRequests {

    @GET("/{user_id}/posts")
    void getUserFeed(@Path("user_id") String userId, @Query("access_token") String accessToken, Callback<FbFeedDataResponse> callback);

    @GET("/{user_id}/likes")
    void getUserLikes(@Path("user_id") String userId, @Query("access_token") String accessToken, Callback<FbLikesDataResponse> callback);

    @GET("/{page_name}/posts")
    void getPageFeed(@Path("page_name") String pageName, @Query("access_token") String accessToken, Callback<FbFeedDataResponse> callback);

    @GET("/posts")
    void getMultiplePagesFeed(@Query("ids") String pagesIdsCommaSeparated, @Query("access_token") String accessToken, Callback<List<FbFeedDataResponse>> callback);
}
