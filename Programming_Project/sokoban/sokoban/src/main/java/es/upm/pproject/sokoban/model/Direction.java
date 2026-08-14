package es.upm.pproject.sokoban.model;

public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int dRow;
    private final int dCol;

    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int getdRow() {
        return dRow;
    }

    public int getdCol() {
        return dCol;
    }
}