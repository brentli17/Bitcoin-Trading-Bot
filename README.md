# Bitcoin Trading Bot
A bot designed to monitor bitcoin prices and automatically buy or sell bitcoin. 

## General Information
The bot is programmed in Java, and uses the Binance Java API. 
The bot can be run in two settings, dumb and smart mode. 

#### Dumb mode
Dumb mode uses very basic logic. It will not sell unless the price of bitcoin is higher than when it last bought, and it will not buy unless the price is lower than when it last sold. This mode is very safe, and is guaranteed to never lose money, however it makes money very slowly. 

#### Smart mode
Smart mode makes use of the Moving Average Convergence Divergence (macd) theory to accurately predict trends based on a trading period, which it can then use to optimize transactions to make as much as possible. Unlike the dumb mode, this mode trades more often and tends to make more money. However, it can lose money. 

## Transaction logging
In case of an unexpected shutdown, the bot logs its transaction history in a .txt file so that it can be started again with relevant price history. The format for the log is as follows:
`day time, BTC held, USD held, price of BTC, transaction type, difference in BTC price since last transaction`

## Created by
- Jared Yen
- Brent Li
- Tristan Troung
