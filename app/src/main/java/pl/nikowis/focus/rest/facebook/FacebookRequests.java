package pl.nikowis.focus.rest.facebook;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface FacebookRequests {

    @GET("/{user_id}/posts")
    void getUserFeed(@Path("user_id") String userId, @Query("access_token") String accessToken, Callback<FbFeedDataResponse> callback);

}
