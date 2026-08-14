package es.upm.pproject.sokoban.model;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Level {

    private static final Logger logger = LoggerFactory.getLogger(Level.class);
    private String name;
    private int rows; //Number of rows of table
    private int cols; //Number of columns of table
    private BoardElement.Element[][] board;
    private Position playerPosition;
    private List<Position> goalPositions;

    // Constructor
    public Level(String name, int rows, int cols) {
        this.name = name;
        this.rows = rows;
        this.cols = cols;
        this.board = new BoardElement.Element[rows][cols];
        this.goalPositions = new ArrayList<>(); //List of positions of goal
        // We create the board, to put the pieces of the level
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = BoardElement.Element.EMPTY;
            }
        }
    }

    //We use setCell to set the player (Start point) and the goal position (Goal--> change color)
    //This method is crucial to extract the cells of the level.txt

    public void setCell(Position position, BoardElement.Element element) {
        setCell(position.getRow(), position.getCol(), element);
    }
    public void setCell(int row, int col, BoardElement.Element element) {
        board[row][col] = element;
        if (element == BoardElement.Element.PLAYER) {
            this.playerPosition = new Position(row, col);
        } else if (element == BoardElement.Element.GOAL) {
            Position newGoal = new Position(row, col);
            if (!this.goalPositions.contains(newGoal)) {
                this.goalPositions.add(newGoal);
            }
        }
    }

    // Returns the position of the Element putting the coordinate
    public BoardElement.Element getCell(int row, int col) {
        return board[row][col];
    }
    public BoardElement.Element getCell(Position position) {
        return board[position.getRow()][position.getCol()];
    }

 
    public String getName() { //.txt name
    	return name; 
    }

    public int getRows() { //number of rows
    	return rows;
    }

    public int getCols() { //and columns
    	return cols; 
    }

    public BoardElement.Element[][] getBoard() {
        return board;
    }

    public Position getPlayerPos() {  //Position of player
    	return playerPosition; 
    }

    public List<Position> getGoalPositions() { //Position of goals 
    	return goalPositions; 
    }


    // We need to validate the table of each level
    public boolean isValid() {

    	//Counting player and boxes
        int playerCount = 0;
        int boxCount = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == BoardElement.Element.PLAYER) {
                    playerCount++; //Count player
                } else if (board[r][c] == BoardElement.Element.BOX) {
                    boxCount++; //count box
                }
            }
        }

        //Counting goals
        int goalCount = goalPositions.size();

        //These are board validation tests

        //At least one player
        if (playerCount != 1) {  
            return false;
        }
        //At least one box
        if (boxCount == 0) {
            return false;
        }
        //At least one goal
        if (goalCount == 0) {
            return false;
        }
        //The number of boxes must be equal to the number of goals
        return boxCount == goalCount;
    }

    // All boxes must be on a goal position to complete the level
    public boolean isCompleted() {
        for (Position goal : goalPositions) {
            if (board[goal.getRow()][goal.getCol()] != BoardElement.Element.BOX) {
                return false;
            }
        }
        logger.info("Level '{}' completed: {}", name, true);
        return true;
    }
}