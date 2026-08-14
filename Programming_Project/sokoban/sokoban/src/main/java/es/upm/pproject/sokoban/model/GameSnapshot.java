package es.upm.pproject.sokoban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "gameSnapshot")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameSnapshot {
    private int levelNumber;
    private int levelScore;
    private int totalScore;
    private int rows;
    private int cols;
    private String boardString;

    @XmlElementWrapper(name = "movements")
    @XmlElement(name = "movement")
    private List<Movement> movements;

    private static final Logger logger = LoggerFactory.getLogger(GameSnapshot.class);

    public GameSnapshot() {
        this.movements = new ArrayList<>();
    }

    public GameSnapshot(int levelNumber, BoardElement.Element[][] board, int levelScore, int totalScore, List<Movement> movements) {
        this.levelNumber = levelNumber;
        this.levelScore = levelScore;
        this.totalScore = totalScore;
        this.movements = movements != null ? new ArrayList<>(movements) : new ArrayList<>();

        if (board != null && board.length > 0) {
            this.rows = board.length;
            this.cols = board[0].length;
            this.boardString = flattenBoard(board);
        }

        logger.info("Snapshot created for level {} with scores: level={}, total={}, movementsCount={}", 
            levelNumber, levelScore, totalScore, this.movements.size());
    }

    private String flattenBoard(BoardElement.Element[][] board) {
        StringBuilder sb = new StringBuilder();
        for (BoardElement.Element[] row : board) {
            for (BoardElement.Element element : row) {
                sb.append(element.getSymbol());
            }
        }
        return sb.toString();
    }

    public int getLevelNumber() { 
        return levelNumber; 
    }
    public int getLevelScore() { 
        return levelScore; 
    }
    public int getTotalScore() { 
        return totalScore; 
    }
    public int getRows() { 
        return rows; 
    }
    public int getCols() { 
        return cols; 
    }
    public String getBoardString() { 
        return boardString; 
    }
    public List<Movement> getMovements() {
        return Collections.unmodifiableList(movements);
    }
}