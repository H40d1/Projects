package es.upm.pproject.sokoban.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Movement {
    private int movimientoX;
    private int movimientoY;
    private boolean pushedBox;

    public Movement() {
        this.movimientoX = 0;
        this.movimientoY = 0;
        this.pushedBox = false;
    }

    public Movement(int movimientoX, int movimientoY, boolean pushedBox) {
        this.movimientoX = movimientoX;
        this.movimientoY = movimientoY;
        this.pushedBox = pushedBox;
    }

    public int getMovimientoX() {
        return movimientoX;
    }

    public int getMovimientoY() {
        return movimientoY;
    }

    public boolean isPushedBox() {
        return pushedBox;
    }
}