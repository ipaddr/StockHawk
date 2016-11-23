package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.model.StockDetail;
import com.sam_chordas.android.stockhawk.service.StockIntentService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class LineChartActivity1 extends AppCompatActivity{

    private LineChart mChart;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_linechart);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.WHITE);

        tv = (TextView)findViewById(R.id.empty_chart);

        getNetworkDetail();

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void getNetworkDetail(){
        if (getIntent() != null && getIntent().getAction() != null){
            String symbol = getIntent().getAction();
            getSupportActionBar().setTitle(symbol);
            Intent intent = new Intent(this, StockIntentService.class);
            intent.setAction(StockIntentService.DETAIL_ACTION);
            intent.putExtra("tag", "add");
            intent.putExtra("symbol", symbol);
            startService(intent);
            show(true);
        } else {
            show(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StockDetail stockDetail) {
        if (stockDetail != null){
            // add data
            setData(stockDetail.getQuery().getResults().getQuote());
            show(true);
        } else {
            show(false);
        }
    }

    private void setData(List<Quote> quotes) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        int i = 0;
        for (Quote quote : quotes) {
            values.add(new Entry(i, Float.valueOf(quote.getClose())));
            i++;
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, getString(R.string.data_set_one));

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setValueTextColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 a        nd above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_black);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.WHITE);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }

        mChart.invalidate();
    }

    private void show(boolean isDataAvailable){
        if (isDataAvailable){
            mChart.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
        } else {
            mChart.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }
    }
}
