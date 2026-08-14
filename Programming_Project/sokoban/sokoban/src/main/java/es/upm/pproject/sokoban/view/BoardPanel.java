package es.upm.pproject.sokoban.view;

import es.upm.pproject.sokoban.model.BoardElement;
import es.upm.pproject.sokoban.model.Position;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.List;


public class BoardPanel extends JPanel {

    private BoardElement.Element[][] boardElements;

    private transient List<Position> goals;
    private static final BufferedImage wall;
    private static final BufferedImage box;
    private static final BufferedImage goal;
    private static final BufferedImage player;
    private static final BufferedImage boxOnGoal;
    private static final BufferedImage floor;

    static {
        try {
            wall = ImageIO.read(new File("textures/wall.png"));
            floor = ImageIO.read(new File("textures/floor.png"));
            box = ImageIO.read(new File("textures/caja.png"));
            goal = ImageIO.read(new File("textures/meta.png"));
            player = ImageIO.read(new File("textures/jugador.png"));
            boxOnGoal = ImageIO.read(new File("textures/cajaGol.png"));
        } catch (IOException e) {
            throw new IOError(e);
        }
    }


    public BoardPanel(){
        this.setLayout(null);
        setBackground(Color.WHITE);
        add(new JLabel());
        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        drawBackground(g2d);
        drawFloor(g2d);
        drawGoals(g2d);
        drawEntities(g2d);

        g2d.dispose();

    }
    
    private void drawBackground(Graphics2D g2d){
        g2d.drawImage(wall, 0, 0, getWidth(), getHeight(), null);
    }    
        
    private void drawFloor(Graphics2D g2d){
        for(int i = 0; i < boardElements.length; i++){
            for(int j = 0; j < boardElements[i].length; j++){
                if(boardElements[i][j].getSymbol() != '+'){
                    g2d.drawImage(floor,i*64, j*64, null);
                }
            }
        }
    }
        
    private void drawGoals(Graphics2D g2d){
        for (int i = 0; i < goals.size(); i++) {
            Position p = goals.get(i);
            g2d.drawImage(goal,p.getRow()*64,p.getCol()*64,null);
        }
    }
        

    private void drawEntities(Graphics2D g2d){
        for(int i = 0; i < boardElements.length; i++){
            for(int j = 0; j < boardElements[i].length; j++){
                drawEntity(g2d, i, j);
            }
        }
    }
        
    private void drawEntity(Graphics2D g2d, int i, int j){

        switch (boardElements[i][j].getSymbol()){
            case '#':
                drawBox(g2d, i, j);
                break;
            case 'W':
                g2d.drawImage(player,i*64,j*64,null);
                break;
            default:break;
        }
    }
    private void drawBox(Graphics2D g2d, int i, int j){
        if(isGoal(i, j)){
            g2d.drawImage(boxOnGoal, i*64, j*64, null);
        } else {
            g2d.drawImage(box,i*64,j*64,null);
        }
    }

    public void updateGraphics(BoardElement.Element[][] board, List<Position> metas){
        boardElements = board;
        this.goals = metas;
        repaint();
    }

    //Presentation method: Know when to paint what
    private boolean isGoal(int row, int col){

        boolean result = false;
        for (int i = 0; i < goals.size(); i++){
            Position goalPos = goals.get(i);
            if(goalPos.getRow() == row && goalPos.getCol() == col){
                result = true;
            }
        }
        return result;
    }


}