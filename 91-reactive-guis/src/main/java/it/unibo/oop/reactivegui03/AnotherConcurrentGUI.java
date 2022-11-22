package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel("0");
    private final JButton stopB = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    /**
     * Builds a new GUI.
     */
    public AnotherConcurrentGUI() {
        //setting layout
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stopB);
        this.getContentPane().add(panel);
        this.setVisible(true);
        //thread
        final Agent2 agent = new Agent2();
        new Thread(agent).start();
        //handlers
        stopB.addActionListener((e) -> agent.stopCounting());
        up.addActionListener((e) -> agent.upCounter());
        down.addActionListener((e) -> agent.downCounter());
    }

    /**
     * Builds the Agent that controls the counter of the GUI.
     */
    private class Agent2 implements Runnable {
        private volatile boolean stop;
        private volatile char sign;
        private int counter;
        //thread counter
        private final AgentCount agentCount = new AgentCount();
        @Override
        public void run() {
            new Thread(agentCount).start(); 
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (sign == '+') {
                        this.counter++;
                    } else {
                        if (sign == '-') {
                            this.counter--;
                        }
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace(); // NOPMD
                }
            }
            stopB.doClick();
        }

        public void stopCounting() {
            this.stop = true;
            stopB.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        }

        public void upCounter() {
            this.sign = '+';
        }

        public void downCounter() {
            this.sign = '-';
        }
        /**
         * Builds the Agent to count 10 seconds.
         */
        private class AgentCount implements Runnable {
            private int sec;
            @Override
            public void run() {
                while (sec < 10) {
                    try {
                        sec++;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace(); // NOPMD
                    }
                } 
                stopB.doClick();
            }
        }
    }

}
