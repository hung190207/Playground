package Application;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SortingVisualization extends JFrame {
    static final int WIDTH = 800;
    static final int HEIGHT = 600;
    static final int ARRAY_SIZE = 10;
    static final int SWAP_STEPS = 15;

    static int[] array;
    static int currentIndex = -1;
    static int comparingIndex = -1;
    static boolean isSorting = false;
    static SortPanel sortPanel;

    static boolean isSwapping = false;
    static int swapIndex = -1;
    static int swapIndex2 = -1;
    static double swapProgress = 0.0;

    static boolean[] isSorted;
    static int lastSortedIndex = -1;
    static double pulseProgress = 0.0;

    static double scaleAnimation = 0.0;

    void randArray() {
        Random rand = new Random();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = rand.nextInt(HEIGHT - 10) + 1;
            isSorted[i] = false;
        }
        lastSortedIndex = -1;
    }

    void reset() {
        isSorting = false;
        isSwapping = false;
        swapIndex = -1;
        swapIndex2 = -1;
        currentIndex = -1;
        comparingIndex = -1;
        randArray();
        sortPanel.repaint();
    }

    void startSorting() {
        if (isSorting) return;
        isSorting = true;

        Thread thread = new Thread(() -> {
            bubbleSort();

            celebrateCompletion();
            isSorting = false;
            sortPanel.repaint();

        });
        thread.start();
    }

    static void animateComparison() {
        for (int step = 0; step <= 10; step++) {
            scaleAnimation = Math.sin((double) step / 10 * Math.PI) * 0.2;
            sortPanel.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        scaleAnimation = 0.0;
    }

    static void markAsSorted(int index) {
        isSorted[index] = true;
        lastSortedIndex = index;

        for (int step = 0; step <= 20; step++) {
            pulseProgress = Math.sin((double) step / 20 * Math.PI);
            sortPanel.repaint();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        pulseProgress = 0.0;
    }

    void celebrateCompletion() {
        for (int wave = 0; wave < 2; wave++) {
            for (int i = 0; i < array.length - 1; i += 2) {
                lastSortedIndex = i;
                pulseProgress = 1.0;
                sortPanel.repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                lastSortedIndex = i + 1;
                sortPanel.repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 1; i < array.length - 1; i += 2) {
                lastSortedIndex = i;
                pulseProgress = 1.0;
                sortPanel.repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                lastSortedIndex = i + 1;
                sortPanel.repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        lastSortedIndex = -1;
        pulseProgress = 0.0;
    }

    static void animateSwap(int index1, int index2) {
        isSwapping = true;
        swapIndex = index1;
        swapIndex2 = index2;

        for (int step = 0; step <= SWAP_STEPS; step++) {
            swapProgress = (double) step / (double) SWAP_STEPS;
            sortPanel.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;

        isSwapping = false;
        swapIndex = -1;
        swapIndex2 = -1;
        pulseProgress = 0.0;
    }

    public static void bubbleSort() {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (!isSorting) return;
                currentIndex = j;
                comparingIndex = j + 1;

                animateComparison();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (array[j] > array[j + 1]) {
                    animateSwap(j, j + 1);
                    swapped = true;
                }
            }
            markAsSorted(n - i - 1);

            if (!swapped) {
                for (int k = 0; k < n - i - 1; k++) {
                    isSorted[k] = true;
                }
                break;
            }
        }
        for (int i = 0; i < n; i++) {
            isSorted[i] = true;
        }
    }

    public SortingVisualization() {
        setTitle("Sorting Visualization");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        array = new int[ARRAY_SIZE];
        isSorted = new boolean[ARRAY_SIZE];
        randArray();

        sortPanel = new SortPanel();
        add(sortPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton sortButton = new JButton("Sort");
        JButton resetButton = new JButton("Reset");
        JButton randButton = new JButton("Randomize");

        sortButton.addActionListener(e -> {
            startSorting();
        });
        resetButton.addActionListener(e -> {
            reset();
        });
        randButton.addActionListener(e -> {
            randArray();
            sortPanel.repaint();
        });

        buttonPanel.add(sortButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(randButton);

        add(buttonPanel, BorderLayout.NORTH);

    }


    class SortPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int barWidth = getWidth() / array.length;

            for (int i = 0; i < array.length; i++) {
                int barHeight = array[i];
                int x = i * barWidth;
                int y = getHeight() - barHeight - 50;
                int width = barWidth - 2;

                if ((i == currentIndex || i == comparingIndex) && !isSwapping) {
                    int scale = (int) (scaleAnimation * 10);
                    width += scale;
                    x -= scale / 2;
                    barWidth += scale;
                    y -= scale;
                }

                if (isSwapping && (i == swapIndex || i == swapIndex2)) {
                    if (i == swapIndex) {
                        x += (int) (barWidth * swapProgress);
                        y -= (int) (50 * Math.sin(swapProgress * Math.PI));
                    } else if (i == swapIndex2) {
                        x -= (int) (barWidth * swapProgress);
                        y -= (int) (50 * Math.sin(swapProgress * Math.PI));
                    }
                }

                if (i == lastSortedIndex && pulseProgress > 0) {
                    int pulse = (int) (pulseProgress * 10);
                    width += pulse;
                    x -= pulse / 2;
                    barHeight += pulse;
                    y -= pulse;
                }

                if (isSwapping && (i == swapIndex || i == swapIndex2)) {
                    g2d.setColor(new Color(255, 209, 220));
                } else if (isSorted[i]) {
                    g2d.setColor(new Color(167, 199, 231));
                } else if (i == currentIndex || i == comparingIndex) {
                    g2d.setColor(new Color(255, 0, 0));
                } else {
                    float ratio = (float) i / array.length;
                    g2d.setColor(new Color(
                            (int) (148 * (1 - ratio) + 255 * ratio),
                            (int) (0 * (1 - ratio) + 192 * ratio),
                            (int) (211 * (1 - ratio) + 203 * ratio)));
                }
                g2d.fillRect(x, y, width, barHeight);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                String value = String.valueOf(array[i]);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(value);
                int textY = getHeight() - 30;
                int textX = i * (getWidth() / array.length) + (getWidth() / array.length - textWidth) / 2;
                g2d.drawString(value, textX, textY);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SortingVisualization frame = new SortingVisualization();
            frame.setVisible(true);
        });
    }

}
