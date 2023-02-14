package com.example.hwada.util.retrofit;

import com.example.hwada.Model.userAddress.DaDataRequest;
import com.example.hwada.Model.userAddress.GeolocationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APiInterface {

    @Headers("Authorization: Token cc64e856f1bdb675fad4ea710a8d8d2943461846")
    @POST("suggestions/api/4_1/rs/geolocate/address")
    Call<GeolocationResponse>getAddress(@Body DaDataRequest request);
}
