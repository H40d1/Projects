package es.upm.pproject.sokoban.model;

import java.util.Objects;

public class Position {

	private int row;
	private int column;

	public Position(int row, int column){
		this.row = row;
		this.column = column;
	}

	public int getRow(){
		return this.row;
	}

	public int getCol(){
		return this.column;
	}

	@Override
	public boolean equals(Object o) {
    	if (!(o instanceof Position)) {
        	return false;
    	}
    	Position other = (Position) o;
    	return this.row == other.row && this.column == other.column;
	}

	@Override
	public int hashCode() {
    	return Objects.hash(row, column);
	}
}