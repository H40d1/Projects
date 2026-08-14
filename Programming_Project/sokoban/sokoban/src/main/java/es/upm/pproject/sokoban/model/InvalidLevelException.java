package es.upm.pproject.sokoban.model; //following the teacher repository

public class InvalidLevelException extends Exception {

    public InvalidLevelException(String msg) {
        super(msg);
    }
}