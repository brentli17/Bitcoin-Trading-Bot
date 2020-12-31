package com.binance.api.client;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

class GUI implements ActionListener {
    private JMenuItem s1, s2, a1, a2;
    private JTextField t1, t2, t3, t4, t5, t6;
    private JFrame manualInputFrame;
    JButton submit;

    private Trading trading;

    //data points to display in the window
    public double threshold, btcHeld, usdtHeld, prevPrice;
    public long frequency;
    public int gainSinceStart;
    public String btcPrice, prevTransaction;


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

    public void openManualInputWindow(){
        manualInputFrame = new JFrame("Input parameters");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //create text fields
        t1 = new JTextField("Bitcoin Balance(x)");
        t2 = new JTextField("USD Balance($x.xx)");
        t3 = new JTextField("Price During Previous Transaction ($x.xx)");
        t4 = new JTextField("Previous Transaction Type (buy/sell)");
        t5 = new JTextField("Price Threshold ($x.xx)");
        t6 = new JTextField("Update Frequency (s)");

        //set bounds for text fields
        t1.setBounds(50, 100, 200, 30);
        t2.setBounds(50, 100, 200, 30);
        t3.setBounds(50, 100, 200, 30);
        t4.setBounds(50, 100, 200, 30);
        t5.setBounds(50, 100, 200, 30);
        t6.setBounds(50, 100, 200, 30);

        //create submit button
        submit = new JButton("Submit");
        submit.addActionListener(this);

        //add text fields and button to JFrame
        panel.add(t1);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(t2);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(t3);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(t4);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(t5);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(t6);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(submit);

        //JFrame constraints
        panel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
        manualInputFrame.add(panel);
        manualInputFrame.setSize(300,600);
        manualInputFrame.setLocationRelativeTo(null);
        manualInputFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == s1){        //Start with manual input
            openManualInputWindow();
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

        else if(e.getSource() == submit){   //user wants to submit manually entered values
            btcHeld = Double.parseDouble(t1.getText());
            usdtHeld = Double.parseDouble(t2.getText());
            prevPrice = Double.parseDouble(t3.getText());
            prevTransaction = t4.getText();
            threshold = Double.parseDouble(t5.getText());
            frequency = Long.parseLong(t6.getText());
            manualInputFrame.dispose();
        }
    }
}
