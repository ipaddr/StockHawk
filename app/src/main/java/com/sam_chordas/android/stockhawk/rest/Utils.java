package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.text.TextUtils;
import android.util.Log;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
            if (isValidJson(jsonObject))
                batchOperations.add(buildBatchOperation(jsonObject));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
                if (isValidJson(jsonObject))
                    batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice){
    bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteProvider.Quotes.CONTENT_URI);
    try {

      String change = jsonObject.getString("Change");
      String symbol = jsonObject.getString("symbol");
      String bid = jsonObject.getString("Bid");
      String changeInPercen = jsonObject.getString("ChangeinPercent");

        if (!TextUtils.isEmpty(change) && !TextUtils.isEmpty(symbol) && !TextUtils.isEmpty(bid) && !TextUtils.isEmpty(changeInPercen)){
            builder.withValue(QuoteColumns.SYMBOL, symbol);
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(bid));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(changeInPercen, true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-'){
                builder.withValue(QuoteColumns.ISUP, 0);
            }else{
                builder.withValue(QuoteColumns.ISUP, 1);
            }
        }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }

    private static boolean isValidJson(JSONObject jsonObject){
        try{
            String change = jsonObject.getString("Change");
            String symbol = jsonObject.getString("symbol");
            String bid = jsonObject.getString("Bid");
            String changeInPercen = jsonObject.getString("ChangeinPercent");

            if (isJsonNotNull(change) && isJsonNotNull(symbol) && isJsonNotNull(bid) && isJsonNotNull(changeInPercen))
                return true;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isJsonNotNull(String text){
        if (null != text && !TextUtils.isEmpty(text) && !text.equalsIgnoreCase("null"))
            return true;
        return false;
    }

    public static String dateToString(Date date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = df.format(date);
        return dateString;
    }

    public static Date fromPrevMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        return  date;
    }
}
