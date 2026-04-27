import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("OOP Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 900);
            frame.setResizable(true);

            simulationPanel panel = new simulationPanel();
            frame.add(panel);
            frame.setVisible(true);

            Timer timer = new Timer(8, e -> panel.update());

            timer.start();
        });
    }
}
