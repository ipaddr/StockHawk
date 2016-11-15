package com.sam_chordas.android.stockhawk.model;

/**
 * Created by ulfiaizzati on 11/16/16.
 */

public class WidgetItem {

    private String symbol;
    private String bid;
    private String change;

    public WidgetItem(String symbol, String bid, String change) {
        this.symbol = symbol;
        this.bid = bid;
        this.change = change;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
