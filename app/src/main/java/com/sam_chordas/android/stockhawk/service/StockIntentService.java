package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.model.StockDetail;
import com.sam_chordas.android.stockhawk.rest.IRetrofitAPI;
import com.sam_chordas.android.stockhawk.rest.RetrofitAPI;
import com.sam_chordas.android.stockhawk.rest.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  public static final String NORMAL_ACTION = "com.sam_chordas.android.stockhawk.service.StockIntentService.NORMAL_ACTION";
  public static final String DETAIL_ACTION = "com.sam_chordas.android.stockhawk.service.StockIntentService.DETAIL_ACTION";

  public StockIntentService(){
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(Intent intent) {
    switch (intent.getAction()){
      case NORMAL_ACTION:
        normalAction(intent);
        break;
      case DETAIL_ACTION:
          detailAction(intent);
        break;
      default:break;
    }
  }

  private void normalAction(Intent intent){
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")){
      args.putString("symbol", intent.getStringExtra("symbol"));
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
  }

  private void detailAction(Intent intent){
      String symbol = "";
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")){
        symbol = intent.getStringExtra("symbol");
    }

      IRetrofitAPI api = RetrofitAPI.buildNetworkCall().create(IRetrofitAPI.class);
      String query = "select * from yahoo.finance.historicaldata where symbol = \""+ symbol
              +"\" and startDate = \""+ Utils.dateToString(Utils.fromPrevMonth()) +"\" and endDate = \""+ Utils.dateToString(new Date()) +"\"";
      Call<StockDetail> data = api.getStockDetail(query);
      try {
          EventBus.getDefault().post(data.execute().body());
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
}
