package es.upm.pproject.sokoban.view;

import es.upm.pproject.sokoban.controller.GameController;
import es.upm.pproject.sokoban.model.BoardElement;
import es.upm.pproject.sokoban.model.Position;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

public class GameFrame extends JFrame {

    private BoardPanel boardPanel;
    private StatusPanel statusPanel;
    private transient GameController gameController;

    public GameFrame() {
        setTitle("Game Frame"); 
        //Para incluir una marca de puntuaciones, necesitamos dividir el tablero en renglones
        //El propio tamaño vendrá definido por tablero y panel derecho de puntuaciones      
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        boardPanel = new BoardPanel();
        boardPanel.setBounds(0, 0, 600, 660);
        add(boardPanel);

        statusPanel = new StatusPanel();
        statusPanel.setBounds(600, 0, 200, 200);
        add(statusPanel);

        setSize(800, 660);
        setLocationRelativeTo(null); //Centrar la pantalla, que no aparezca en cualquier lado

        addKeyListener(new MyKeyListener());

        getContentPane().setBackground(new Color(10, 22, 44));
    }


    public void loadGraphics(BoardElement.Element[][] board, List<Position> metas){
        boardPanel.updateGraphics(board, metas);
    }


    public void setGameController(GameController gameController) {
        
        GameMenuBar gameMenuBar;
        this.gameController = gameController;
        gameMenuBar = new GameMenuBar(gameController);
        this.setJMenuBar(gameMenuBar);
        requestFocusInWindow();
    }

    // Dialogs
    public void showErrorMessage(String message) {
        ErrorDialog dialog = new ErrorDialog(this, message);
        dialog.setVisible(true);
    }

    public void showGameCompletedMessage(int totalScore) {
        GameCompletedDialog dialog = new GameCompletedDialog(this, totalScore);
        dialog.setVisible(true);
        if (dialog.isNewGame()) {
            gameController.newGame();  

        } 
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateStatus(String levelName, int levelScore, int totalScore) {
        statusPanel.updateStatus(levelName, levelScore, totalScore);
    }

    public File showSaveFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game State");
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML Files (*.xml)", "xml"));

        int userSelection = fileChooser.showSaveDialog(this); 


        return processFileChooser(userSelection, fileChooser);
    }

    public File showLoadFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game Save");
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML Files (*.xml)", "xml"));

        int userSelection = fileChooser.showOpenDialog(this);


        return processFileChooser(userSelection, fileChooser);
    }

    private File processFileChooser(int userSelection, JFileChooser fileChooser) {
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".xml")) {
                selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".xml");
            }
            return selectedFile;
        }
        return null;
    }
    
    class MyKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            //Do nothing because is not needed for the game controls
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN){
                gameController.movePlayer(0,1);
            }
            else if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP){
                gameController.movePlayer(0,-1);
            }
            else if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT){
                gameController.movePlayer(-1,0);
            }
            else if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT){
                gameController.movePlayer(1,0);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            //Do nothing because is not needed for the game controls
        }

    }

}
