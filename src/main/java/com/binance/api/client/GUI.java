package com.binance.api.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GUI implements ActionListener {
    JMenuItem s1;
    JMenuItem s2;
    JMenuItem a1;
    JMenuItem a2;

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

        }

        else if(e.getSource() == a1){   //start trading

        }

        else if(e.getSource() == a2){   //stop trading

        }
    }
}
