package app.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WorldTimeApiService {
    @GET("api/timezone/{region}/{city}")
    Call<TimeResponse> getTime(@Path("region") String region, @Path("city") String city);
}
