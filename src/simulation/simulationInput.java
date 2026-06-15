package simulation;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class simulationInput {

    private final simulationPanel panel;

    public simulationInput(simulationPanel panel) {
        this.panel = panel;
    }

    public void install() {
        setupMouseListener();
        setupResizeListener();
        setupKeyBindings();
    }

    private void setupMouseListener() {
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                panel.atualizarCorpoSobMouse(e.getX(), e.getY());
            }
        });
    }

    private void setupResizeListener() {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.reposicionarSistemaSeNecessario();
            }
        });
    }

    private void setupKeyBindings() {
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke('D'), "toggleEstrelas");
        inputMap.put(KeyStroke.getKeyStroke('d'), "toggleEstrelas");

        actionMap.put("toggleEstrelas", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.alternarEstrelas();
            }
        });
    }
}
