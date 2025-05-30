package dungeon.engine;

/**
 * Console implementation of ActionLogger that prints to System.out
 */
public class ConsoleActionLogger implements ActionLogger {
    @Override
    public void log(String message) {
        System.out.println(message);
    }
    
    @Override
    public void clear() {
        // Console can't be cleared, so we'll just print a separator
        System.out.println("\n=== New Game Started ===\n");
    }
}
