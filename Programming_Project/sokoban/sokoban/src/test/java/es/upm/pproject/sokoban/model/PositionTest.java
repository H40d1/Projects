package es.upm.pproject.sokoban.model;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
    
    @Test
    void testCreatePositionZero() {
        Position p = new Position(0, 0);
        assertEquals(0, p.getRow());
        assertEquals(0, p.getCol());
    }

    @Test
    void testCreatePosition() {
        Position p = new Position(2, 5);
        assertEquals(2, p.getRow());
        assertEquals(5, p.getCol());
    }

    @Test
    void testPositionsAreEqualWhenSameCoordinates() {
        assertEquals(new Position(2, 3), new Position(2, 3));
    }

    @Test
    void testPositionsAreNotEqualWhenDifferentCoordinates() {
        assertNotEquals(new Position(2, 3), new Position(3, 2));
    }

    @Test
    void testNotEqualsNull() {
        Position p = new Position(1, 1);
        assertNotEquals(null, p);
    }

    @Test
    void testEqualsWithSomethingNotPosition() {
        Position p = new Position(1, 1);
        Movement m = new Movement(1,0,false);
        assertNotEquals(p, m);
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        Position p1 = new Position(3, 5);
        Position p2 = new Position(3, 5);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
