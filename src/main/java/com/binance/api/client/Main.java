package com.binance.api.client;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import com.binance.api.client.domain.market.TickerStatistics;
import sun.jvm.hotspot.types.JByteField;

import java.util.Scanner;

public class Main {
    private static double previousPrice;
    private static double currentPrice;
    private static double usdtWallet;
    private static double btcWallet;
    private static double transactionThreshold; //min change in bitcoin price to warrant a transaction
    private static String lastTransaction;  //last transaction type that was carried out (buy/sell)

    public Main(){
        previousPrice = 0.00;
        currentPrice = 0.00;
        transactionThreshold = 0.00;
        lastTransaction = "";
        usdtWallet = 0.00;
        btcWallet = 0.00;
    }

    public static void main(String[] args) throws InterruptedException {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        JFrame frame = new JFrame("Bitcoin Trading Bot");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());


        Scanner scanner = new Scanner(System.in);
        System.out.println("Previous Price: ");
        previousPrice = scanner.nextDouble();
        System.out.println("Transaction Threshold: ");
        transactionThreshold = scanner.nextDouble();
        System.out.println("Current usdt wallet amount");
        usdtWallet = scanner.nextDouble();
        currentPrice = 0.00;
        btcWallet = 0.00;
        lastTransaction = "sell";

        double change = 0.0023471;

        while (true){   //get the price every 30 seconds and do the subsequent actions depending on the price
            TickerStatistics tickerStatistics = client.get24HrPriceStatistics("BTCUSDT");
            currentPrice = Double.parseDouble(tickerStatistics.getLastPrice()); //get current price

            change = currentPrice - previousPrice;
            System.out.println(currentPrice + " $" + change);

            if(Math.abs(change) > transactionThreshold){
                System.out.println();
                System.out.println("Current Price: " + currentPrice + "    Previous Price: " + previousPrice);
                if(lastTransaction.equals("buy") && (change > transactionThreshold)){
                    System.out.println("Selling...");
                    sell(currentPrice);
                    previousPrice = currentPrice;
                }
                else if(lastTransaction.equals("sell") && (change > transactionThreshold)){
                    System.out.println("Buying...");
                    buy(currentPrice);
                    previousPrice = currentPrice;
                }
                System.out.println();
            }
            Thread.sleep(1 * 1000);    //wait 1 second
        }
    }

    public static void sell(double price){
        lastTransaction = "sell";
        usdtWallet = btcWallet * currentPrice;
        System.out.println("Sold " + btcWallet + " bitcoin for $" + usdtWallet);
        btcWallet = 0.00;
    }

    public static void buy(double price){
        lastTransaction = "buy";
        btcWallet = usdtWallet / currentPrice;
        System.out.println("Bought " + btcWallet + " bitcoin for $" + usdtWallet);
        usdtWallet = 0.00;
    }
}