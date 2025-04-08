package com.example.catventure.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WorldTimeApiService {
    @GET("ip")
    Call<TimeResponse> getCurrentTime();
    @GET("timezone/{region}/{city}")
    Call<TimeResponse> getTime(@Path("region") String region, @Path("city") String city);
}
