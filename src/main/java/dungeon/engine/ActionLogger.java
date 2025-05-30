package dungeon.engine;

/**
 * Interface for logging game events to different outputs (console, GUI, etc.)
 */
public interface ActionLogger {
    /**
     * Log a game event message.
     * @param message The message to log
     */
    void log(String message);
    
    /**
     * Clear the action log.
     */
    void clear();
}
