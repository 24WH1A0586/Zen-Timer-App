package com.zen.gui;

import com.zen.core.TimerWorker;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class TimerPanel extends JPanel {

    
    private JLabel timeLabel = new JLabel("25:00", SwingConstants.CENTER);
    private JLabel statusLabel = new JLabel("WORK TIME", SwingConstants.CENTER);
    private JButton startPauseButton = new JButton("START");
    private JButton resetButton = new JButton("RESET");

    private TimerWorker currentWorker;
    private ZenTimerApp parentFrame;

    
    private final int workDurationSeconds;
    private int remainingSeconds;

 
    public TimerPanel(int workMin, ZenTimerApp frame) {
        this.workDurationSeconds = workMin * 60;
        this.remainingSeconds = workDurationSeconds;
        this.parentFrame = frame;

        
        setLayout(new BorderLayout(10, 10));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 72));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 20));

        add(statusLabel, BorderLayout.NORTH);
        add(timeLabel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.add(startPauseButton);
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.SOUTH);

        
        startPauseButton.addActionListener(e -> toggleTimer());
        resetButton.addActionListener(e -> resetTimer());

        
        updateDisplay(remainingSeconds);
    }

   

    private void toggleTimer() {
        if (currentWorker == null || currentWorker.isDone()) {
            
            startTimer();
            startPauseButton.setText("PAUSE");
        } else {
            // Pause
            currentWorker.cancel(true);
            startPauseButton.setText("RESUME");
        }
    }

    private void startTimer() {
        
        currentWorker = new TimerWorker(remainingSeconds) {

            @Override
            protected void process(java.util.List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    int secondsLeft = chunks.get(chunks.size() - 1);
                    updateDisplay(secondsLeft);
                }
            }

            @Override
            protected void done() {
                if (!isCancelled()) {
                    onTimerFinished();  
                }
            }
        };

        currentWorker.execute();
    }

    private void onTimerFinished() {
        JOptionPane.showMessageDialog(this, "Work session complete! Starting mini-game...");
        parentFrame.switchToGame();   
        resetTimer();                
    }

    public void resetTimer() {
        if (currentWorker != null) {
            currentWorker.cancel(true);
        }
        remainingSeconds = workDurationSeconds;
        statusLabel.setText("WORK TIME");
        startPauseButton.setText("START");
        updateDisplay(remainingSeconds);
    }

    private void updateDisplay(int secondsLeft) {
        this.remainingSeconds = secondsLeft; 

        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        DecimalFormat df = new DecimalFormat("00");
        timeLabel.setText(minutes + ":" + df.format(seconds));
    }
}
