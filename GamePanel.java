package com.zen.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;
import java.net.URL;

import com.zen.gui.ZenTimerApp;
import com.zen.core.TimerWorker;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;

public class GamePanel extends JPanel {

    private ZenTimerApp parentFrame;
    private JLabel scoreLabel;
    private JLabel gameTimerLabel;

    private int score = 0;
    private TimerWorker gameTimer;
    private Random random = new Random();

    private final int NUM_MOLES = 9;
    private final int MOLE_SIZE = 50;

    private int activeMoleIndex = -1;

    private GameAreaPanel gameArea;
    private Timer molePopTimer;

    public GamePanel(ZenTimerApp frame) {
        this.parentFrame = frame;
        setLayout(new BorderLayout());

        // ---------------- TOP INFO PANEL ----------------
        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        gameTimerLabel = new JLabel("Time Left: --:--", SwingConstants.CENTER);
        infoPanel.add(scoreLabel);
        infoPanel.add(gameTimerLabel);
        add(infoPanel, BorderLayout.NORTH);

        // ---------------- GAME AREA ----------------
        gameArea = new GameAreaPanel();
        gameArea.setPreferredSize(new Dimension(300, 200));
        add(gameArea, BorderLayout.CENTER);

        // ---------------- EXIT BUTTON ----------------
        JButton returnButton = new JButton("End Break & Return to Work");
        returnButton.addActionListener(e -> stopGame());
        add(returnButton, BorderLayout.SOUTH);

        // ---------------- MOUSE LISTENER ----------------
        gameArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkHit(e.getX(), e.getY());
            }
        });
    }

    // ==========================================================
    //                      GAME AREA PANEL
    // ==========================================================

    private class GameAreaPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int width = getWidth();
            int height = getHeight();
            int rows = 3, cols = 3;

            int cellWidth = width / cols;
            int cellHeight = height / rows;

            for (int i = 0; i < NUM_MOLES; i++) {
                int row = i / cols;
                int col = i % cols;

                int x = col * cellWidth + cellWidth / 2 - MOLE_SIZE / 2;
                int y = row * cellHeight + cellHeight / 2 - MOLE_SIZE / 2;

                g.setColor(Color.LIGHT_GRAY);
                g.fillOval(x, y, MOLE_SIZE, MOLE_SIZE);

                if (i == activeMoleIndex) {
                    g.setColor(Color.RED);
                    g.fillOval(x, y, MOLE_SIZE, MOLE_SIZE);
                }
            }
        }
    }

    // ==========================================================
    //                 SOUND: POP ON HIT
    // ==========================================================

    private void playPopSound() {
        try {
            File soundFile = new File("pop.wav");
            System.out.println("Sound file exists? " + soundFile.exists());

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================================
    //                  HIT DETECTION (FIXED)
    // ==========================================================

    private void checkHit(int clickX, int clickY) {

        if (activeMoleIndex == -1) return;

        int width = gameArea.getWidth();
        int height = gameArea.getHeight();

        int cellWidth = width / 3;
        int cellHeight = height / 3;

        int row = activeMoleIndex / 3;
        int col = activeMoleIndex % 3;

        int moleX = col * cellWidth + cellWidth / 2 - MOLE_SIZE / 2;
        int moleY = row * cellHeight + cellHeight / 2 - MOLE_SIZE / 2;

        boolean hit =
                clickX >= moleX && clickX <= moleX + MOLE_SIZE &&
                clickY >= moleY && clickY <= moleY + MOLE_SIZE;

        if (hit) {
            playPopSound();  // 🔊 sound effect
            score++;
            scoreLabel.setText("Score: " + score);
            popNewMole();
        }
    }

    // ==========================================================
    //                      GAME LOGIC
    // ==========================================================

    private void startMoleTimer() {
        molePopTimer = new Timer(1500, e -> popNewMole());
        molePopTimer.start();
    }

    private void popNewMole() {
        activeMoleIndex = random.nextInt(NUM_MOLES);
        gameArea.repaint();
    }

    public void startGame(int durationSeconds) {
        score = 0;
        scoreLabel.setText("Score: 0");
        activeMoleIndex = -1;

        startMoleTimer();

        gameTimer = new TimerWorker(durationSeconds) {

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int secondsLeft = chunks.get(chunks.size() - 1);
                gameTimerLabel.setText(
                        "Time Left: " +
                                String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60)
                );
            }

            @Override
            protected void done() {
                if (!isCancelled()) {
                    stopGame();
                }
            }
        };

        gameTimer.execute();
    }

    public void stopGame() {
        if (gameTimer != null)
            gameTimer.cancel(true);

        if (molePopTimer != null)
            molePopTimer.stop();

        activeMoleIndex = -1;
        gameArea.repaint();

        JOptionPane.showMessageDialog(this, "Break Finished! Final Score: " + score);

        parentFrame.switchToTimer();
    }
}
