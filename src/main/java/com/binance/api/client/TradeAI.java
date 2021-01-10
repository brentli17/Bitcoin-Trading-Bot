package com.binance.api.client;

public class TradeAI {
    double[] btcPrice;

    public TradeAI(){
        btcPrice = new double[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    }

    //EMA


    //SMA
    //add an element onto the end of the btcPrice array, if it is full, shift all elements to the left and add onto the end
    public void add(double price){
        boolean isFull = true;
        //scan through btcPrice and find the first open spot, if not found, array is full and first item needs to be removed
        for(int i = 0; i < 20; i++){
            if(btcPrice[i] == -1){  //empty spot found, set price
                btcPrice[i] = price;
                isFull = false;
            }
        }
        if(isFull){ //price list is full, remove first price and shift left
            for(int i = 0; i < 19; i++){    //shift elements left by 1
                btcPrice[i] = btcPrice[i + 1];
            }
            btcPrice[19] = price;   //add price to the end of the array
        }
    }

    //averages all elements in btcPrice
    public double average(){
        double sum = 0;
        for(int i = 0; i < 20; i++){
            sum += btcPrice[i];
        }
        return (sum / 20);
    }

    //displays btcPrice
    public void displayHistory(){
        System.out.print("[");
        for(int i = 0; i < 19; i++){
            if(btcPrice[i] != -1){
                System.out.print(btcPrice[i] + ", ");
            }
        }
        System.out.print(btcPrice[19]);
        System.out.print("]\n");
    }
}
