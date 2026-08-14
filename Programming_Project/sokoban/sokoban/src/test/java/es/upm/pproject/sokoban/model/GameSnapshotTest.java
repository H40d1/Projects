package es.upm.pproject.sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameSnapshotTest {

    private GameSnapshot snapshot;
    private BoardElement.Element[][] board;
    private List<Movement> expectedMovements;

    @BeforeEach
    void setupGameSnapshotToSave() {
        board = new BoardElement.Element[][] {
            {BoardElement.Element.WALL, BoardElement.Element.EMPTY},
            {BoardElement.Element.PLAYER, BoardElement.Element.BOX}
        };

        expectedMovements = new ArrayList<>();
        expectedMovements.add(new Movement(1, 0, false));
        expectedMovements.add(new Movement(0, 1, true));

        snapshot = new GameSnapshot(1, board, 3, 10, expectedMovements);
    }

    @Test
    void testGetLevelNumber() {
        assertEquals(1, snapshot.getLevelNumber());
    }

    @Test
    void testGetBoardString() {
        assertNotNull(snapshot.getBoardString());
        assertEquals("+_W#", snapshot.getBoardString());
    }

    @Test
    void testGetLevelScore() {
        assertEquals(3, snapshot.getLevelScore());
    }

    @Test
    void testGetTotalScore() {
        assertEquals(10, snapshot.getTotalScore());
    }

    @Test
    void testBoardDimensions() {
        assertEquals(2, snapshot.getRows());
        assertEquals(2, snapshot.getCols());
    }

    @Test
    void testGetMovements() {
        List<Movement> movementsFromSnapshot = snapshot.getMovements();
        assertNotNull(movementsFromSnapshot);
        assertEquals(2, movementsFromSnapshot.size());
        assertEquals(1, movementsFromSnapshot.get(0).getMovimientoX());
        assertTrue(movementsFromSnapshot.get(1).isPushedBox());
    }

    @Test
    void testMovementsListIsImmutable() {
        List<Movement> movementsFromSnapshot = snapshot.getMovements();
        Movement extraMovement = new Movement(0, -1, false);
        
        assertThrows(UnsupportedOperationException.class, () -> {
            movementsFromSnapshot.add(extraMovement);
        });
    }

    @Test
    void testConstructorWithNullMovements() {
        GameSnapshot nullMovementsSnapshot = new GameSnapshot(1, board, 0, 0, null);
        assertNotNull(nullMovementsSnapshot.getMovements());
        assertTrue(nullMovementsSnapshot.getMovements().isEmpty());
    }

    @Test
    void testConstructorWithDefaultJAXB() {
        GameSnapshot emptySnapshot = new GameSnapshot();
        assertNotNull(emptySnapshot.getMovements());
        assertTrue(emptySnapshot.getMovements().isEmpty());
    }

    @Test
    void testSaveSnapshot() throws Exception {
        File tempFile = File.createTempFile("sokoban_save_game", ".xml");
        tempFile.deleteOnExit();
    
        JAXBContext context = JAXBContext.newInstance(GameSnapshot.class);
    
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(snapshot, tempFile);
    
        assertTrue(tempFile.exists(), "The save file should have been created.");
        assertTrue(tempFile.length() > 0, "The save XML file should not be empty.");

        String xmlContent = Files.readString(tempFile.toPath());
        assertTrue(xmlContent.contains("<gameSnapshot>"), "The XML should contain the root element <gameSnapshot>");
        assertTrue(xmlContent.contains("<levelNumber>1</levelNumber>"), "The XML should contain the node <levelNumber>");
        assertTrue(xmlContent.contains("<boardString>+_W#</boardString>"), "The XML should contain the correct board string node");
        assertTrue(xmlContent.contains("<movements>"), "The XML should wrapper node <movements>");
        assertTrue(xmlContent.contains("<movement>"), "The XML should contain individual <movement> tags");
    }
}
