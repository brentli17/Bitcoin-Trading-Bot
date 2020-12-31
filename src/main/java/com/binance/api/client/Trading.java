package com.binance.api.client;

import com.binance.api.client.domain.market.TickerStatistics;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class Trading {
    private double previousPrice;   //price of btc in previous transaction
    private double currentPrice;    //current price of bitcoin
    private double usdtWallet;      //currently held $ in USD
    private double btcWallet;       //currently held $ in BTC
    private double transactionThreshold; //min change in bitcoin price to warrant a transaction
    private String lastTransaction; //last transaction type that was carried out (buy/sell)
    private java.io.File log;      //log file
    public boolean stopTrading;     //boolean to start and stop trading
    public long tradingFrequency;   //how often the bot will check the price of BTC (using binance api)

    //constructor
    public Trading(double previousPrice, double transactionThreshold, double usdtWallet, double btcWallet, long tradingFrequency, String lastTransaction){
        this.previousPrice = previousPrice;
        this.transactionThreshold = transactionThreshold;
        this.usdtWallet = usdtWallet;
        this.btcWallet = btcWallet;
        this.tradingFrequency = tradingFrequency;
        this.lastTransaction = lastTransaction;
        currentPrice = 0.00;
        stopTrading = false;
    }

    public void tradingMain() throws InterruptedException, IOException {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        double change;  //$ difference in BTC price since last transaction

        BufferedWriter logWriter = new BufferedWriter(new FileWriter(log.getPath()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        //write first line in log
        logWriter.write(Double.toString(transactionThreshold) + ',' + Double.toString(tradingFrequency) + "\n");

        //main loop. as long as this runs, the bot will continue to trade
        while(!stopTrading){
            TickerStatistics tickerStatistics = client.get24HrPriceStatistics("BTCUSDT");
            currentPrice = Double.parseDouble(tickerStatistics.getLastPrice()); //get current price

            change = currentPrice - previousPrice;
            System.out.println(currentPrice + " $" + change);

            if(Math.abs(change) >= transactionThreshold){
                if(lastTransaction.equals("buy") && (change > transactionThreshold)){   //need to sell
                    System.out.println("\nCurrent Price: " + currentPrice + "    Previous Price: " + previousPrice);
                    System.out.println("Selling...");
                    sell(currentPrice);
                    previousPrice = currentPrice;

                    //log transaction
                    logWriter.write(formatter.format(date) + ',' + btcWallet + ',' + usdtWallet + ',' + currentPrice + ',' + lastTransaction + ',' + change + "\n");
                }
                else if(lastTransaction.equals("sell") && (change < transactionThreshold)){ //need to buy
                    System.out.println("\nCurrent Price: " + currentPrice + "    Previous Price: " + previousPrice);
                    System.out.println("Buying...");
                    buy(currentPrice);
                    previousPrice = currentPrice;

                    //log transaction
                    logWriter.write(formatter.format(date) + ',' + btcWallet + ',' + usdtWallet + ',' + currentPrice + ',' + lastTransaction + ',' + change + "\n");
                }
            }
            Thread.sleep(tradingFrequency * 1000);    //wait 1 second
        }
        logWriter.close();
    }

    //gets the file location for the log
    public void logPrompt(){
        JFileChooser fileChooser = new JFileChooser("Transaction log location");
        fileChooser.showOpenDialog(null);   //open file chooser
        log = fileChooser.getSelectedFile();
    }

    public void sell(double price){
        lastTransaction = "sell";
        usdtWallet = btcWallet * currentPrice;
        System.out.println("Sold " + btcWallet + " bitcoin for $" + usdtWallet + "\n");
        btcWallet = 0.00;
    }

    public void buy(double price){
        lastTransaction = "buy";
        btcWallet = usdtWallet / currentPrice;
        System.out.println("Bought " + btcWallet + " bitcoin for $" + usdtWallet + "\n");
        usdtWallet = 0.00;
    }

    //mutator to start/stop trading (pretty much only used to stop trading)
    public void setStopTrading(boolean trading){
        stopTrading = trading;
    }
}