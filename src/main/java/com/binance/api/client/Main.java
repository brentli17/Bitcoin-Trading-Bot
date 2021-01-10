package com.binance.api.client;

import com.binance.api.client.domain.account.Trade;

class Main {
    public static void main(String[] args){
        //GUI gui = new GUI();
        //gui.startWindow();
        TradeAI tradeAI = new TradeAI();
        for(int i = 0; i < 20; i++){
            tradeAI.add(i+1);
        }
        tradeAI.displayHistory();
        tradeAI.add(21);
        tradeAI.displayHistory();
    }
}