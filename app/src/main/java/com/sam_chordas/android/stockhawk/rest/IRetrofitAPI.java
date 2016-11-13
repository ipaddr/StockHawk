package com.sam_chordas.android.stockhawk.rest;

import com.sam_chordas.android.stockhawk.model.StockDetail;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ulfiaizzati on 11/13/16.
 */

public interface IRetrofitAPI {

    @GET("/v1/public/yql?format=json&env=http://datatables.org/alltables.env")
    Call<StockDetail> getStockDetail(@Query("q") String query);
//    Call<StockDetail> getStockDetail();

}
