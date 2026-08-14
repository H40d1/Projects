package es.upm.pproject.sokoban.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LevelLoaderTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    @DisplayName("Verificar la carga de los niveles iniciales")
    void testLoadLevel(int levelNumber) throws InvalidLevelException {
        Level level = LevelLoader.loadLevel(levelNumber);
            assertNotNull(level);
            assertTrue(level.isValid());
            assertEquals("Level " + levelNumber, level.getName());
            assertEquals(8, level.getRows());
            assertEquals(8, level.getCols());
            assertNotNull(level.getPlayerPos());
            assertFalse(level.getGoalPositions().isEmpty());
    }

    @Test
    void testLoadNonexistentLevel() {
        assertThrows(InvalidLevelException.class, () -> LevelLoader.loadLevel(404));
    }

    @Test
    void testLoadLevelWithLessThanThreeLinesThrowsException() {
        assertThrows(InvalidLevelException.class, () -> LevelLoader.loadLevel(101));
    }

    @Test
    void testLoadLevelInvalidStructureThrowsException() {
        assertThrows(InvalidLevelException.class, () -> LevelLoader.loadLevel(102));
    }

}