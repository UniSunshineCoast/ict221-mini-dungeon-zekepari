# MiniDungeon Game - Technical Overview

## Table of Contents
1. [Game Overview](#game-overview)
2. [How to Play](#how-to-play)
3. [Technical Architecture](#technical-architecture)
4. [Core Systems](#core-systems)
5. [Gameplay Mechanics](#gameplay-mechanics)
6. [User Interface](#user-interface)
7. [Data Persistence](#data-persistence)
8. [Build & Development](#build--development)
9. [Testing Strategy](#testing-strategy)

---

## Game Overview

**MiniDungeon** is a 2D grid-based dungeon crawler game built in Java using JavaFX. Players navigate through procedurally generated 10×10 dungeon levels, collecting items, avoiding traps, fighting mutants, and progressing through levels to win the game.

### Core Game Loop
1. **Exploration**: Navigate a 10×10 grid using arrow keys or mouse clicks
2. **Resource Management**: Monitor HP (health), score, and step count
3. **Item Collection**: Gather gold for points and health potions for survival
4. **Combat**: Encounter and defeat mutant enemies
5. **Progression**: Find ladders to advance through 2 dungeon levels
6. **Victory/Defeat**: Win by completing level 2, lose by running out of HP or steps

---

## How to Play

### Starting the Game
- Launch with `./gradlew run` or run the `RunGame` class
- Player starts at position (0,0) - the entry point
- Initial stats: 10 HP, 0 Score, 0 Steps, Level 1

### Movement Controls
- **Keyboard**: Arrow keys (↑↓←→) or WASD keys
- **Mouse**: Click directional arrow buttons in the UI
- Each movement costs 1 step (maximum 100 steps before game over)

### Cell Types & Interactions

| Cell Type | Visual | Effect | Persistence |
|-----------|--------|--------|-------------|
| **Empty** | Gray square | Safe passage | Permanent |
| **Wall** | Dark barrier | Blocks movement | Permanent |
| **Entry** | Starting position | Safe spawn point | Permanent |
| **Gold** | Yellow coin | +2 Score | Consumed |
| **Health Potion** | Red bottle | +4 HP (max 10) | Consumed |
| **Trap** | Spikes | -2 HP | Permanent |
| **Melee Mutant** | Enemy sprite | -2 HP, +2 Score | Defeated |
| **Ladder** | Staircase | Next level | One-time use |

### Win/Loss Conditions
- **Victory**: Reach and use the ladder on Level 2
- **Defeat**: HP drops to 0 or below, OR exceed 100 steps
- **Game Over**: Modal dialog offers restart or scoreboard entry

### Difficulty System
- Affects number of enemies spawned in each level
- Difficulty 1: 3 Melee Mutants
- Difficulty 2: 3 Melee Mutants + 2 additional enemies
- Higher difficulty = more dangerous but potentially higher scores

---

## Technical Architecture

### Package Structure
```
src/main/java/
├── dungeon/
│   ├── engine/           # Core game logic
│   │   ├── cells/        # Cell type implementations
│   │   └── persistence/  # Save/load functionality
│   └── gui/              # JavaFX user interface
└── src/main/resources/
    └── dungeon/gui/      # FXML layouts

src/test/java/            # Comprehensive test suite
```

### Design Patterns Used
- **Strategy Pattern**: `Cell` interface with varied `onEnter()` behaviors
- **Factory Pattern**: `GameMap` procedural generation
- **Observer Pattern**: UI updates responding to game state changes
- **Command Pattern**: Movement handling through `Direction` enum
- **Serialization Pattern**: Game state persistence via `SaveState`

---

## Core Systems

### 1. Position & Coordinate System
**Class**: `dungeon.engine.Position`
- **Immutable** coordinate representation (row, col)
- **Bounds**: 0-9 for both dimensions (10×10 grid)
- **Operations**: `plus(Direction)` for movement calculation
- **Validation**: Automatic bounds checking, returns null for invalid positions

```java
Position current = new Position(5, 5);
Position newPos = current.plus(Direction.UP); // (4, 5)
```

### 2. Direction System
**Enum**: `dungeon.engine.Direction`
- **Values**: UP(-1,0), DOWN(1,0), LEFT(0,-1), RIGHT(0,1)
- **Input Mapping**: Converts JavaFX KeyCode to Direction
- **Support**: Arrow keys + WASD keys for accessibility

### 3. Cell System Architecture
**Interface**: `dungeon.engine.cells.Cell`
- **Core Methods**: 
  - `onEnter(Player, GameEngine)`: Triggered when player enters
  - `spritePath()`: Returns image path for rendering
- **Inheritance**: `AbstractItemCell` base class for consumable items

**Cell Implementations**:
- **`EmptyCell`**: Basic traversable space
- **`WallCell`**: Impassable barrier  
- **`EntryCell`**: Player spawn point
- **`GoldCell`**: +2 score, disappears after collection
- **`HealthPotionCell`**: +4 HP (capped at 10), disappears
- **`TrapCell`**: -2 HP, remains dangerous
- **`LadderCell`**: Level progression trigger
- **`MeleeMutantCell`**: Enemy encounter (-2 HP, +2 score, disappears)

### 4. Player System
**Class**: `dungeon.engine.Player`
- **State Management**: HP (0-10), Score, Steps, Level, Position
- **HP System**: Automatic clamping to 0-10 range
- **Movement**: Position updates with step counting
- **Persistence**: Serializable for save/load functionality

**Key Methods**:
- `takeDamage(int)`: Reduces HP with bounds checking
- `heal(int)`: Increases HP with maximum cap
- `incrementSteps()`: Tracks movement for loss condition
- `modifyHp(int)`: General HP modification with clamping

### 5. Game Engine
**Class**: `dungeon.engine.GameEngine`
- **Central Controller**: Orchestrates all game systems
- **Game Loop**: Handles movement, cell interactions, win/loss detection
- **Level Management**: Generates new maps, tracks progression
- **State Checking**: Monitors HP, steps, and level completion

**Core Workflow**:
1. `move(Direction)` → validates movement
2. `movePlayer()` → updates position, triggers cell effects
3. `checkGameOverConditions()` → evaluates win/loss states
4. `advanceToNextLevel()` → handles level progression

---

## Gameplay Mechanics

### Map Generation Algorithm
**Class**: `dungeon.engine.GameMap`
- **Size**: Fixed 10×10 grid
- **Procedural**: Uses seeded Random for reproducibility
- **Cell Distribution**:
  - 1 Entry (0,0), 1 Ladder (random position)
  - 5 Gold pieces, 5 Trap cells, 2 Health Potions
  - 3 Melee Mutants + difficulty-based additional enemies
  - Remaining cells filled as Empty or Wall

### Combat System
- **Automatic**: Triggered by entering mutant cells
- **Damage**: All mutants deal 2 HP damage
- **Rewards**: Defeating mutants grants 2 score points
- **Resolution**: Mutants disappear after being defeated

### Level Progression
- **Level 1 → 2**: Find and enter the ladder
- **Level 2 → Victory**: Find and enter the second ladder
- **Map Regeneration**: New procedural layout for each level
- **Player Persistence**: HP, score, and steps carry over

### Resource Management
- **HP**: Starts at 10, consumed by traps and mutants, restored by potions
- **Steps**: Limited to 100 total across all levels
- **Score**: Accumulated through gold collection and mutant defeats

---

## User Interface

### Main Game Window
**Layout**: JavaFX BorderPane with organized sections
- **Top**: Status bar with HP, Score, Steps, Level, and Seed display
- **Center**: 10×10 GridPane representing the dungeon
- **Controls**: Arrow buttons for mouse-based movement
- **Menu**: New Game, Save/Load, Scoreboard access buttons

### Visual Representation
**Class**: `dungeon.gui.CellView`
- **Rendering**: StackPane combining cell sprite + player indicator
- **Player Marker**: Visual overlay showing current position
- **Sprite System**: Dynamic image loading based on `Cell.spritePath()`
- **Grid Layout**: Automatic arrangement in game GridPane

### Input Handling
**Class**: `dungeon.gui.Controller`
- **Keyboard**: Global key event capture for arrow/WASD movement
- **Mouse**: Button click handlers for directional movement
- **Focus Management**: Ensures consistent input reception
- **Game State**: Disables input during game over conditions

### User Experience Features
- **Real-time Updates**: Immediate visual feedback for all actions
- **Game Over Dialogs**: Modal alerts for win/loss with restart options
- **Scoreboard Integration**: Automatic high score submission for victories
- **Responsive Design**: Scalable UI adapting to window resizing

---

## Data Persistence

### Save System
**Class**: `dungeon.engine.persistence.SaveState`
- **Serialization**: Java Object Serialization for complete game state
- **Components**: GameEngine state, Player data, Map configuration, RNG seed
- **File Format**: Binary .save files via FileChooser dialogs
- **Restoration**: Complete game state reconstruction from saved data

### Scoreboard System
**Class**: `dungeon.engine.persistence.ScoreBoard`
- **Storage**: JSON format in user home directory (`~/.minidungeon.scores.json`)
- **JSON Library**: Jackson for serialization/deserialization
- **Capacity**: Maintains top 5 scores automatically
- **Sorting**: Descending order by score, then by level achieved
- **Thread Safety**: Synchronized access for concurrent score updates

**Score Entry Structure**:
```json
{
  "playerName": "Player1",
  "score": 150,
  "level": 2,
  "timestamp": "2025-05-27T10:30:00"
}
```

---

## Build & Development

### Project Configuration
- **Build Tool**: Gradle with Kotlin DSL
- **Java Version**: 21 (LTS)
- **JavaFX**: Integrated via JavaFX Gradle plugin
- **Dependencies**: Jackson for JSON, JUnit 5 for testing

### Development Commands
```bash
# Build and test
./gradlew build

# Run application  
./gradlew run

# Execute tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport

# Verify coverage threshold
./gradlew jacocoTestCoverageVerification

# Clean build
./gradlew clean
```

### IDE Integration
- **IntelliJ IDEA**: Full support with run configurations
- **Entry Point**: `dungeon.gui.RunGame.main()` (avoids JavaFX module issues)
- **Resources**: FXML files in `src/main/resources/dungeon/gui/`
- **Testing**: JUnit 5 integration with automatic discovery

---

## Testing Strategy

### Coverage Goals
- **Target**: ≥85% line coverage via JaCoCo
- **Exclusions**: GUI package (`dungeon.gui.*`) excluded from coverage analysis
- **Focus**: Core engine and persistence systems comprehensively tested

### Test Structure
**Test Classes**:
- `TestGameEngine`: Core game logic and integration scenarios
- `TestPlayer`: Player state management and HP/score mechanics  
- `TestDirection`: Input mapping and coordinate calculations
- `TestPosition`: Bounds checking and movement validation
- `TestGameMap`: Procedural generation and reproducibility
- `TestSaveState`: Serialization round-trip verification
- `TestScoreBoard`: JSON persistence and ranking logic
- `TestAdditionalCellCoverage`: Cell behavior edge cases
- `TestCoverageGaps`: Boundary conditions and error paths
- `TestEdgeCaseCoverage`: Game over scenarios and state transitions

### Testing Approaches
- **Unit Tests**: Individual component isolation and validation
- **Integration Tests**: Multi-system interaction verification
- **Property Tests**: Boundary value analysis and edge cases
- **Regression Tests**: Known bug prevention and fix validation
- **Reproducibility Tests**: Seeded random generation consistency

### Quality Metrics
- **105+ Test Cases**: Comprehensive scenario coverage
- **Zero Test Failures**: Continuous integration ready
- **85%+ Coverage**: Exceeds industry standard thresholds
- **Fast Execution**: Complete test suite runs in seconds

---

## Key Features Summary

### Implemented Features ✅
- ✅ Complete 2-level dungeon progression system
- ✅ Procedural map generation with reproducible seeds
- ✅ Full cell type system (8 different cell behaviors)
- ✅ Player movement with keyboard and mouse support
- ✅ HP/Score/Steps resource management with bounds checking
- ✅ Combat system with mutant enemies
- ✅ Save/Load game functionality with file choosers
- ✅ JSON-based persistent scoreboard (top 5 tracking)
- ✅ Comprehensive win/loss condition handling
- ✅ JavaFX GUI with real-time updates and responsive design
- ✅ 85%+ test coverage with robust test suite
- ✅ Gradle build system with full CI/CD readiness

### Architecture Strengths
- **Modular Design**: Clear separation between engine and GUI
- **Extensible**: Easy to add new cell types, difficulty modes, or features
- **Testable**: Comprehensive unit and integration test coverage
- **Maintainable**: Clean code structure with documented interfaces
- **Performant**: Efficient algorithms with minimal memory footprint
- **User-Friendly**: Intuitive controls and clear visual feedback

This MiniDungeon implementation represents a complete, production-ready game with professional software development practices, comprehensive testing, and a polished user experience.
