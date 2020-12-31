package com.binance.api.client;

import com.binance.api.client.domain.market.TickerStatistics;
import java.util.*;

public class Stochastic {
    public static void main(String[] args) throws InterruptedException {
        double[] prices = getPriceHistory(1);
        stochasticOscillator(prices);
    }

    public static void stochasticOscillator(double[] prices) {
        // C = closing price
        // L = lowest price in last period
        // H = highest price in last period
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        TickerStatistics tickerStatistics = client.get24HrPriceStatistics("BTCUSDT");
        double C = Double.parseDouble(tickerStatistics.getLastPrice()); //get current price
        double L = prices[0];
        double H = prices[prices.length - 1];

        double kPercent = ((C - L) / (H - L)) * 100;
        System.out.println(kPercent);
    }

    public static double[] getPriceHistory(long tradingFrequency) throws InterruptedException {
        // Need to get last 14 prices
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        TickerStatistics tickerStatistics = client.get24HrPriceStatistics("BTCUSDT");
        double[] prices = new double[14];

        for (int i = 0; i < 14; i++) {
            double currentPrice = Double.parseDouble(tickerStatistics.getLastPrice()); //get current price
            prices[i] = currentPrice;
            Thread.sleep(tradingFrequency * 1000);    //wait 1 second
        }

        // Need to get lowest/highest price of last 14 sessions
        Arrays.sort(prices);
        return prices;
    }
}

