package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.model.WidgetItem;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ulfiaizzati on 11/15/16.
 */

public class StockHawkWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {


    private Context mContext;
    private List<WidgetItem> mCollection = new ArrayList();

    public StockHawkWidgetDataProvider(Context context, Intent intent){
        this.mContext = context;
    }

    private void initData(){
        mCollection.clear();
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        if (cursor.moveToFirst()){
            while (cursor.moveToNext()){
                String symbol = cursor.getString(cursor.getColumnIndex("symbol"));
                String bidPrice = cursor.getString(cursor.getColumnIndex("bid_price"));
                String change = "";
                if (Utils.showPercent){
                    change = cursor.getString(cursor.getColumnIndex("percent_change"));
                } else{
                    change = cursor.getString(cursor.getColumnIndex("change"));
                }
                mCollection.add(new WidgetItem(symbol, bidPrice, change));
            }
        }
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        WidgetItem wi = mCollection.get(position);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        views.setTextViewText(R.id.stock_symbol, wi.getSymbol());
        views.setTextViewText(R.id.bid_price, wi.getBid());
        views.setTextViewText(R.id.change, wi.getChange());
        views.setTextColor(R.id.stock_symbol, Color.WHITE);
        views.setTextColor(R.id.bid_price, Color.WHITE);
        views.setTextColor(R.id.change, Color.WHITE);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
