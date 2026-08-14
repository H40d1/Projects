package es.upm.pproject.sokoban.model;

import static org.junit.jupiter.api.Assertions.*; //Assert True, Equals
import org.junit.jupiter.api.Test;
import es.upm.pproject.sokoban.model.BoardElement.Element;//We can access directly to elements of Element, since it's an enum

class BoardElementTest {

    //Test to see if the method gives the same element
    @Test
    void testFromCharEmpty() {
        assertEquals(Element.EMPTY, BoardElement.elementChar('_'));
    }

    @Test
    void testFromCharWall() {
        assertEquals(Element.WALL, BoardElement.elementChar('+'));
    }

    @Test
    void testFromCharBox() {
        assertEquals(Element.BOX, BoardElement.elementChar('#'));
    }

    @Test
    void testFromCharGoal() {
        assertEquals(Element.GOAL, BoardElement.elementChar('*'));
    }

    @Test
    void testFromCharPlayer() {
        assertEquals(Element.PLAYER, BoardElement.elementChar('W'));
    }

    @Test
    void testFromCharInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            BoardElement.elementChar('X');
        });
    }

    //Tests to get a char Symbol given an element from enum Element

    @Test
    void testGetSymbolEmpty() {
        assertEquals('_', Element.EMPTY.getSymbol());
    }    

    @Test
    void testGetSymbolWall() {
        assertEquals('+', Element.WALL.getSymbol());
    }

    @Test
    void testGetSymbolBox() {
        assertEquals('#', Element.BOX.getSymbol());
    }

    @Test
    void testGetSymbolGoal() {
        assertEquals('*', Element.GOAL.getSymbol());
    }

    @Test
    void testGetSymbolPlayer() {
        assertEquals('W', Element.PLAYER.getSymbol());
    }

}
