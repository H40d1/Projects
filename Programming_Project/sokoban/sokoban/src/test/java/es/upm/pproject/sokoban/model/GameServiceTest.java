package es.upm.pproject.sokoban.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.xml.bind.JAXBException;

class GameServiceTest {

    private GameService gameService;


    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    @Test
    @DisplayName("El jugador puede empujar una caja a una casilla vacía")
    void testPushBoxToEmptySpace() throws InvalidLevelException {
        gameService.startLevel(1);
        
        // IMPORTANTE: La matriz del equipo funciona como board[x][y] (columna, fila).
        // En el nivel 1, el jugador empieza en x=2, y=4.
        
        // Forzamos el tablero: ponemos caja a su derecha (x=3) y vacío en la siguiente (x=4)
        gameService.getGameBoard()[3][4] = BoardElement.Element.BOX;
        gameService.getGameBoard()[4][4] = BoardElement.Element.EMPTY;
        
        // Act: Movemos al jugador a la derecha (xOffset = 1, yOffset = 0)
        gameService.movePlayer(1, 0);
        
        // Assert: Comprobamos que todo se ha desplazado hacia la derecha
        assertEquals(BoardElement.Element.PLAYER, gameService.getGameBoard()[3][4], "El jugador debería estar donde estaba la caja");
        assertEquals(BoardElement.Element.BOX, gameService.getGameBoard()[4][4], "La caja debería haber sido empujada a la derecha");
        assertEquals(BoardElement.Element.EMPTY, gameService.getGameBoard()[2][4], "La casilla original del jugador debería estar vacía");
    }

    @Test
    void testScoreIncreasesAfterMove() throws InvalidLevelException {
        gameService.startLevel(1);
        int scoreBefore = gameService.getLevelScore();
        gameService.movePlayer(1, 0);
        assertEquals(scoreBefore + 1, gameService.getLevelScore());
    }

