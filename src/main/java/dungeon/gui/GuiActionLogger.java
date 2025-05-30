package dungeon.gui;

import dungeon.engine.ActionLogger;
import javafx.scene.control.TextArea;

/**
 * GUI implementation of ActionLogger that appends to a TextArea
 */
public class GuiActionLogger implements ActionLogger {
    private TextArea textArea;
    
    public GuiActionLogger(TextArea textArea) {
        this.textArea = textArea;
    }
    
    @Override
    public void log(String message) {
        if (textArea != null) {
            // Use Platform.runLater to ensure we're on the JavaFX thread
            javafx.application.Platform.runLater(() -> {
                textArea.appendText(message + "\n");
                // Auto-scroll to bottom
                textArea.setScrollTop(Double.MAX_VALUE);
            });
        }
    }
    
    @Override
    public void clear() {
        if (textArea != null) {
            javafx.application.Platform.runLater(() -> {
                textArea.clear();
                textArea.appendText("=== New Game Started ===\n");
            });
        }
    }
}
