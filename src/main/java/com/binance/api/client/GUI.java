package com.binance.api.client;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

class GUI implements ActionListener {
    private JMenuItem s1;
    private JMenuItem s2;
    private JMenuItem a1;
    private JMenuItem a2;

    private Trading trading;

    //data points to display in the window
    public double threshold;
    public long frequency;
    public double btcHeld;
    public double usdtHeld;
    public String btcPrice;
    public int gainSinceStart;
    public double prevPrice;
    public String prevTransaction;


    public void startWindow(){
        //create window
        JFrame frame = new JFrame("Bitcoin Trading Bot");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());    //set panel layout

        //create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu start = new JMenu("Initialization");  //initialization menu
        JMenu action = new JMenu("Actions");        //actions menu

        //create menu items
        s1 = new JMenuItem("Start with manual input");
        s2 = new JMenuItem("Start with history log");
        a1 = new JMenuItem("Start trading");
        a2 = new JMenuItem("Stop trading");

        //add action listeners to menu items
        s1.addActionListener(this);
        s2.addActionListener(this);
        a1.addActionListener(this);
        a2.addActionListener(this);

        //add menu items to their menus
        start.add(s1);
        start.add(s2);
        action.add(a1);
        action.add(a2);

        //add menus to the menu bar
        menuBar.add(start);
        menuBar.add(action);

        //set frame constraints
        frame.add(menuBar, BorderLayout.NORTH);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == s1){        //Start with manual input

        }

        else if(e.getSource() == s2){   //start with log
            JFileChooser fileChooser = new JFileChooser("Select transaction log");
            fileChooser.showOpenDialog(null);   //open file chooser
            java.io.File file = fileChooser.getSelectedFile();

            System.out.println(file.getPath());

            String lastLine = "";   //last transaction
            String firstLine = "";  //bot settings (threshold, frequency)

            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("File not found");   //TODO: Pop up error window
            }
            firstLine = scanner.nextLine();
            while(scanner.hasNextLine()){  //read last line of inputted file
                lastLine = scanner.nextLine();
            }

            //parse file
            String[] transactionInfo = lastLine.split(","); // time,btc held,usdt held,price of btc,last transaction,gain
            String[] botSettings = firstLine.split(",");    // threshold,frequency

            //set parameters
            btcHeld = Double.parseDouble(transactionInfo[1]);
            usdtHeld = Double.parseDouble(transactionInfo[2]);
            prevPrice = Double.parseDouble(transactionInfo[3]);
            prevTransaction = transactionInfo[4];
            threshold = Double.parseDouble(botSettings[0]);
            frequency = Long.parseLong(botSettings[1]);
        }

        else if(e.getSource() == a1){   //start trading
            trading = new Trading(prevPrice, threshold, usdtHeld, btcHeld, frequency, prevTransaction);
            try {
                trading.tradingMain();
            } catch (InterruptedException interruptedException) {
                System.out.println("failed");
            }
        }

        else if(e.getSource() == a2){   //stop trading
            trading.setStopTrading(true);
        }
    }
}
