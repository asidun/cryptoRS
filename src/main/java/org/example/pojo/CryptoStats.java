package org.example.pojo;

public class CryptoStats {
    private String symbol;
    private long oldest;
    private long newest;
    private double min;
    private double max;

    public CryptoStats(String symbol, long oldest, long newest, double min, double max) {
        this.symbol = symbol;
        this.oldest = oldest;
        this.newest = newest;
        this.min = min;
        this.max = max;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getOldest() {
        return oldest;
    }

    public void setOldest(long oldest) {
        this.oldest = oldest;
    }

    public long getNewest() {
        return newest;
    }

    public void setNewest(long newest) {
        this.newest = newest;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}

