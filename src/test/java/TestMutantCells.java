import dungeon.engine.*;
import dungeon.engine.cells.*;import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestMutantCells {

    @Test
    public void testMutant() {
        // Create a test mutant
        Mutant testMutant = new Mutant() {
            @Override
            public String getSpritePath() {
                return "test_mutant.png";
            }
        };
        
        // Create a player for testing
        Player player = new Player(new Position(0, 0));
        GameEngine engine = new GameEngine(10);
        
        // Initial player stats
        int initialHp = player.getHp();
        int initialScore = player.getScore();
        
        // Test attack method
        testMutant.attack(player, engine);
        assertEquals(initialHp - Mutant.DAMAGE, player.getHp(), "Mutant should deal " + Mutant.DAMAGE + " damage");
        
        // Test giveReward method
        testMutant.giveReward(player);
        assertEquals(initialScore + Mutant.POINTS, player.getScore(), "Mutant should give " + Mutant.POINTS + " points");
    }

    @Test
    public void testMeleeMutantCell() {
        // Create a melee mutant cell
        MeleeMutantCell mutantCell = new MeleeMutantCell();
        
        // Create a game engine and player
        GameEngine engine = new GameEngine(10);
        Player player = new Player(new Position(1, 1));
        player.setPosition(new Position(1, 1));
        
        // Initial player HP and score
        int initialHp = player.getHp();
        int initialScore = player.getScore();
        
        // Player enters the cell with the mutant
        mutantCell.onEnter(player, engine);
        
        // Verify player takes damage (2 points)
        assertEquals(initialHp - Mutant.DAMAGE, player.getHp(), "Player should take " + Mutant.DAMAGE + " damage");
        
        // Verify player gets points (2 points)
        assertEquals(initialScore + Mutant.POINTS, player.getScore(), "Player should gain " + Mutant.POINTS + " points");
    }
    
    @Test
    public void testMutantInGameEngine() {
        // Create a custom game engine for testing
        GameEngine engine = new GameEngine(10);
        
        // Place a melee mutant at a specific position
        Position mutantPos = new Position(3, 3);
        engine.replaceCell(mutantPos, new MeleeMutantCell());
        
        // Create a player at a position next to the mutant
        Player player = engine.getPlayer();
        player.setPosition(new Position(3, 2));
        
        // Reset player HP to full
        while (player.getHp() < 10) {
            player.modifyHp(1);
        }
        
        // Initial player HP and score
        int initialHp = player.getHp();
        int initialScore = player.getScore();
        
        // Move the player onto the mutant cell
        engine.movePlayer(Direction.RIGHT);
        
        // Verify player took damage
        assertEquals(initialHp - Mutant.DAMAGE, player.getHp(), 
            "Player should take " + Mutant.DAMAGE + " damage when entering a mutant cell");
        
        // Verify player got points
        assertEquals(initialScore + Mutant.POINTS, player.getScore(), 
            "Player should gain " + Mutant.POINTS + " points when defeating a mutant");
        
        // Verify the cell was replaced with an empty cell
        Cell cell = engine.getMap()[3][3];
        assertTrue(cell instanceof EmptyCell, "Mutant cell should be replaced with an empty cell");
    }
}
