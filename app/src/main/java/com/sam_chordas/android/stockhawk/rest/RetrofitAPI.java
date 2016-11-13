package com.sam_chordas.android.stockhawk.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ulfiaizzati on 11/13/16.
 */

public class RetrofitAPI {

    public static Retrofit buildNetworkCall(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
