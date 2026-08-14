package es.upm.pproject.sokoban.model;

import java.io.File;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameService {

    private Level nivel;
    private int currentLevelNumber;
    private int levelScore;
    private int totalScore;
    private Deque<Movement> undoStack;
    private boolean levelCompleted;

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private final GameRepository repository;
 
    public GameService() {
        this.repository = new GameRepository();
    }

    public void startLevel(int levelNumber) throws InvalidLevelException {
        nivel =  LevelLoader.loadLevel(levelNumber);
        currentLevelNumber = levelNumber;
        levelScore = 0;
        undoStack = new ArrayDeque<>();
        levelCompleted = false;
    }

    public void startNewGame(int levelNumber) throws InvalidLevelException {
        totalScore = 0;
        startLevel(levelNumber);
    }

    public BoardElement.Element[][] getGameBoard() {
        return nivel.getBoard();
    }

    public List<Position> getGoalPositions(){
        return nivel.getGoalPositions();
    }

    public int getCurrentLevelNumber(){
        return currentLevelNumber;
    }

    public int getLevelScore() {
        return levelScore;
    }

    public int getTotalScore() { 
        return totalScore; 
    }

    public boolean getLevelCompletedAttribute() {
        return levelCompleted;
    }

    public Deque<Movement> getUndoStack() {
        return undoStack;
    }

    public void movePlayer(int x, int y) {
        Position playerPos = nivel.getPlayerPos();
        
        // Calculamos a qué coordenadas intenta ir el jugador
        int newRow = playerPos.getRow() + x;
        int newCol = playerPos.getCol() + y;
        Position newPosition = new Position(newRow, newCol);
        
        BoardElement.Element targetCell = nivel.getCell(newPosition);

        // Si es una pared, se cancela el movimiento
        if (targetCell == BoardElement.Element.WALL) {
            return; 
        }

        // Si la casilla objetivo está vacía o es una meta
        if (targetCell == BoardElement.Element.EMPTY || targetCell == BoardElement.Element.GOAL) {
            nivel.setCell(newPosition, BoardElement.Element.PLAYER);
            nivel.setCell(playerPos, BoardElement.Element.EMPTY);
            levelScore++;
            undoStack.push(new Movement(x, y, false));
        }
        
        // Si la casilla objetivo es una caja, intentamos empujarla
        else if (targetCell == BoardElement.Element.BOX) {
            // Calculamos dónde iría a parar la caja
            int boxNewRow = newRow + x;
            int boxNewCol = newCol + y;
            Position boxNewPosition = new Position(boxNewRow, boxNewCol);
            
            BoardElement.Element spaceBehindBox = nivel.getCell(boxNewPosition);
            
            // Si hay hueco detrás de la caja para empujarla
            if (spaceBehindBox == BoardElement.Element.EMPTY || spaceBehindBox == BoardElement.Element.GOAL) {
                // Avanzamos la caja
                nivel.setCell(boxNewPosition, BoardElement.Element.BOX);
                // Avanzamos al jugador
                nivel.setCell(newPosition, BoardElement.Element.PLAYER);
                // Vaciamos la casilla original del jugador
                nivel.setCell(playerPos, BoardElement.Element.EMPTY);
                levelScore++;
                undoStack.push(new Movement(x, y, true));
            }
        }
    }

    public void undoMove() {
        if (undoStack == null || undoStack.isEmpty()) {
            return;
        }

        if (levelCompleted) {
            totalScore -= levelScore;
            levelCompleted = false;
        }

        Movement lastMove = undoStack.pop();
        Position currentPlayerPos = nivel.getPlayerPos();
        
        int prevX = currentPlayerPos.getRow() - lastMove.getMovimientoX();
        int prevY = currentPlayerPos.getCol() - lastMove.getMovimientoY();
        Position prevPosition = new Position(prevX, prevY);

        // Movemos al jugador atrás
        nivel.setCell(prevPosition, BoardElement.Element.PLAYER);
        
        // Verificamos si en este turno se había empujado una caja
        if (lastMove.isPushedBox()) {
            // La caja está un paso por delante de donde está el jugador actualmente
            int boxCurrentX = currentPlayerPos.getRow() + lastMove.getMovimientoX();
            int boxCurrentY = currentPlayerPos.getCol() + lastMove.getMovimientoY();
            Position boxCurrentPos = new Position(boxCurrentX, boxCurrentY);
            
            // "Tiramos" de la caja: la ponemos donde estaba el jugador justo antes de deshacer
            nivel.setCell(currentPlayerPos, BoardElement.Element.BOX);
            // Vaciamos el hueco lejano donde estaba la caja
            nivel.setCell(boxCurrentPos, BoardElement.Element.EMPTY);
        } else {
            // Si no había caja, simplemente limpiamos el rastro del jugador
            nivel.setCell(currentPlayerPos, BoardElement.Element.EMPTY);
        }

        // Restamos un movimiento al contador
        if (levelScore > 0) {
            levelScore--;
        }
    }

    public boolean isCompleted() {
        return nivel.isCompleted();
    }

    public GameSnapshot createSnapshot() {
        List<Movement> movementsList = new ArrayList<>(undoStack);
        return new GameSnapshot(currentLevelNumber, nivel.getBoard(), levelScore, totalScore, movementsList);
    }

    public void completeLevel() {
        if (!levelCompleted){
            totalScore += levelScore;
            levelCompleted = true;
        }
        
    }

    public void saveGame(File file) throws JAXBException {
        GameSnapshot snapshot = createSnapshot();
        repository.save(snapshot, file); 
    }

    public void loadGame(File saveFile) throws JAXBException, InvalidLevelException {
        GameSnapshot snapshot = repository.load(saveFile);
        changeGameState(snapshot);

    }

    private void changeGameState(GameSnapshot snapshot) throws InvalidLevelException {
        this.levelScore = snapshot.getLevelScore();
        this.totalScore = snapshot.getTotalScore();
        this.undoStack = new ArrayDeque<>(snapshot.getMovements());
        this.currentLevelNumber = snapshot.getLevelNumber();
        Level level = LevelLoader.loadLevel(currentLevelNumber);
        String levelData = snapshot.getBoardString();
        int charCount = 0;
        for (int i = 0; i < snapshot.getRows(); i++) {
            for (int j = 0; j < snapshot.getCols(); j++) {
                level.setCell(i, j, BoardElement.elementChar(levelData.charAt(charCount)));
                charCount++;
            }
        }
        this.nivel = level;
    }

    public void restartCurrentLevel() throws InvalidLevelException {
        logger.info("Restarting Level {}", currentLevelNumber);

        if (levelCompleted) {
            totalScore -= levelScore;
            levelScore = 0;
            levelCompleted = false;
        }
        startLevel(currentLevelNumber);
    }

    public String getLevelName(){
        return nivel.getName();
    }
}
