package dungeon.gui;

import dungeon.engine.*;
import dungeon.engine.cells.*;
import dungeon.engine.persistence.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class Controller {
    @FXML
    private GridPane gridPane;
    
    @FXML
    private Label hpLabel;
    
    @FXML
    private Label scoreLabel;
    
    @FXML
    private Label stepsLabel;
    
    @FXML
    private Label levelLabel;
    
    @FXML
    private Label seedLabel;
    
    @FXML
    private Button upButton;
    
    @FXML
    private Button downButton;
    
    @FXML
    private Button leftButton;
    
    @FXML
    private Button rightButton;
    
    @FXML
    private Button viewScoreboardButton;
    
    @FXML
    private Button saveGameButton;
    
    @FXML
    private Button loadGameButton;

    @FXML
    private Button newGameButton;

    private GameEngine engine;

    @FXML
    public void initialize() {
        engine = new GameEngine(2); // Use moderate difficulty
        updateGui();
        updateLabels();
        
        // Use Platform.runLater to ensure the scene is fully loaded before requesting focus
        javafx.application.Platform.runLater(() -> {
            if (gridPane != null && gridPane.getScene() != null) {
                gridPane.getScene().getRoot().requestFocus();
            }
        });
    }
    
    @FXML
    public void handleKeyPress(KeyEvent event) {
        Direction direction = Direction.fromKeyCode(event.getCode());
        
        if (direction != null) {
            handleMove(direction);
        }
    }
    
    private void showGameOverAlert() {
        String statusMessage = engine.getStatusMessage();
        boolean isWin = statusMessage.toLowerCase().contains("won") || 
                       statusMessage.toLowerCase().contains("congratulations");
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Finished!");
        alert.setContentText(statusMessage);
        
        // If the player won, offer to add their score to the leaderboard
        if (isWin) {
            alert.showAndWait();
            
            // Prompt for player name to add to scoreboard
            TextInputDialog nameDialog = new TextInputDialog("Player");
            nameDialog.setTitle("High Score!");
            nameDialog.setHeaderText("Congratulations on completing the game!");
            nameDialog.setContentText("Enter your name for the leaderboard:");
            
            Optional<String> result = nameDialog.showAndWait();
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                ScoreBoard scoreBoard = new ScoreBoard();
                Player player = engine.getPlayer();
                boolean addedToTop5 = scoreBoard.addScore(result.get().trim(), 
                    player.getScore(), player.getLevel());
                
                if (addedToTop5) {
                    Alert scoreAlert = new Alert(Alert.AlertType.INFORMATION);
                    scoreAlert.setTitle("High Score Added!");
                    scoreAlert.setContentText("Your score has been added to the top 5!");
                    scoreAlert.showAndWait();
                }
            }
        } else {
            alert.showAndWait();
        }
        
        // After handling game over, offer to start a new game
        Alert newGameAlert = new Alert(Alert.AlertType.CONFIRMATION);
        newGameAlert.setTitle("Start New Game?");
        newGameAlert.setHeaderText("Game Over");
        newGameAlert.setContentText("Would you like to start a new game?");
        
        Optional<javafx.scene.control.ButtonType> newGameResult = newGameAlert.showAndWait();
        if (newGameResult.isPresent() && newGameResult.get() == javafx.scene.control.ButtonType.OK) {
            newGame(); // Start a new game
        }
    }
    
    private void updateLabels() {
        Player player = engine.getPlayer();
        
        if (hpLabel != null) {
            hpLabel.setText("HP: " + player.getHp());
        }
        
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + player.getScore());
        }
        
        if (stepsLabel != null) {
            stepsLabel.setText("Steps: " + player.getSteps());
        }
        
        if (levelLabel != null) {
            levelLabel.setText("Level: " + player.getLevel());
        }
        
        if (seedLabel != null) {
            seedLabel.setText("Seed: " + engine.getSeed());
        }
    }

    private void updateGui() {
        // Clear old GUI grid pane
        gridPane.getChildren().clear();

        // Loop through map board and add each cell into grid pane
        for(int i = 0; i < engine.getSize(); i++) {
            for (int j = 0; j < engine.getSize(); j++) {
                Cell cell = engine.getMap()[i][j];
                
                // Create a CellView for this cell
                CellView cellView = new CellView(cell, i, j);
                
                // Check if player is at this position
                Position playerPos = engine.getPlayer().getPosition();
                boolean isPlayerHere = (i == playerPos.getRow() && j == playerPos.getCol());
                cellView.setPlayerHere(isPlayerHere);
                
                // Add the cellView to the grid
                gridPane.add(cellView, j, i);
            }
        }
        gridPane.setGridLinesVisible(true);
    }
    
    @FXML
    public void moveUp() {
        handleMove(Direction.UP);
    }
    
    @FXML
    public void moveDown() {
        handleMove(Direction.DOWN);
    }
    
    @FXML
    public void moveLeft() {
        handleMove(Direction.LEFT);
    }
    
    @FXML
    public void moveRight() {
        handleMove(Direction.RIGHT);
    }
    
    @FXML
    public void showScoreboard() {
        ScoreBoard scoreBoard = new ScoreBoard();
        List<ScoreBoard.ScoreEntry> scores = scoreBoard.getTopScores();
        
        StringBuilder sb = new StringBuilder("Top 5 Scores:\n\n");
        if (scores.isEmpty()) {
            sb.append("No scores yet. Be the first!");
        } else {
            for (int i = 0; i < scores.size(); i++) {
                ScoreBoard.ScoreEntry entry = scores.get(i);
                sb.append(String.format("%d. %s - %d points (Level %d)\n", 
                    i + 1, entry.playerName, entry.score, entry.level));
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("High Scores");
        alert.setHeaderText("Leaderboard");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }
    
    @FXML
    public void saveGame() {
        try {
            SaveState saveState = new SaveState(engine);
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Game");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save Files", "*.save")
            );
            
            File file = fileChooser.showSaveDialog(gridPane.getScene().getWindow());
            if (file != null) {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                    oos.writeObject(saveState);
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Game Saved");
                alert.setContentText("Game saved successfully!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setContentText("Failed to save game: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    public void loadGame() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Game");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save Files", "*.save")
            );
            
            File file = fileChooser.showOpenDialog(gridPane.getScene().getWindow());
            if (file != null) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    SaveState saveState = (SaveState) ois.readObject();
                    engine = saveState.restoreGame();
                    updateGui();
                    updateLabels();
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game Loaded");
                    alert.setContentText("Game loaded successfully!");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setContentText("Failed to load game: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    public void newGame() {
        engine = new GameEngine(2); // Start a new game with moderate difficulty
        updateGui();
        updateLabels();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("New Game");
        alert.setContentText("New game started! Good luck!");
        alert.showAndWait();
    }

    /**
     * Handles movement in a given direction (used by both keyboard and button input)
     */
    private void handleMove(Direction direction) {
        // Don't process input if game is over
        if (engine.isGameOver()) {
            return;
        }
        
        boolean moved = engine.move(direction);
        
        if (moved) {
            updateGui();
            updateLabels();
            
            // Check for game over and show alert
            if (engine.isGameOver()) {
                showGameOverAlert();
            }
        }
    }
}
