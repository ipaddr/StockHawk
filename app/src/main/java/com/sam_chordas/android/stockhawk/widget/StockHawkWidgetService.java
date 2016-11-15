package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by ulfiaizzati on 11/15/16.
 */

public class StockHawkWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        StockHawkWidgetDataProvider data = new StockHawkWidgetDataProvider(getApplicationContext(), intent);
        return data;
    }
}
