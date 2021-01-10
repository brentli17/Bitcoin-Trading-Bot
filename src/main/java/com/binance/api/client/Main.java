package com.binance.api.client;

class Main {
    public static void main(String[] args){
        GUI gui = new GUI();
        gui.startWindow();
    }

    public double calculateSMA(double[] data) {
        double sum = 0;
        for (int i = 0; i < 20; i++) {
            sum += data[i];
        }

        return sum / data.length;
    }

    public double calculateEMA(double c, double p) {
        double k = 0.095238;

        double EMA = (k / (c - p)) + p;

        return EMA;
    }
}