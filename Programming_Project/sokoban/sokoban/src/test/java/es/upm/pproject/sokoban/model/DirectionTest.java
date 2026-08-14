package es.upm.pproject.sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DirectionTest {

    @Test
    void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    void testUpDecrementsRow() {
        assertEquals(-1, Direction.UP.getdRow());
        assertEquals(0, Direction.UP.getdCol());
    }

    @Test
    void testDownIncrementsRow() {
        assertEquals(1, Direction.DOWN.getdRow());
        assertEquals(0, Direction.DOWN.getdCol());
    }

    @Test
    void testLeftDecrementsCol() {
        assertEquals(0, Direction.LEFT.getdRow());
        assertEquals(-1, Direction.LEFT.getdCol());
    }

    @Test
    void testRightIncrementsCol() {
        assertEquals(0, Direction.RIGHT.getdRow());
        assertEquals(1, Direction.RIGHT.getdCol());
    }
}
