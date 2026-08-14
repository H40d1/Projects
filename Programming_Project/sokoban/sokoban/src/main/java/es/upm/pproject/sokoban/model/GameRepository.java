package es.upm.pproject.sokoban.model;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRepository {
    private static final Logger logger = LoggerFactory.getLogger(GameRepository.class);
    private final JAXBContext context;

    @ExcludedFromGeneratedLineCoverage
    public GameRepository() {
        try {
            this.context = JAXBContext.newInstance(GameSnapshot.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Could not configure the game save system", e);
        }
    }

    public void save(GameSnapshot snapshot, File targetFile) throws JAXBException {
        if (snapshot == null || targetFile == null) {
            throw new IllegalArgumentException("The snapshot or target file cannot be null");
        }

        try {
            Marshaller marshaller = context.createMarshaller();
            // Format the XML to make it human-readable when opened
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); 
            
            marshaller.marshal(snapshot, targetFile);
            logger.info("Game saved successfully to: {}", targetFile.getAbsolutePath());
        } catch (JAXBException e) {
            throw new JAXBException("Failed to serialize game state to XML at path: {}" + targetFile.getAbsolutePath(), e); 
        }
    }

    public GameSnapshot load(File targetFile) throws JAXBException {
        if (targetFile == null) {
            throw new IllegalArgumentException("The target file cannot be null");
        }
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            GameSnapshot snapshot = (GameSnapshot) unmarshaller.unmarshal(targetFile);
            logger.info("Game loaded successfully from: {}", targetFile.getAbsolutePath());
            return snapshot;
        }catch (JAXBException e) {
            throw new JAXBException("Failed loading game state from XML at path: {}" + targetFile.getAbsolutePath(), e);
        }
    }
}
