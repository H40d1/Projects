package es.upm.pproject.sokoban.controller;

import es.upm.pproject.sokoban.model.GameService;
import es.upm.pproject.sokoban.model.InvalidLevelException;
import es.upm.pproject.sokoban.view.GameFrame;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameController {

    private GameService gameService;
    private GameFrame gameFrame;

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private boolean gameFinished;

    private static final String ERROR_MSG_LEVEL = "Error in the loading of level: {}";

    public GameController(GameService gameService, GameFrame gameFrame) {
        this.gameService = gameService;
        this.gameFrame = gameFrame;
        this.gameFinished = false;

        int levelNumber = 1;

        try{
            gameService.startLevel(levelNumber);
            updateStatus();
            gameFrame.loadGraphics(gameService.getGameBoard(), gameService.getGoalPositions());
        } catch(InvalidLevelException e){
            logger.error(ERROR_MSG_LEVEL, levelNumber);
            gameFrame.showErrorMessage(e.getMessage());
        }

    }

    public void movePlayer(int x, int y) {

        if (gameFinished){
            return;
        }

        gameService.movePlayer(x,y);
        gameFrame.loadGraphics(gameService.getGameBoard(), gameService.getGoalPositions());
        updateStatus();

        if (gameService.isCompleted()) {
            gameService.completeLevel();
            updateStatus();

            int currentLevel = gameService.getCurrentLevelNumber();
            int nextLevel = currentLevel + 1;

            try {
                gameService.startLevel(nextLevel);
                gameFrame.loadGraphics(gameService.getGameBoard(), gameService.getGoalPositions());
                updateStatus();
            } catch (InvalidLevelException e) {
                gameFrame.showGameCompletedMessage(gameService.getTotalScore());
                gameFinished = true;
            }

        }
    }

    public void newGame() {

        gameFinished = false;
        try {
            gameService.startNewGame(1);
            updateStatus();
            gameFrame.loadGraphics(gameService.getGameBoard(), gameService.getGoalPositions());
            gameFrame.requestFocusInWindow();
        } catch (InvalidLevelException e) {
            logger.error(ERROR_MSG_LEVEL, 1);
            gameFrame.showErrorMessage(e.getMessage());
        }
    }

    public void loadGame(){
        logger.info("Load Game in progress");
        File saveFile = gameFrame.showLoadFileChooser();
        if (saveFile == null){
            logger.error("Game save load operation canceled by the user");
            return;
        }
        try {
            gameService.loadGame(saveFile);
            gameFrame.loadGraphics(gameService.getGameBoard(), gameService.getGoalPositions());
            updateStatus();
            gameFrame.showMessage("Game loaded successfully");
            logger.info("Game loaded successfully");
        }catch (Exception e){
            logger.error(ERROR_MSG_LEVEL, gameService.getCurrentLevelNumber());
            gameFrame.showErrorMessage("Error: Could not load the game save file: \n" +  e.getMessage());
        }

    }
    public void saveGame(){
        logger.info("Save Game in progress");
        File file = gameFrame.showSaveFileChooser();
        if(file == null) {
            logger.info("Game save operation canceled by the user");
            return;
        }

        try {
            gameService.saveGame(file);
            gameFrame.showMessage("Game saved successfully!");
            logger.info("Game saved successfully to: {}", file.getAbsolutePath());
            
        } catch (Exception e) {
            logger.error("Failed to save the game at: {}", file.getAbsolutePath(), e);
            gameFrame.showErrorMessage("Error: Could not save the game. " + e.getMessage());
        }
    }
    public void undoGame(){
        logger.info("Undoing last move");
        gameService.undoMove();
        gameFrame.loadGraphics(gameService.getGameBoard(), gameService.getGoalPositions());
        updateStatus();

        if(gameFinished){
            gameFinished = false;
        }
    }
    public void restartLevel(){

        gameFinished = false;
        try {
            gameService.restartCurrentLevel();
            updateStatus();
            gameFrame.loadGraphics(gameService.getGameBoard(), gameService.getGoalPositions());
        } catch (InvalidLevelException e) {
            logger.error("Error restarting level");
            gameFrame.showErrorMessage(e.getMessage());
        }
    }

    private void updateStatus() {
        gameFrame.updateStatus(
            gameService.getLevelName(),
            gameService.getLevelScore(),
            gameService.getTotalScore()
        );
    }
}
