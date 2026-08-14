package es.upm.pproject.sokoban.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.*;

class GameRepositoryTest {
    private GameRepository repository;
    private GameSnapshot snapshot;

    @BeforeEach
    void setUp() {
        repository = new GameRepository();
        
        BoardElement.Element[][] board = {
            {BoardElement.Element.WALL, BoardElement.Element.EMPTY},
            {BoardElement.Element.PLAYER, BoardElement.Element.BOX}
        };
        List<Movement> movements = new ArrayList<>();
        movements.add(new Movement(1, 0, false));
        
        snapshot = new GameSnapshot(1, board, 5, 20, movements);
    }

    @Test
    void testSaveSuccessfully() throws Exception {
        File tempFile = File.createTempFile("sokoban_repo_test", ".xml");
        tempFile.deleteOnExit();

        assertDoesNotThrow(() -> repository.save(snapshot, tempFile));
        
        assertTrue(tempFile.exists(), "El archivo de guardado debería existir.");
        assertTrue(tempFile.length() > 0, "El archivo XML no debería estar vacío.");
    }

    @Test
    void testSaveWithNullParameters() {
        File file = new File("Test_save_game.xml");
        assertThrows(IllegalArgumentException.class, () -> 
            repository.save(null, file), "Should fail if the snapshot is null");

        assertThrows(IllegalArgumentException.class, () -> 
            repository.save(snapshot, null), "Should fail if the target file is null");
    }

    @Test
    void testLoadSuccessfully() throws Exception {
        File tempFile = new File("testsavefile.xml");

        assertDoesNotThrow(() -> repository.load(tempFile));

        GameSnapshot snap = repository.load(tempFile);

        assertEquals(1, snap.getLevelNumber());
        assertEquals(4, snap.getLevelScore());
        assertEquals(0, snap.getTotalScore());
        assertEquals("+++++++++___+__++______++++W___++++_+++++++_#_+++++___++++++++++", snap.getBoardString());
        assertNotNull(snap.getMovements());
    }

    @Test
    void testLoadWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.load(null);
        }, "Should fail if the snapshot is null");
    }

    @Test
    void testLoadNonExistentFileThrowsException() {
        File nonExistent = new File("non_existent_file.xml");
        assertThrows(Exception.class, () -> repository.load(nonExistent));
    }

    @Test
    void testLoadWithCorruptedFileThrowsException() {
        File corruptedSaveFile = new File("corruptedtestsavefile.xml");
        assertThrows(JAXBException.class, () -> repository.load(corruptedSaveFile));
    }

    @Test
    void testSaveToInvalidPathThrowsException() {
        File invalidFile = new File("/"); 
        assertThrows(JAXBException.class, () -> repository.save(snapshot, invalidFile));
    }

}
