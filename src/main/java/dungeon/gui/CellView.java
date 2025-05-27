package dungeon.gui;

import dungeon.engine.cells.Cell;
import dungeon.engine.cells.EmptyCell;
import dungeon.engine.cells.EntryCell;
import dungeon.engine.cells.WallCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A JavaFX component that renders a Cell as a StackPane.
 * This component displays the sprite from the cell's spritePath() and includes 
 * a tooltip for debugging information.
 */
public class CellView extends StackPane {
    private static final int CELL_SIZE = 40;
    
    private final Cell cell;
    private final int row;
    private final int col;
    
    /**
     * Creates a new CellView for the given cell at the specified position.
     *
     * @param cell the cell to render
     * @param row the row position in the grid
     * @param col the column position in the grid
     */
    public CellView(Cell cell, int row, int col) {
        this.cell = cell;
        this.row = row;
        this.col = col;
        
        initializeView();
        setupTooltip();
    }
    
    /**
     * Initializes the visual representation of the cell.
     */
    private void initializeView() {
        // Set preferred size
        setPrefSize(CELL_SIZE, CELL_SIZE);
        setMinSize(CELL_SIZE, CELL_SIZE);
        setMaxSize(CELL_SIZE, CELL_SIZE);
        
        // Create background rectangle
        Rectangle background = new Rectangle(CELL_SIZE, CELL_SIZE);
        
        // Set background color based on cell type (fallback)
        if (cell instanceof EmptyCell) {
            background.setFill(Color.LIGHTGRAY);
        } else if (cell instanceof WallCell) {
            background.setFill(Color.DARKGRAY);
        } else if (cell instanceof EntryCell) {
            background.setFill(Color.LIGHTGREEN);
        } else {
            background.setFill(Color.WHITE);
        }
        
        getChildren().add(background);
        
        // Try to load and display sprite
        String spritePath = cell.spritePath();
        if (spritePath != null && !spritePath.isEmpty()) {
            try {
                // Load image from resources
                Image sprite = new Image(getClass().getResourceAsStream("/" + spritePath));
                
                if (!sprite.isError()) {
                    ImageView spriteView = new ImageView(sprite);
                    spriteView.setFitWidth(CELL_SIZE - 4); // Leave small border
                    spriteView.setFitHeight(CELL_SIZE - 4);
                    spriteView.setPreserveRatio(true);
                    
                    getChildren().add(spriteView);
                } else {
                    // If sprite fails to load, keep the background color
                    System.out.println("Warning: Could not load sprite: " + spritePath);
                }
            } catch (Exception e) {
                // If sprite loading fails, keep the background color
                System.out.println("Warning: Could not load sprite: " + spritePath + " - " + e.getMessage());
            }
        }
        
        // Add border
        setStyle("-fx-border-color: black; -fx-border-width: 1px;");
    }
    
    /**
     * Sets up a tooltip showing debug information about this cell.
     */
    private void setupTooltip() {
        String cellType = cell.getClass().getSimpleName();
        String tooltipText = String.format("Position: (%d, %d)\nType: %s\nSprite: %s", 
                                         row, col, cellType, cell.spritePath());
        
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(this, tooltip);
    }
    
    /**
     * Updates the view to highlight if the player is on this cell.
     *
     * @param isPlayerHere true if the player is currently on this cell
     */
    public void setPlayerHere(boolean isPlayerHere) {
        if (isPlayerHere) {
            // Add player sprite overlay
            try {
                Image playerImage = new Image(getClass().getResourceAsStream("/player.png"));
                if (!playerImage.isError()) {
                    ImageView playerView = new ImageView(playerImage);
                    playerView.setFitWidth(CELL_SIZE - 8);
                    playerView.setFitHeight(CELL_SIZE - 8);
                    playerView.setPreserveRatio(true);
                    
                    // Remove any existing player overlay
                    getChildren().removeIf(node -> node.getStyleClass().contains("player-overlay"));
                    
                    playerView.getStyleClass().add("player-overlay");
                    getChildren().add(playerView);
                }
            } catch (Exception e) {
                // Fallback: add red border to indicate player position
                setStyle(getStyle() + " -fx-border-color: red; -fx-border-width: 3px;");
            }
        } else {
            // Remove player overlay
            getChildren().removeIf(node -> node.getStyleClass().contains("player-overlay"));
            // Reset border
            setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        }
    }
    
    /**
     * Gets the cell represented by this view.
     *
     * @return the cell
     */
    public Cell getCell() {
        return cell;
    }
    
    /**
     * Gets the row position of this cell.
     *
     * @return the row
     */
    public int getRow() {
        return row;
    }
    
    /**
     * Gets the column position of this cell.
     *
     * @return the column
     */
    public int getCol() {
        return col;
    }
}
