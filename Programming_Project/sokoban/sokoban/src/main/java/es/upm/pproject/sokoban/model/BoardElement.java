package es.upm.pproject.sokoban.model;


public class BoardElement {

	public enum Element{ // We indicate in an enumeration every piece of the game
		EMPTY('_'),
		WALL('+'),
		GOAL('*'),
		BOX('#'),
		PLAYER('W'); //This works for the constructor, which saves every element in this.symbol
		//So in this case, symbol, the attribute won't return null.

		private final char symbol; //This will be the attribute
		//It can be, empty, a wall, Goal, a box, or a player

		//Element constructor
		private Element(char symbol) {
			this.symbol = symbol;
		}

		public char getSymbol(){
			return symbol;
		}
	}

	public static Element elementChar(char c){ //Values is an implicit method for enum
		for(Element e : Element.values()){ //Search from the implicit list of Element
			if(e.getSymbol() == c){
				return e;
			}
		} //If there is no c
		throw new IllegalArgumentException("The character " + c + " is invalid");
	}
}