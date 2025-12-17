package com.zen.core; 
import javax.swing.SwingWorker;
import java.util.concurrent.TimeUnit;


public abstract class TimerWorker extends SwingWorker<Void, Integer> {
    
    private int secondsLeft;

 
    public TimerWorker(int durationSeconds) {
        this.secondsLeft = durationSeconds;
    }

   
  
    protected Void doInBackground() throws Exception {
        while (secondsLeft >= 0 && !isCancelled()) {
            publish(secondsLeft);
            secondsLeft--;
            
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e; 
            }
        }
        return null; 
    }
    
}
