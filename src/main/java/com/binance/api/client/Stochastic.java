package com.binance.api.client;

public class Stochastic {
    public void stochasticOscillator(double C, double L, double H) {
        // C = closing price
        // L = lowest price in last period
        // H = highest price in last period
        double equation = ((C - L) / (H - L)) * 100;
    }
}
