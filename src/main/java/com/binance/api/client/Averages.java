package com.binance.api.client;

public class Averages {
    public static void main(String[] args) {

    }

    double calculateSMA(double[] data) {
        double sum = 0;
        for (int i = 0; i < 20; i++) {
            sum += data[i];
        }

        return sum / data.length;
    }

    double calculateEMA(double c, double p) {
        double k = 0.095238;

        double EMA = (k / (c - p)) + p;

        return EMA;
    }
}
