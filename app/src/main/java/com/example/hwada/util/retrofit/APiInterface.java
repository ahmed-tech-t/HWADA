package com.example.hwada.util.retrofit;

import com.example.hwada.Model.userAddress.OsmResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface APiInterface {

    @GET("reverse-geocode-client")
    Call<OsmResponse>getAddress(@Query("lat") double lat , @Query("lon") double lon, @Query("format") String format);
}