    @Test
    void testCompleteLevelAccumulatesTotalScore() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.movePlayer(1, 0);
        gameService.completeLevel();
        assertTrue(gameService.getTotalScore() > 0);
    }

    @Test
    void testStartNewGameResetsTotalScore() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.movePlayer(1, 0);
        gameService.completeLevel();
        gameService.startNewGame(1);
        assertEquals(0, gameService.getTotalScore());
    }

    @Test
    @DisplayName("El snapshot debe guardar el historial de movimientos")
    void testCreateSnapshot() throws InvalidLevelException {
        gameService.startLevel(1);
        
        gameService.getGameBoard()[3][4] = BoardElement.Element.EMPTY;
        gameService.movePlayer(1, 0); 
        
        GameSnapshot snapshot = gameService.createSnapshot();
        
        assertNotNull(snapshot);
        assertEquals(1, snapshot.getLevelNumber());
        assertEquals(1, snapshot.getLevelScore());
        assertEquals(0, snapshot.getTotalScore());
        assertNotNull(snapshot.getBoardString());
        
        List<Movement> savedMovements = snapshot.getMovements();
        assertNotNull(savedMovements);
        assertEquals(1, savedMovements.size());
        assertEquals(1, savedMovements.get(0).getMovimientoX());
        assertFalse(savedMovements.get(0).isPushedBox());
    }

    @Test
    void testSnapshotContainsCurrentBoard() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.movePlayer(1, 0);
        GameSnapshot snapshot = gameService.createSnapshot();
        assertNotNull(snapshot.getBoardString());
        assertFalse(snapshot.getBoardString().isEmpty());
        assertTrue(snapshot.getRows() > 0);
        assertTrue(snapshot.getCols() > 0);
    }

    @Test
    void testSaveGame() throws Exception {
        gameService.startLevel(1);
        File tempFile = File.createTempFile("sokoban_save_game", ".xml");
        tempFile.deleteOnExit();
        assertDoesNotThrow(() -> gameService.saveGame(tempFile));
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
    }

    @Test
    @DisplayName("Se puede deshacer un movimiento simple")
    void testUndoSimpleMove() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.getGameBoard()[3][4] = BoardElement.Element.EMPTY;
        gameService.movePlayer(1, 0);
        assertEquals(1, gameService.getLevelScore());
        gameService.undoMove();
        assertEquals(BoardElement.Element.PLAYER, gameService.getGameBoard()[2][4], "El jugador debe volver a su posición original");
        assertEquals(BoardElement.Element.EMPTY, gameService.getGameBoard()[3][4], "La casilla avanzada debe quedar vacía de nuevo");
        assertEquals(0, gameService.getLevelScore(), "La puntuación debe volver a 0");
    }

    @Test
    @DisplayName("Se puede deshacer un movimiento simple")
    void testUndoMoveOnCompletedLevel() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.getGameBoard()[3][4] = BoardElement.Element.EMPTY;
        gameService.movePlayer(1, 0);
        assertEquals(1, gameService.getLevelScore());
        gameService.completeLevel();
        int totalScore = gameService.getTotalScore();
        int levelScore =  gameService.getLevelScore();
        gameService.undoMove();
        assertFalse(gameService.getLevelCompletedAttribute());
        assertEquals(totalScore - levelScore, gameService.getTotalScore());
    }


    @Test
    @DisplayName("Se puede deshacer el empuje de una caja")
    void testUndoBoxPush() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.getGameBoard()[3][4] = BoardElement.Element.BOX;
        gameService.getGameBoard()[4][4] = BoardElement.Element.EMPTY;
        gameService.movePlayer(1, 0);
        gameService.undoMove();
        assertEquals(BoardElement.Element.PLAYER, gameService.getGameBoard()[2][4], "El jugador debe volver a su posición original");
        assertEquals(BoardElement.Element.BOX, gameService.getGameBoard()[3][4], "La caja debe ser 'tirada' hacia atrás");
        assertEquals(BoardElement.Element.EMPTY, gameService.getGameBoard()[4][4], "El espacio donde se empujó la caja debe quedar vacío");
    }

    @Test
    @DisplayName("Intentar mover contra una pared no genera movimientos ni altera el score")
    void testMoveAgainstWall() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.getGameBoard()[3][4] = BoardElement.Element.WALL;
        
        int scoreBefore = gameService.getLevelScore();
        gameService.movePlayer(1, 0);
        
        assertEquals(scoreBefore, gameService.getLevelScore(), "La puntuación no debería variar si chocas con una pared");
        GameSnapshot snapshot = gameService.createSnapshot();
        assertTrue(snapshot.getMovements().isEmpty(), "No debería registrarse ningún movimiento en la pila");
    }

    @Test
    @DisplayName("Intentar deshacer movimientos cuando la pila está vacía no altera nada")
    void testUndoWithEmptyStack() throws InvalidLevelException {
        gameService.startLevel(1);
        assertDoesNotThrow(() -> gameService.undoMove());
        assertEquals(0, gameService.getLevelScore());
    }

    @Test
    @DisplayName("Evitar la acumulación repetida de puntuación total en un nivel ya completado")
    void testCompleteLevelMultipleTimes() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.getGameBoard()[3][4] = BoardElement.Element.EMPTY;
        gameService.movePlayer(1, 0); // score = 1
        
        gameService.completeLevel(); // totalScore += 1 -> totalScore = 1
        int executionOneTotal = gameService.getTotalScore();
        
        gameService.completeLevel(); // Se llama una segunda vez
        assertEquals(executionOneTotal, gameService.getTotalScore(), "El total score no debe incrementarse de forma duplicada");
    }

    @Test
    @DisplayName("Verificar que la consulta del nombre del nivel y metas funciona")
    void testLevelMetadataQueries() throws InvalidLevelException {
        gameService.startLevel(1);
        assertNotNull(gameService.getLevelName());
        assertNotNull(gameService.getGoalPositions());
    }

    @Test

    void testGetCurrentLevelNum() throws InvalidLevelException{
        gameService.startLevel(1);
        assertEquals(1, gameService.getCurrentLevelNumber());
    }

    @ParameterizedTest
    @DisplayName("Verificar que empujar cajas en diferentes niveles no lanza excepciones")
    @ValueSource(ints = {6, 2, 5})
    void testPushBoxVariations(int levelNumber) throws InvalidLevelException {
        gameService.startLevel(levelNumber);
        assertDoesNotThrow(() -> gameService.movePlayer(0, -1));
    }

    @Test
    void testNotCompleted() throws InvalidLevelException{
        gameService.startLevel(1);
        assertFalse(gameService.isCompleted());
    }

    @Test 
    void testIsCompleted() throws InvalidLevelException{
        gameService.startLevel(1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, -1);

        assertEquals(true, gameService.isCompleted());
    }

    @Test
    @DisplayName("Creación de Snapshot en Service")
    void testcreateSnapshot() throws InvalidLevelException{
        gameService.startLevel(1);
        GameSnapshot snapshot = gameService.createSnapshot();
        assertNotNull(snapshot);
        assertEquals(1, snapshot.getLevelNumber());        
    }

    @Test
    @DisplayName("Puntuación al terminar nivel > 0")
    void testScoreVerify() throws InvalidLevelException {
        gameService.startLevel(1);

        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(-1, 0);
        gameService.movePlayer(0, -1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, 1);
        gameService.movePlayer(1, 0);
        gameService.movePlayer(0, -1);

        if(gameService.isCompleted()){
            gameService.completeLevel();
        }
        assertEquals(true, gameService.getTotalScore() > 0);
    }


    @Test
    @DisplayName("Prueba de reinicio")
    void testRestartCurrentLevel() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.movePlayer(0, 1);
        gameService.restartCurrentLevel();
        assertEquals(0, gameService.getLevelScore());
        assertEquals(1, gameService.getCurrentLevelNumber());
    }

    @Test
    @DisplayName("Prueba de reinicio en nivel completo")
    void testRestartCompletedCurrentLevel() throws InvalidLevelException {
        gameService.startLevel(1);
        gameService.movePlayer(0, 1);
        gameService.completeLevel();
        int totalScore = gameService.getTotalScore();
        int levelScore =  gameService.getLevelScore();
        gameService.restartCurrentLevel();
        assertEquals(0, gameService.getLevelScore());
        assertEquals(1, gameService.getCurrentLevelNumber());
        assertFalse(gameService.getLevelCompletedAttribute());
        assertEquals(totalScore - levelScore, gameService.getTotalScore());
    }

    @Test
    @DisplayName("Test no puede ser null")
    void testGetLevelName() throws InvalidLevelException {
        gameService.startLevel(1);
        assertNotNull(gameService.getLevelName());

    }
    @Test
    @DisplayName("Verificar que la carga del archivo de guardado es correcto")
    void testGameSaveFileLoading() throws InvalidLevelException, JAXBException {
        File archivo = new File("testsavefile.xml");
        gameService.loadGame(archivo);
        assertEquals("Level 1", gameService.getLevelName());
        assertEquals(4, gameService.getLevelScore());
        assertEquals(0, gameService.getTotalScore());
        BoardElement.Element[][] board = gameService.getGameBoard();
        StringBuilder sb = new StringBuilder();
        for (BoardElement.Element[] row : board) {
            for (BoardElement.Element element : row) {
                sb.append(element.getSymbol());
            }
        }
        assertEquals("+++++++++___+__++______++++W___++++_+++++++_#_+++++___++++++++++", sb.toString());
        assertNotNull(gameService.getUndoStack());


    }
}