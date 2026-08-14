package es.upm.pproject.sokoban.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LevelTest {

    private Level level;

    @BeforeEach
    void setUp() {
        level = new Level("Level 0", 8, 8);
    }

    // Test Getters before being initialized

    @Test
    void testGetName() {
        assertEquals("Level 0", level.getName());
    }

    @Test
    void testGetRows() {
        assertEquals(8, level.getRows());
    }

    @Test
    void testGetCols() {
        assertEquals(8, level.getCols());
    }

    @Test
    void testGetPlayerPosInitiallyNull() {
        assertNull(level.getPlayerPos());
    }

    @Test
    void testGetGoalPositionsInitiallyEmpty() {
        assertTrue(level.getGoalPositions().isEmpty());
    }

    @Test
    void testGetCellEmpty() {
        assertEquals(BoardElement.Element.EMPTY, level.getCell(0, 4));
    }

    // Test Getters after being modified

    @Test
    void testGetCellAfterSetting() {
        level.setCell(1, 1, BoardElement.Element.WALL);
        assertEquals(BoardElement.Element.WALL, level.getCell(1, 1));
    }

    @Test
    void testGetPlayerPosAfterSet() {
        level.setCell(2, 3, BoardElement.Element.PLAYER);
        Position p = level.getPlayerPos();
        assertNotNull(p);
        assertEquals(2, p.getRow());
        assertEquals(3, p.getCol());
    }

    @Test
    void testGetGoalPositionsAfterSet() {
        level.setCell(0, 1, BoardElement.Element.GOAL);
        assertEquals(1, level.getGoalPositions().size());
    }

    // === Tests de isValid ===

    @Test
    void testIsValidOk() {
        level.setCell(0, 0, BoardElement.Element.PLAYER);
        level.setCell(0, 1, BoardElement.Element.BOX);
        level.setCell(0, 2, BoardElement.Element.GOAL);
        assertTrue(level.isValid());
    }

    @Test
    void testIsValidNoPlayer() {
        level.setCell(0, 0, BoardElement.Element.BOX);
        level.setCell(0, 1, BoardElement.Element.GOAL);
        assertFalse(level.isValid());
    }

    @Test
    void testIsNotValidTwoPlayers() {
        level.setCell(0, 0, BoardElement.Element.PLAYER);
        level.setCell(0, 1, BoardElement.Element.PLAYER);
        level.setCell(0, 2, BoardElement.Element.BOX);
        level.setCell(0, 3, BoardElement.Element.GOAL);
        assertFalse(level.isValid());
        
    }

    @Test
    void testIsNotValidNoBoxes() {
        level.setCell(0, 0, BoardElement.Element.PLAYER);
        level.setCell(0, 1, BoardElement.Element.GOAL);
        assertFalse(level.isValid());
    }

    @Test
    void testIsNotValidNoGoals() {
        level.setCell(0, 0, BoardElement.Element.PLAYER);
        level.setCell(0, 1, BoardElement.Element.BOX);
        assertFalse(level.isValid());
       
    }

    @Test
    void testIsValidNotEqualGoals() {
        level.setCell(0, 0, BoardElement.Element.PLAYER);
        level.setCell(0, 1, BoardElement.Element.BOX);
        level.setCell(0, 2, BoardElement.Element.BOX);
        level.setCell(0, 3, BoardElement.Element.GOAL);
        assertFalse(level.isValid());
    }

    // === Tests for level completion ===
    @Test
    void testLevelNotCompleted() {
        level.setCell(0, 0, BoardElement.Element.PLAYER);
        level.setCell(0, 1, BoardElement.Element.BOX);
        level.setCell(0, 2, BoardElement.Element.GOAL);
        assertFalse(level.isCompleted());
    }

    @Test
    void testLevelCompletedWhenAllBoxesOnGoals() {
        level.setCell(0, 1, BoardElement.Element.PLAYER);
        level.setCell(0, 2, BoardElement.Element.GOAL);
        level.setCell(0, 2, BoardElement.Element.BOX);
        assertTrue(level.isCompleted());
    }

    @Test
    void testLevelNotCompletedWhenOnlyOneBoxOnGoal() {
        level.setCell(0, 0, BoardElement.Element.PLAYER);
        level.setCell(1, 1, BoardElement.Element.BOX);
        level.setCell(0, 4, BoardElement.Element.BOX);
        level.setCell(1, 3, BoardElement.Element.GOAL);
        level.setCell(0, 4, BoardElement.Element.GOAL);
        assertFalse(level.isCompleted());
    }

    @Test
    void testSetGoalCellDoesntAddTwoTimes() {
        level.setCell(0, 0, BoardElement.Element.GOAL);
        level.setCell(0, 0, BoardElement.Element.GOAL);
        assertEquals(1, level.getGoalPositions().size());
    }
}