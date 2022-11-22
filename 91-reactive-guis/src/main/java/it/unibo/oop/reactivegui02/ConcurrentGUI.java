package it.unibo.oop.reactivegui02;

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
public final class ConcurrentGUI extends JFrame {
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel("0");
    private final JButton stopB = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    public ConcurrentGUI() {
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
        final Agent agent = new Agent();
        new Thread(agent).start();
        //handlers
        stopB.addActionListener((e) -> agent.stopCounting());
        up.addActionListener((e) -> agent.upCounter());
        down.addActionListener((e) -> agent.downCounter());
    }

    //class agent
    private class Agent implements Runnable {
        private volatile boolean stop;
        public volatile char sign;
        private int counter = 0;
        @Override
        public void run() {
            while (!this.stop) {
                try {
                    // The EDT doesn't access `counter` anymore, it doesn't need to be volatile 
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
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
                    ex.printStackTrace();
                }
            }
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
    }

}
