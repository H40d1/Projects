package es.upm.pproject.sokoban.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class GameCompletedDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND = new Color(224, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(0, 128, 128);
    private static final Color TEXT_SECONDARY = new Color(95, 158, 160);
    private static final Color NEW_GAME_COLOR = new Color(0, 206, 209);
    private static final Color CLOSE_COLOR = new Color(95, 158, 160);
    private static final Color HOVER_COLOR = new Color(0, 128, 128);
    private static final String FONT_ARIAL = "Arial";
    private static final Font TITLE_FONT = new Font(FONT_ARIAL, Font.BOLD, 22);
    private static final Font SCORE_FONT = new Font(FONT_ARIAL, Font.PLAIN, 18);
    private static final Font BUTTON_FONT = new Font(FONT_ARIAL, Font.BOLD, 16);
    private static final Font BUTTON_FONT_HOVER = new Font(FONT_ARIAL, Font.BOLD, 18);

    private boolean newGame = false;

    public GameCompletedDialog(JFrame parent, int totalScore) {
        super(parent, "Game Completed", true);
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        add(createMessagePanel(totalScore), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMessagePanel(int totalScore) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(BACKGROUND);

        JLabel congratsLabel = new JLabel("Congratulations!", SwingConstants.CENTER);
        congratsLabel.setFont(TITLE_FONT);
        congratsLabel.setForeground(TEXT_PRIMARY);

        JLabel scoreLabel = new JLabel("Total score: " + totalScore + " movements", SwingConstants.CENTER);
        scoreLabel.setFont(SCORE_FONT);
        scoreLabel.setForeground(TEXT_SECONDARY);

        panel.add(congratsLabel);
        panel.add(scoreLabel);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(BACKGROUND);
        panel.add(createNewGameButton());
        panel.add(createCloseButton());
        return panel;
    }

    private JButton createNewGameButton() {
        JButton button = new JButton("New Game");
        button.setFont(BUTTON_FONT);
        button.setBackground(NEW_GAME_COLOR);
        button.setForeground(Color.WHITE);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
                button.setFont(BUTTON_FONT_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(NEW_GAME_COLOR);
                button.setFont(BUTTON_FONT);
            }
        });
        button.addActionListener(e -> {
            newGame = true;
            dispose();
        });
        return button;
    }

    private JButton createCloseButton() {
        JButton button = new JButton("Close");
        button.setFont(BUTTON_FONT);
        button.setBackground(CLOSE_COLOR);
        button.setForeground(Color.WHITE);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
                button.setFont(BUTTON_FONT_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(CLOSE_COLOR);
                button.setFont(BUTTON_FONT);
            }
        });
        button.addActionListener(e -> dispose());
        return button;
    }

    public boolean isNewGame() {
        return newGame;
    }
}