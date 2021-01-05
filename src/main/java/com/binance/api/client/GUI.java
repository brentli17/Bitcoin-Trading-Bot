package com.binance.api.client;

import java.io.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import java.util.List;
import java.awt.event.*;
import javax.swing.border.*;
import java.text.SimpleDateFormat;
import com.binance.api.client.domain.market.TickerStatistics;

class GUI implements ActionListener {
    //GUI elements
    private JMenuItem s1, s2, a1, a2;
    private JTextField t1, t2, t3, t4, t5, t6;
    private JFrame manualInputFrame;
    private JTextArea infoBox, outputLog;
    private JButton submit;

    //log writing members
    private java.io.File log;
    private BufferedWriter logWriter;

    //BTC trading members
    private final BinanceApiClientFactory factory;
    private final BinanceApiRestClient client;

    //swing worker members
    private SwingWorker<Void, String> worker;

    //formatters
    private final SimpleDateFormat formatter;
    private final DecimalFormat btcFormat;
    private final DecimalFormat usdtFormat;

    //data points to display in the window
    private double threshold, btcHeld, usdtHeld, prevPrice, currentPrice, gainSinceStart, startingAmount;
    private long frequency;
    private String prevTransaction;

    public GUI(){
        factory = BinanceApiClientFactory.newInstance();
        client = factory.newRestClient();
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        btcFormat = new DecimalFormat("#.0000");
        usdtFormat = new DecimalFormat("#.00");
    }

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

        Font font = new Font("Monospaced", Font.PLAIN, 17);

        //initialize left info box
        infoBox = new JTextArea();
        infoBox.setBorder(new TitledBorder(new EtchedBorder(), "General Info"));
        infoBox.setPreferredSize(new Dimension(440,500));
        infoBox.setEditable(false);
        infoBox.setFont(font);
        updateInfoBox();

        //initialize right output log
        outputLog = new JTextArea();
        JScrollPane scroll = new JScrollPane(outputLog);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setBorder(new TitledBorder(new EtchedBorder(), "Output Log"));
        scroll.setPreferredSize(new Dimension(540,500));
        outputLog.setEditable(false);
        outputLog.setFont(font);

        //all processes stop when frame is closed
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //JFrame constraints
        frame.add(menuBar, BorderLayout.NORTH);
        frame.add(infoBox, BorderLayout.WEST);
        frame.add(scroll, BorderLayout.EAST);
        frame.pack();
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

    //update the left info box with newest info
    private void updateInfoBox(){
        infoBox.setText(null);  //first clear previous text
        infoBox.append("========== Bot settings ==========\n");
        infoBox.append("Trading Threshold:       $" + usdtFormat.format(threshold) + "\n");
        infoBox.append("Bot Update Frequency:    " + frequency + " sec\n\n");

        infoBox.append("========== Trading Info ==========\n");
        infoBox.append("Current BTC Held:        " + btcFormat.format(btcHeld) + " btc\n");
        infoBox.append("Current USDT Held:       $" + usdtFormat.format(usdtHeld) + "\n");
        infoBox.append("Previous BTC Price:      $" + usdtFormat.format(prevPrice) + "\n");
        infoBox.append("Earnings Since Start:    $" + usdtFormat.format(gainSinceStart) + "\n");
        infoBox.append("Current BTC Price:       $" + usdtFormat.format(currentPrice) + "\n");
        infoBox.append("Previous Transaction:    " + prevTransaction + "\n");
    }

    //sell bitcoin
    public void sell(){
        prevTransaction = "sell";
        usdtHeld = btcHeld * currentPrice;
        btcHeld = 0.00;
    }

    //buy bitcoin
    public void buy(){
        prevTransaction = "buy";
        btcHeld = usdtHeld / currentPrice;
        usdtHeld = 0.00;
    }

    //open file explorer and let user choose a log file
    public void logPrompt(){
        JFileChooser fileChooser = new JFileChooser("Transaction log location");
        fileChooser.showOpenDialog(null);   //open file chooser
        log = fileChooser.getSelectedFile();
        try {
            logWriter = new BufferedWriter(new FileWriter(log.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            String lastLine = "";   //last transaction
            String firstLine = "";  //bot settings (threshold, frequency)

            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();   //TODO: Pop up error window
            }
            assert scanner != null;
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
            startingAmount = usdtHeld;

            updateInfoBox();
        }

        else if(e.getSource() == a1){   //start trading
            logPrompt();
            worker = new SwingWorker<Void, String>() {
                //process to do in background of GUI
                @Override
                protected Void doInBackground() throws Exception {
                    Date date = new Date();
                    double change;

                    //write first line of the log
                    logWriter.write(Double.toString(threshold) + ',' + Double.toString(frequency) + "\n");

                    while(true){
                        TickerStatistics tickerStatistics = client.get24HrPriceStatistics("BTCUSDT");
                        currentPrice = Double.parseDouble(tickerStatistics.getLastPrice()); //get current price

                        change = currentPrice - prevPrice;
                        publish(currentPrice + " $" + change + "\n");

                        if(Math.abs(change) >= threshold){
                            if(prevTransaction.equals("buy") && (change > threshold)){   //need to sell
                                publish("\nCurrent Price: " + currentPrice + "    Previous Price: " + prevPrice);
                                publish("\nSelling...\n");
                                publish("Sold " + btcHeld + " bitcoin for $" + usdtHeld + "\n\n");
                                sell();
                                prevPrice = currentPrice;

                                //log transaction
                                logWriter.write(formatter.format(date) + ',' + btcHeld + ',' + usdtHeld + ',' + currentPrice + ',' + prevTransaction + ',' + change + "\n");
                            }
                            else if(prevTransaction.equals("sell") && (change < threshold)){ //need to buy
                                publish("\nCurrent Price: " + currentPrice + "    Previous Price: " + prevPrice);
                                publish("\nBuying...\n");
                                publish("Bought " + btcHeld + " bitcoin for $" + usdtHeld + "\n\n");
                                System.out.println(usdtHeld + " " + startingAmount);
                                gainSinceStart = usdtHeld - startingAmount;
                                buy();
                                prevPrice = currentPrice;

                                //log transaction
                                logWriter.write(formatter.format(date) + ',' + btcHeld + ',' + usdtHeld + ',' + currentPrice + ',' + prevTransaction + ',' + change + "\n");
                            }
                        }
                        updateInfoBox();
                        Thread.sleep(frequency * 1000);    //wait
                    }
                }

                //this will run when doInBackground is done
                @Override
                protected void done() {
                    try {
                        logWriter.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }

                //takes published strings from doInBackground and puts them in the GUI as info for the user
                @Override
                protected void process(List<String> log) {
                    for(String line: log){
                        outputLog.append(line);
                    }
                }
            };
            worker.execute();
        }

        else if(e.getSource() == a2){   //stop trading
            try {
                logWriter.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            worker.cancel(true);
        }

        else if(e.getSource() == submit){   //user wants to submit manually entered values
            btcHeld = Double.parseDouble(t1.getText());
            usdtHeld = Double.parseDouble(t2.getText());
            prevPrice = Double.parseDouble(t3.getText());
            prevTransaction = t4.getText();
            threshold = Double.parseDouble(t5.getText());
            frequency = Long.parseLong(t6.getText());
            manualInputFrame.dispose();

            updateInfoBox();
        }
    }
}