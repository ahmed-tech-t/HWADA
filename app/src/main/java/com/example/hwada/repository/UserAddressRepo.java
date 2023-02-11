package com.example.hwada.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.userAddress.OsmResponse;
import com.example.hwada.R;
import com.example.hwada.util.retrofit.APiInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAddressRepo {
    private static final String TAG = "UserAddressRepo";
    final String BASE_URL = "https://nominatim.openstreetmap.org/";
   public MutableLiveData<String> mutableLiveData;

    Application application ;

    public UserAddressRepo(Application application){
        this.application =  application;
        mutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<String> getUserAddress(LocationCustom locationCustom) {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        APiInterface aPiInterface = retrofit.create(APiInterface.class);
        makeRequest(aPiInterface,locationCustom);
        return mutableLiveData;
    }

    private void makeRequest(APiInterface aPiInterface ,LocationCustom locationCustom){
        Call<OsmResponse> call = aPiInterface.getAddress(locationCustom.getLatitude(),locationCustom.getLongitude() ,"json");

        call.enqueue(new Callback<OsmResponse>() {
            @Override
            public void onResponse(Call<OsmResponse> call, Response<OsmResponse> response) {
                mutableLiveData.setValue(response.body().getDisplay_name());
                return;
            }

            @Override
            public void onFailure(Call<OsmResponse> call, Throwable t) {
                mutableLiveData.setValue(application.getString(R.string.faildToLoadYourLocation));
                makeRequest(aPiInterface,locationCustom);
            }
        });
    }
}