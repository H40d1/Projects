# Sokoban:

### Programming Project 2025-2026
### UPM - ETSIINF

### <u>Authors</u>

- Haodi Lin Sun and his team

## <u>Game description</u>

Sokoban is a jigsaw puzzle game created by Hiroyuki Imabayashi in the 1980s. The goal is to push all boxes onto their target positions in a warehouse. This application is developed with Java as part of the Programming Project course.

## Game Rules

1. The player can only move horizontally or vertically.
    
2. Walls cannot be traversed.
    
3. The player can push boxes. A box can only be pushed if the space behind it is empty or a goal position.
    
4. A level is completed when all boxes are on goal positions.
    
5. The game automatically advances to the next level upon completion.
    
6. When all levels are completed, a congratulations message is shown with the total score.
    

## Scoring

- **Level score**: Number of movements made in the current level.
    
- **Total score**: Sum of level scores from all completed levels.

## <u>Level Files</u>

Level files are stored in the `resources/levels/` directory. These files must be named following the format: level_N.txt, where N is a positive integer starting from 1. The levels must be numbered sequentially.

Each level file contains the following symbols:

| Symbol | Meaning                |
| ------ | ---------------------- |
| `+`    | Wall                   |
| `*`    | Goal position          |
| `#`    | Box                    |
| `W`    | Player (Warehouse man) |
| `_`    | Empty space            |

#### When is a level valid?

- There must be exactly one player (`W`).
- There must be at least one box (`#`) and one goal position (`*`).
- The total number of boxes must be exactly equal to the total number of goals.

### <u>Save files</u>

The game is saved in XML format using JAXB framework. The save file contains:

- Board state
- Current level number
- Level score
- Total score
- History of movements (to preserve the Undo stack after loading)

## <u>Logging</u>

We use SLF4J with Log4j for logging. Log files are stored in the `logs/` directory. These logs record:
- Game initialization (View, Model and Controller startups)
- Level loading, level transitions and completions
- Player movements and undo actions
- Game restart, save and load operations
- Errors like: if a level fails to load or restart.

## <u> Project Structure </u>

```
sokoban/
	deliverables/
		backlog1.csv
		backlog2.csv
		backlog3.csv
		board1.png
		board2.jpg
		board3.jpg
		sprint1.csv
		sprint1-board.jpg
		sprint2.csv
		sprint2-board.jpg
    src/
        main/
            java/es/upm/pproject/sokoban/
                Main.java
                controller/
                    GameController.java
                model/
                    BoardElement.java
                    Direction.java
                    GameRepository.java
                    GameService.java
                    GameSnapshot.java
                    InvalidLevelException.java
                    Level.java
                    LevelLoader.java
                    Movement.java
                    Position.java
                view/
                    BoardPanel.java
                    ErrorDialog.java
                    GameCompletedDialog.java
                    GameFrame.java
                    GameMenuBar.java
                    StatusPanel.java
            resources/
                log4j.properties
                levels/
		            level_1.txt
		            level_2.txt
		            level_3.txt
		            level_4.txt
		            level_5.txt
		            level_6.txt
		            level_7.txt
		            level_8.txt
		            level_9.txt
		            level_10.txt
        test/java/es/upm/pproject/sokoban/model
            BoardElementTest.java
            DirectionTest.java
            GameRepositoryTest.java
            GameServiceTest.java
            GameSnapshotTest.java
            GameTest.java
            LevelLoaderTest.java
            LevelTest.java
            MovementTest.java
            PositionTest.java
    textures/
        wall.png
        caja.png
        cajaGol.png
        floor.png
        meta.png
        jugador.png
    logs/
    pom.xml
    .gitlab-ci.yml
    README.md
```
## <u>File formats</u>

The level files follow the mandatory structure required by the specification. The first line contains the level name, the second line contains the dimensions (rows and columns), followed by the board layout.

Example of level_1.txt:

```
Level 1
8 8 
++++++++
+__+++++
+__+++++
+______+
++W*+#_+
+___+__+
+___++++
++++++++
```

JAXB will generate the XML file using data from GameSnapshot class to serialize the active board layout, level metadata, and score tracking.

## <u>Other information</u>

### Controls
| Key | Action |
|-----|--------|
| W / Arrow Up | Move player up |
| A / Arrow Left | Move player left |
| S / Arrow Down | Move player down |
| D / Arrow Right | Move player right |

### Menu Options
| Menu | Option | Shortcut | Description |
|------|--------|----------|-------------|
| File | New Game | Ctrl+N | Start a new game from level 1 |
| File | Restart Level | Ctrl+R | Restart the current level |
| File | Load Game | Ctrl+L | Load a saved game |
| File | Save Game | Ctrl+S | Save the current game |
| File | Exit | Ctrl+E | Close the application |
| Edit | Undo | Ctrl+U | Undo the last movement |
| Help | How to Play | -- | Show game instructions |
| Help | About | -- | Show authors and version |

### Undo System
- Each movement is recorded and can be undone one by one with no limits within the level.
- Pushing a box counts as one movement. Undoing it restores both the player and the box positions.
- If an undo action is performed after winning, the game safely reverts the movement and dynamically decreases the global total score accordingly.

### Textures
- The game uses custom images for walls, boxes, goals, and the player.
- Textures are loaded from the `textures/` folder at startup.
- When a box is placed on a goal position, its texture changes to indicate it is correctly placed.

### How to Run

**Prerequisites**: JUnit 5.6 + Java 11 + Maven 3.6

**Compile and run** :

	mvn clean compile exec:java

