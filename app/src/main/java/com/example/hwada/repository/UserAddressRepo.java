package com.example.hwada.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.userAddress.DaDataRequest;
import com.example.hwada.Model.userAddress.GeolocationResponse;
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
    final String BASE_URL = "https://suggestions.dadata.ru/";
    public static final String Token ="cc64e856f1bdb675fad4ea710a8d8d2943461846";
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
        if(locationCustom!=null) makeRequest(aPiInterface,locationCustom);
        return mutableLiveData;
    }

    private void makeRequest(APiInterface aPiInterface ,LocationCustom locationCustom){
        Log.e(TAG, "make daData Request: ");
        DaDataRequest request = new DaDataRequest(locationCustom);
        Call<GeolocationResponse> call = aPiInterface.getAddress(request);
        call.enqueue(new Callback<GeolocationResponse>() {
            @Override
            public void onResponse(Call<GeolocationResponse> call, Response<GeolocationResponse> response) {
                if(response!=null && response.body()!=null) {
                    if(!response.body().getSuggestions().isEmpty()){
                        String address = response.body().getSuggestions().get(0).getValue();
                        mutableLiveData.setValue(address);
                    }else mutableLiveData.setValue(application.getString(R.string.unsupportedLocation));
                }else mutableLiveData.setValue(application.getString(R.string.unsupportedLocation));
            }

            @Override
            public void onFailure(Call<GeolocationResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}