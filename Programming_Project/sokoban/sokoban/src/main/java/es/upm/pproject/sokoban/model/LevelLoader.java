package es.upm.pproject.sokoban.model;

import java.io.InputStream; //In order to get file data
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LevelLoader{
	//Getting levels *.txt
	private static final Logger logger = LoggerFactory.getLogger(LevelLoader.class);


    @ExcludedFromGeneratedLineCoverage
    private LevelLoader(){
        throw new IllegalStateException("Utility class");
    }


	public static Level loadLevel(int lvlN) throws InvalidLevelException {
		String path = "/levels/level_" + lvlN + ".txt"; //Java searchs in the classpath
		InputStream myTXT = LevelLoader.class.getResourceAsStream(path); //Get the file

		logger.info("Loading level {}", lvlN);

		if(myTXT == null){
			logger.warn("Level {} not found", lvlN);
			throw new InvalidLevelException("Level " + lvlN + " not found");
		}

		List<String> lines1 = new ArrayList<>();

		//The scanner closes automatically in case of any failure
		try (Scanner in = new Scanner(myTXT)) {
        	while (in.hasNextLine()) {		//Verify if there is next line
            	lines1.add(in.nextLine());	//Goes directly to next
        	}
    	}

		if (lines1.size() < 3){
			throw new InvalidLevelException("The level mustn't be empty and needs the name and dimensions");
		}

		//Get dimensions
		String[] dimensions = lines1.get(1).trim().split("\\s+");
    	int rows = Integer.parseInt(dimensions[0]);
    	int cols = Integer.parseInt(dimensions[1]); 

		//We create the level
		Level level = new Level(lines1.get(0), rows, cols);
		char c; //Character storer

		for(int i = 0; i < rows; i++){
			String line = lines1.get(i+2); //we got the row
			for(int j = 0; j < cols && j < line.length(); j++){//Storing with column		
				c = line.charAt(j);
				BoardElement.Element element = BoardElement.elementChar(c);
				level.setCell(j, i, element);
			}
		}

		if (!level.isValid()) {
        	throw new InvalidLevelException("Level validation failed for level " + lvlN);
    	}

		logger.info("Level {} loaded successfully", lvlN);
		return level;
	}

}