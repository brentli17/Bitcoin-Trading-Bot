package com.binance.api.client;

import com.binance.api.client.domain.market.TickerStatistics;

public class MacD {
    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
    BinanceApiRestClient client = factory.newRestClient();

    public static void main(String[] args) {

    }

    double calculateSMA(double[] data) {
        TickerStatistics tickerStatistics = client.get24HrPriceStatistics("BTCUSDT");
        double sum = 0;
        for (int i = 0; i < 20; i++) {
            double currentPrice = Double.parseDouble(tickerStatistics.getLastPrice());
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