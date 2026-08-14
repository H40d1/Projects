package es.upm.pproject.sokoban;

import es.upm.pproject.sokoban.controller.GameController;
import es.upm.pproject.sokoban.model.GameService;
import es.upm.pproject.sokoban.view.GameFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;

public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            logger.info("Starting Game");
            logger.info("Starting GameFrame view");
            GameFrame view = new GameFrame();
            logger.info("Starting GameService");
            GameService service = new GameService();
            logger.info("Starting GameController");
            GameController controller = new GameController(service,view);


            view.setGameController(controller);
            view.setVisible(true);
            logger.info("Game successfully started");
        });
    }
    
}
