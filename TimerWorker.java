package com.zen.core; // Package: com.zen.core

import javax.swing.SwingWorker;
import java.util.concurrent.TimeUnit;

// SwingWorker<Void, Integer> -> No final result, but publishes Integer chunks (seconds left)
public abstract class TimerWorker extends SwingWorker<Void, Integer> {
    
    private int secondsLeft;

    // Constructor (OOP)
    public TimerWorker(int durationSeconds) {
        this.secondsLeft = durationSeconds;
    }

    // --- Multithreading Logic (Runs in a background thread) ---
    @Override
    protected Void doInBackground() throws Exception {
        while (secondsLeft >= 0 && !isCancelled()) {
            publish(secondsLeft); // Safely sends the value to the GUI thread
            secondsLeft--;
            
            try {
                // Pause for 1 second
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // Exception Handling: Thrown if the thread is interrupted (e.g., by PAUSE/CANCEL)
                // Re-throw the exception or set the interrupted flag again
                Thread.currentThread().interrupt();
                throw e; // Stop the countdown
            }
        }
        return null; // Signals task completion
    }
    
    // --- GUI Update Logic (Runs on the Event Dispatch Thread - EDT) ---
    // (The process and done methods are implemented in TimerPanel for GUI access)
}