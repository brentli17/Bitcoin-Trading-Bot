package com.binance.api.client;

public class TradeAI {
    double[] btcPrice;

    //EMA

    // For period 21, prevAvg = SMA calculated for last 20 periods
    // After period 21, prevAvg = previous EMA
    public double calculateEMA(double currPrice, double prevAvg, double periodNum) {
        // prevAvg = previous period's SMA or EMA
        // double k = 0.095238;
        double k = 2 / (periodNum + 1);

        double EMA = (k / (currPrice - prevAvg)) + prevAvg;
        return EMA;
    }

    //SMA
    public void add(double price){
        
    }

    public void average(){

    }
}
