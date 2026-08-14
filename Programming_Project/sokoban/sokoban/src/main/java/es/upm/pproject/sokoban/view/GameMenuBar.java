package es.upm.pproject.sokoban.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import es.upm.pproject.sokoban.controller.GameController;

public class GameMenuBar extends JMenuBar {
	private transient GameController controller;

	private JMenu fileMenu;
	private JMenu editionMenu;
	private JMenu helpMenu;

	public GameMenuBar(GameController controller){
		this.controller = controller;

		fileMenu = new JMenu("File");
		editionMenu = new JMenu("Edit");
		helpMenu = new JMenu("Help");

		JMenuItem newGameItem = new JMenuItem("New Game"); //Initialise Game
		JMenuItem loadGameItem = new JMenuItem("Load"); //Charge game
		JMenuItem saveGameItem = new JMenuItem("Save"); //Save game
		JMenuItem exitGameItem = new JMenuItem("Exit"); //Exit game

		JMenuItem undoGameItem = new JMenuItem("Undo"); //Undo game
		JMenuItem restartLevelItem = new JMenuItem("Restart level"); //Restart game

		JMenuItem instructionItem = new JMenuItem("How to play"); //Game instructions
		JMenuItem aboutItem = new JMenuItem("About");

		fileMenu.add(newGameItem);
		fileMenu.add(loadGameItem);
		fileMenu.add(saveGameItem);
		fileMenu.add(exitGameItem);
		fileMenu.add(restartLevelItem);

		this.add(fileMenu);

		//Add edit elements
		editionMenu.add(undoGameItem);

		this.add(editionMenu);

		//Add help elements
		helpMenu.add(instructionItem);
		helpMenu.add(aboutItem);

		this.add(helpMenu);

		// Listeners, so when we click, it works

		newGameItem.addActionListener(e -> actionNewGame());
        loadGameItem.addActionListener(e -> actionLoadGame());
        saveGameItem.addActionListener(e -> actionSaveGame());
        exitGameItem.addActionListener(e -> actionExitGame());
        undoGameItem.addActionListener(e -> actionUndoGame());
        restartLevelItem.addActionListener(e -> actionRestartGame());

        instructionItem.addActionListener(e -> actionInstruction());
        aboutItem.addActionListener(e -> actionAbout());

		//We can use accelerators (atajos de teclado ctrl + key)
		newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		loadGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
		saveGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		exitGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
		undoGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK));
		restartLevelItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));	
	
		applyStyle();
	}

	private void actionNewGame() {

		if (controller != null) {
			controller.newGame();
		}
	}

	private void actionLoadGame(){
		if (controller != null) {
			controller.loadGame();
		} else {
			JOptionPane.showMessageDialog(null, "Load Game in progress");
		}
	}

	private void actionSaveGame(){
		if (controller != null) {
			controller.saveGame();
		} else {
			JOptionPane.showMessageDialog(null, "Save Game in progress");
		}
	}

	private void actionExitGame() {
	    int confirm = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit?","Exit",JOptionPane.YES_NO_OPTION);
	    if (confirm == JOptionPane.YES_OPTION) {
	        System.exit(0);
	    }
	}

	private void actionUndoGame(){
		if (controller != null) {
			controller.undoGame();
		} else {
			JOptionPane.showMessageDialog(null, "Undo in progress");
		}
	}

	private void actionRestartGame(){
		if(controller != null){
			controller.restartLevel();
		} else {
			JOptionPane.showMessageDialog(null, "Restart level in progress");
		}
	}

	private void actionInstruction(){
		JOptionPane.showMessageDialog(null, "Sokoban - How to Play:\n\n" +
		"Use arrow keys or WASD to move the player.\n" +
		"Push all boxes onto the target positions.\n",
		"How to Play",
		JOptionPane.INFORMATION_MESSAGE);
	}

	private void actionAbout(){
		JOptionPane.showMessageDialog(null, "Sokoban Game\n" + 
			"Programming Project 2025-2026 UPM ETSIINF\n" +
			"Contributors:\n" +
			"Alex Martínez Porto, Tarek Kanjaa Cuerva, Andrea Stephany Larico Estrada, Haodi Lin Sun",
			"About", JOptionPane.INFORMATION_MESSAGE);

	}

	public void setController(GameController controller) {
		this.controller = controller;
	}

	// I don't like boring menus
	private void applyStyle() {

	    this.setBackground(new Color(30, 30, 40));
	    this.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));

	    styleMenu(fileMenu);
	    styleMenu(editionMenu);
	    styleMenu(helpMenu);
	}

	private void styleMenu(JMenu menu) {
	    menu.setBackground(new Color(30, 30, 40));
	    menu.setForeground(Color.YELLOW);
	    menu.setFont(new Font("Arial", Font.BOLD, 20));

	    for (int i = 0; i < menu.getItemCount(); i++) {
	        JMenuItem item = menu.getItem(i);
	        if (item != null) {
	            item.setBackground(new Color(40, 40, 55));
	            item.setForeground(Color.WHITE);
	            item.setFont(new Font("Arial", Font.PLAIN, 13));
	        }
	    }
	}
}