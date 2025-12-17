package com.zen.gui; // Package: com.zen.gui

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ZenTimerApp extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private TimerPanel timerPanel;
    private GamePanel gamePanel;

    // --- Configuration Variables (OOP) ---
    private final int WORK_TIME_MINUTES = 1;
    private final int BREAK_TIME_SECONDS = 1 * 60; // 5 minutes
    
    public ZenTimerApp() {
        setTitle("Zen Focus Timer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        
        timerPanel = new TimerPanel(WORK_TIME_MINUTES,  this); // Pass reference to switch panels
        gamePanel = new GamePanel(this); 

        mainPanel.add(timerPanel, "TIMER");
        mainPanel.add(gamePanel, "GAME");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "TIMER"); // Start on the timer panel

        setVisible(true);
    }
    
    // --- Public methods for Panel Switching (Controller Logic) ---
    public void switchToGame() {
        cardLayout.show(mainPanel, "GAME");
        gamePanel.startGame(BREAK_TIME_SECONDS); // Start the game with break duration
    }

    public void switchToTimer() {
        cardLayout.show(mainPanel, "TIMER");
        timerPanel.resetTimer(); // Reset and prepare for the next focus session
    }

    public static void main(String[] args) {
        // Ensure GUI updates are run on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new ZenTimerApp());
    }
}