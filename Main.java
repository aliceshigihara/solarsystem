import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema Solar projeto");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 900);
            frame.setResizable(false);

            simulationPanel panel = new simulationPanel();
            frame.add(panel);
            frame.setVisible(true);

            Timer timer = new Timer(16, e -> panel.update());

            timer.start();
        });
    }
}
