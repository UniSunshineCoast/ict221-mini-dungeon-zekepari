import dungeon.engine.persistence.ScoreBoard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TestScoreBoard {
    
    @Test
    void testAddScoreAndRetrieval(@TempDir File tempDir) {
        File scoreFile = new File(tempDir, "test_scores.json");
        ScoreBoard scoreBoard = new ScoreBoard(scoreFile);
        
        // Add a score
        boolean added = scoreBoard.addScore("Alice", 100, 2);
        assertTrue(added, "First score should be added");
        
        // Retrieve scores
        List<ScoreBoard.ScoreEntry> scores = scoreBoard.getTopScores();
        assertEquals(1, scores.size(), "Should have one score");
        assertEquals("Alice", scores.get(0).playerName);
        assertEquals(100, scores.get(0).score);
        assertEquals(2, scores.get(0).level);
    }
    
    @Test
    void testInsertionOrder(@TempDir File tempDir) {
        File scoreFile = new File(tempDir, "test_scores.json");
        ScoreBoard scoreBoard = new ScoreBoard(scoreFile);
        
        // Add scores in mixed order
        scoreBoard.addScore("Charlie", 50, 1);
        scoreBoard.addScore("Alice", 100, 2);
        scoreBoard.addScore("Bob", 75, 1);
        scoreBoard.addScore("Diana", 200, 3);
        scoreBoard.addScore("Eve", 25, 1);
        
        List<ScoreBoard.ScoreEntry> scores = scoreBoard.getTopScores();
        
        // Should be sorted by score descending
        assertEquals(5, scores.size());
        assertEquals("Diana", scores.get(0).playerName);
        assertEquals(200, scores.get(0).score);
        assertEquals("Alice", scores.get(1).playerName);
        assertEquals(100, scores.get(1).score);
        assertEquals("Bob", scores.get(2).playerName);
        assertEquals(75, scores.get(2).score);
        assertEquals("Charlie", scores.get(3).playerName);
        assertEquals(50, scores.get(3).score);
        assertEquals("Eve", scores.get(4).playerName);
        assertEquals(25, scores.get(4).score);
    }
    
    @Test
    void testMaxFiveScores(@TempDir File tempDir) {
        File scoreFile = new File(tempDir, "test_scores.json");
        ScoreBoard scoreBoard = new ScoreBoard(scoreFile);
        
        // Add 7 scores
        scoreBoard.addScore("Player1", 100, 1);
        scoreBoard.addScore("Player2", 200, 2);
        scoreBoard.addScore("Player3", 50, 1);
        scoreBoard.addScore("Player4", 300, 3);
        scoreBoard.addScore("Player5", 150, 2);
        boolean sixth = scoreBoard.addScore("Player6", 75, 1);
        boolean seventh = scoreBoard.addScore("Player7", 25, 1);
        
        assertTrue(sixth, "Sixth score should be added (replaces lower score)");
        assertFalse(seventh, "Seventh score should not be added (too low)");
        
        List<ScoreBoard.ScoreEntry> scores = scoreBoard.getTopScores();
        assertEquals(5, scores.size(), "Should only keep top 5 scores");
        
        // Check that the lowest score is not in the list
        assertFalse(scores.stream().anyMatch(s -> s.playerName.equals("Player7")),
            "Lowest score should not be in top 5");
        
        // Check that scores are in descending order
        for (int i = 0; i < scores.size() - 1; i++) {
            assertTrue(scores.get(i).score >= scores.get(i + 1).score,
                "Scores should be in descending order");
        }
    }
    
    @Test
    void testSameScoroTimestampOrdering(@TempDir File tempDir) throws InterruptedException {
        File scoreFile = new File(tempDir, "test_scores.json");
        ScoreBoard scoreBoard = new ScoreBoard(scoreFile);
        
        // Add same scores with slight time difference
        scoreBoard.addScore("First", 100, 1);
        Thread.sleep(1); // Ensure different timestamp
        scoreBoard.addScore("Second", 100, 1);
        
        List<ScoreBoard.ScoreEntry> scores = scoreBoard.getTopScores();
        assertEquals(2, scores.size());
        
        // First score should come first (earlier timestamp)
        assertEquals("First", scores.get(0).playerName);
        assertEquals("Second", scores.get(1).playerName);
    }
    
    @Test
    void testPersistence(@TempDir File tempDir) {
        File scoreFile = new File(tempDir, "test_scores.json");
        
        // Create first scoreboard and add scores
        ScoreBoard scoreBoard1 = new ScoreBoard(scoreFile);
        scoreBoard1.addScore("Alice", 100, 2);
        scoreBoard1.addScore("Bob", 75, 1);
        
        // Create second scoreboard with same file
        ScoreBoard scoreBoard2 = new ScoreBoard(scoreFile);
        List<ScoreBoard.ScoreEntry> scores = scoreBoard2.getTopScores();
        
        // Should load existing scores
        assertEquals(2, scores.size());
        assertEquals("Alice", scores.get(0).playerName);
        assertEquals("Bob", scores.get(1).playerName);
    }
    
    @Test
    void testClearScores(@TempDir File tempDir) {
        File scoreFile = new File(tempDir, "test_scores.json");
        ScoreBoard scoreBoard = new ScoreBoard(scoreFile);
        
        // Add some scores
        scoreBoard.addScore("Alice", 100, 2);
        scoreBoard.addScore("Bob", 75, 1);
        
        assertEquals(2, scoreBoard.getTopScores().size());
        
        // Clear scores
        scoreBoard.clearScores();
        
        assertEquals(0, scoreBoard.getTopScores().size());
    }
    
    @Test
    void testGetMinimumTopScore(@TempDir File tempDir) {
        File scoreFile = new File(tempDir, "test_scores.json");
        ScoreBoard scoreBoard = new ScoreBoard(scoreFile);
        
        // Empty scoreboard
        assertEquals(0, scoreBoard.getMinimumTopScore());
        
        // Add 3 scores
        scoreBoard.addScore("Alice", 100, 2);
        scoreBoard.addScore("Bob", 75, 1);
        scoreBoard.addScore("Charlie", 50, 1);
        
        // Still less than 5, so minimum is 0
        assertEquals(0, scoreBoard.getMinimumTopScore());
        
        // Add 2 more to fill top 5
        scoreBoard.addScore("Diana", 200, 3);
        scoreBoard.addScore("Eve", 25, 1);
        
        // Now minimum should be the lowest of the 5
        assertEquals(25, scoreBoard.getMinimumTopScore());
    }
    
    @Test
    void testScoreEntryEquality() {
        ScoreBoard.ScoreEntry entry1 = new ScoreBoard.ScoreEntry("Alice", 100, 2, 12345L);
        ScoreBoard.ScoreEntry entry2 = new ScoreBoard.ScoreEntry("Alice", 100, 2, 12345L);
        ScoreBoard.ScoreEntry entry3 = new ScoreBoard.ScoreEntry("Bob", 100, 2, 12345L);
        
        assertEquals(entry1, entry2, "Identical entries should be equal");
        assertNotEquals(entry1, entry3, "Different entries should not be equal");
        assertEquals(entry1.hashCode(), entry2.hashCode(), "Equal entries should have same hash code");
    }
}
