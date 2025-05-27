package dungeon.engine.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages the high score leaderboard for the game.
 * Stores the top 5 scores in a JSON file in the user's home directory.
 */
public class ScoreBoard {
    
    private static final int MAX_SCORES = 5;
    private static final String SCORE_FILE_NAME = ".minidungeon.scores.json";
    
    private final File scoreFile;
    private final ObjectMapper objectMapper;
    
    /**
     * Creates a new ScoreBoard instance.
     * The scores are stored in ~/.minidungeon.scores.json
     */
    public ScoreBoard() {
        this.scoreFile = new File(System.getProperty("user.home"), SCORE_FILE_NAME);
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Creates a ScoreBoard with a custom file location (for testing).
     *
     * @param scoreFile the file to store scores in
     */
    public ScoreBoard(File scoreFile) {
        this.scoreFile = scoreFile;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Adds a new score to the leaderboard.
     * Maintains only the top 5 scores, sorted in descending order.
     *
     * @param playerName the name of the player
     * @param score the score achieved
     * @param level the level reached
     * @return true if the score was added to the top 5, false otherwise
     */
    public boolean addScore(String playerName, int score, int level) {
        try {
            List<ScoreEntry> scores = loadScores();
            
            // Add new score
            ScoreEntry newEntry = new ScoreEntry(playerName, score, level, System.currentTimeMillis());
            scores.add(newEntry);
            
            // Sort by score (descending), then by timestamp (ascending for same scores)
            scores.sort((a, b) -> {
                int scoreCompare = Integer.compare(b.score, a.score);
                if (scoreCompare != 0) {
                    return scoreCompare;
                }
                return Long.compare(a.timestamp, b.timestamp);
            });
            
            // Keep only top 5
            boolean wasAdded = scores.indexOf(newEntry) < MAX_SCORES;
            if (scores.size() > MAX_SCORES) {
                scores = scores.subList(0, MAX_SCORES);
            }
            
            // Save back to file
            saveScores(scores);
            
            return wasAdded;
        } catch (IOException e) {
            System.err.println("Error managing scores: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the current top scores.
     *
     * @return a list of the top scores, sorted in descending order
     */
    public List<ScoreEntry> getTopScores() {
        try {
            return new ArrayList<>(loadScores());
        } catch (IOException e) {
            System.err.println("Error loading scores: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Clears all scores from the leaderboard.
     */
    public void clearScores() {
        try {
            saveScores(new ArrayList<>());
        } catch (IOException e) {
            System.err.println("Error clearing scores: " + e.getMessage());
        }
    }
    
    /**
     * Gets the lowest score in the top 5, or 0 if there are fewer than 5 scores.
     *
     * @return the minimum score needed to make the leaderboard
     */
    public int getMinimumTopScore() {
        List<ScoreEntry> scores = getTopScores();
        if (scores.size() < MAX_SCORES) {
            return 0;
        }
        return scores.get(scores.size() - 1).score;
    }
    
    /**
     * Loads scores from the JSON file.
     */
    private List<ScoreEntry> loadScores() throws IOException {
        if (!scoreFile.exists()) {
            return new ArrayList<>();
        }
        
        CollectionType listType = objectMapper.getTypeFactory()
            .constructCollectionType(List.class, ScoreEntry.class);
        
        return objectMapper.readValue(scoreFile, listType);
    }
    
    /**
     * Saves scores to the JSON file.
     */
    private void saveScores(List<ScoreEntry> scores) throws IOException {
        // Create parent directories if they don't exist
        scoreFile.getParentFile().mkdirs();
        
        objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(scoreFile, scores);
    }
    
    /**
     * Represents a single score entry in the leaderboard.
     */
    public static class ScoreEntry {
        public String playerName;
        public int score;
        public int level;
        public long timestamp;
        
        // Default constructor for Jackson
        public ScoreEntry() {}
        
        public ScoreEntry(String playerName, int score, int level, long timestamp) {
            this.playerName = playerName;
            this.score = score;
            this.level = level;
            this.timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            return String.format("%s: %d points (Level %d)", playerName, score, level);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ScoreEntry that = (ScoreEntry) obj;
            return score == that.score && 
                   level == that.level && 
                   timestamp == that.timestamp &&
                   Objects.equals(playerName, that.playerName);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(playerName, score, level, timestamp);
        }
    }
}
