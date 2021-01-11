package com.binance.api.client;

public class TradeAI {
    double[] btcPrice;
    int period;

    public TradeAI(int period){
        btcPrice = new double[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        this.period = period;
    }

    //EMA
    // For period 21, prevAvg = SMA calculated for last 20 periods
    // After period 21, prevAvg = previous EMA
    public double calculateEMA(double currPrice, double prevAvg) {
        //prevAvg = previous period's SMA or EMA
        //double k = 9.5238;

        double k = 2 / (period + 1.0);
        return((k * (currPrice - prevAvg)) + prevAvg);
    }

    //SMA
    //add an element onto the end of the btcPrice array, if it is full, shift all elements to the left and add onto the end
    public void add(double price){
        if(smaIsFull()){    //array already contains 20 data points
            for(int i = 0; i < period - 1; i++){    //shift elements left by 1
                btcPrice[i] = btcPrice[i + 1];
            }
            btcPrice[period - 1] = price;   //add price to the end of the array
        }
        else{   //scan through btcPrice and find the first open spot, if not found, array is full and first item needs to be removed
            for(int i = 0; i < period; i++){
                if(btcPrice[i] == -1){  //empty spot found, set price
                    btcPrice[i] = price;
                    return;
                }
            }
        }
    }

    //return true if btcPrice already has 20 data points, false if not
    public boolean smaIsFull(){
        for(int i = 0; i < period; i++){
            if(btcPrice[i] == -1){
                return false;
            }
        }
        return true;
    }

    //averages all elements in btcPrice
    public double average(){
        double sum = 0;
        for(int i = 0; i < period; i++){
            sum += btcPrice[i];
        }
        return (sum / period);
    }

    //displays btcPrice
    public void displayHistory(){
        System.out.print("[");
        for(int i = 0; i < period - 1; i++){
            if(btcPrice[i] != -1){
                System.out.print(btcPrice[i] + ", ");
            }
        }
        System.out.print(btcPrice[period - 1] + "]\n");
    }
}
