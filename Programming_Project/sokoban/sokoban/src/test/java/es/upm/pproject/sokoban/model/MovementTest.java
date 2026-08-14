package es.upm.pproject.sokoban.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MovementTest {

    @Test
    void testMovementCreationAndGetters() {
        Movement move = new Movement(0, -1, true);
        
        assertEquals(0, move.getMovimientoX());
        assertEquals(-1, move.getMovimientoY());
        assertTrue(move.isPushedBox());
    }
}