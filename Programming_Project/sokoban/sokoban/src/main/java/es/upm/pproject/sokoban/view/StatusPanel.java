package es.upm.pproject.sokoban.view;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;
public class StatusPanel extends JPanel {

	private JLabel levelName;
	private JLabel levelScore;
	private JLabel totalScore;


	public StatusPanel() {
	setLayout(new GridLayout(3, 1, 20, 50));
    	setBorder(BorderFactory.createTitledBorder(
		    BorderFactory.createLineBorder(Color.BLACK), 
		    "Game Status",
		    TitledBorder.CENTER,
		    TitledBorder.TOP,
		    new Font("Arial", Font.BOLD, 22),
		    new Color(255, 200, 50)  
		));
    	setPreferredSize(new Dimension(200, 150));
    	setBackground(new Color(10, 22, 44));

    	levelName = new JLabel("Level: ");
    	styleLabel(levelName, Color.WHITE);

    	levelScore = new JLabel("Level Score: 0");
    	styleLabel(levelScore, Color.BLUE);

    	totalScore = new JLabel("Total Score: 0");
    	styleLabel(totalScore, new Color(100, 255, 100));

    	add(levelName);
    	add(levelScore);
    	add(totalScore);
	}

	private void styleLabel(JLabel label, Color textColor){
		label.setFont(new Font("Arial", Font.BOLD, 18));
		label.setForeground(textColor);
		label.setHorizontalAlignment(SwingConstants.LEFT);
	}

	public void updateStatus(String levelN, int levelS, int totalS) {
    	levelName.setText(levelN);
    	levelScore.setText("Movements: " + levelS);
    	totalScore.setText("Total Moves: " + totalS);
	}
}
