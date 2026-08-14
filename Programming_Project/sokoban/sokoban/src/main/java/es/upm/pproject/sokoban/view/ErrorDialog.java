package es.upm.pproject.sokoban.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class ErrorDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND = new Color(255, 204, 204);
    private static final Color TEXT_COLOR = new Color(100, 0, 0);
    private static final Color BUTTON_COLOR = new Color(200, 0, 0);
    private static final Color BUTTON_HOVER_COLOR = new Color(150, 0, 0);
    private static final String FONT_ARIAL = "Arial";
    private static final Font MESSAGE_FONT = new Font(FONT_ARIAL, Font.BOLD, 18);
    private static final Font BUTTON_FONT = new Font(FONT_ARIAL, Font.BOLD, 16);
    private static final Font BUTTON_FONT_HOVER = new Font(FONT_ARIAL, Font.BOLD, 18);

    public ErrorDialog(JFrame parent, String message) {
        super(parent, "Error", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        add(createMessagePanel(message), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMessagePanel(String message) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(MESSAGE_FONT);
        label.setForeground(TEXT_COLOR);
        panel.add(label);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND);
        panel.add(createOkButton());
        return panel;
    }

    private JButton createOkButton() {
        JButton button = new JButton("OK");
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
                button.setFont(BUTTON_FONT_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
                button.setFont(BUTTON_FONT);
            }
        });
        button.addActionListener(e -> dispose());
        return button;
    }
}