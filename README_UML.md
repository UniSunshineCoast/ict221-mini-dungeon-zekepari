# MiniDungeon UML Documentation - ICT221 Task 2

## Class Diagram Specification

This document provides complete UML class diagram specifications for the MiniDungeon game engine and GUI façade, highlighting key design patterns for ICT221 Task 2 assessment.

---

## Package Structure

```
dungeon.engine
├── GameEngine
├── Player
├── Position
├── Direction (enum)
├── ActionLogger (interface)
├── ConsoleActionLogger
└── GameMap

dungeon.engine.cells
├── Cell (interface) - **Strategy Pattern**
├── AbstractItemCell (abstract)
├── EmptyCell
├── WallCell
├── EntryCell
├── GoldCell
├── HealthPotionCell
├── TrapCell
├── LadderCell
├── MeleeMutantCell
└── RangedMutantCell

dungeon.engine.persistence
├── SaveState
└── ScoreBoard

dungeon.gui (High-level façade)
├── Controller - **Observer Pattern**
├── GuiActionLogger
├── CellView
└── RunGame
```

---

## Core Engine Classes

### 1. GameEngine (Central Controller)

```java
class GameEngine {
    // Attributes
    - gameMap: GameMap
    - player: Player
    - difficulty: int
    - gameOver: boolean
    - statusMessage: String
    - rng: Random
    - actionLogger: ActionLogger

    // Constructors
    + GameEngine(difficulty: int)
    + GameEngine(difficulty: int, seed: long)
    + GameEngine(difficulty: int, rng: Random)

    // Core Game Loop Methods
    + move(direction: Direction): boolean
    - movePlayer(newPosition: Position): void
    - checkGameOverConditions(): void
    - advanceToNextLevel(): void
    + processRangedMutantTurns(): void

    // Action Logging
    + setActionLogger(logger: ActionLogger): void
    + logAction(message: String): void

    // State Access
    + getPlayer(): Player
    + getGameMap(): GameMap
    + getDifficulty(): int
    + isGameOver(): boolean
    + getStatusMessage(): String

    // Factory Method Pattern
    + static newGame(difficulty: int): GameEngine

    // Console Interface
    + static main(args: String[]): void
}
```

**Relationships:**
- Aggregates `GameMap`, `Player`, `ActionLogger`
- Uses `Direction` enum
- Creates cells through `GameMap` (Factory Pattern)

### 2. Player (State Management)

```java
class Player implements Serializable {
    // Attributes
    - hp: int
    - score: int
    - steps: int
    - level: int
    - position: Position

    // Constructor
    + Player()

    // HP Management
    + getHp(): int
    + setHp(hp: int): void
    + takeDamage(damage: int): void
    + heal(amount: int): void
    + modifyHp(change: int): void

    // Score and Steps
    + getScore(): int
    + addScore(points: int): void
    + getSteps(): int
    + incrementSteps(): void

    // Level and Position
    + getLevel(): int
    + setLevel(level: int): void
    + getPosition(): Position
    + setPosition(position: Position): void
}
```

**Relationships:**
- Aggregates `Position`
- Used by `GameEngine`

### 3. Position (Immutable Coordinate)

```java
class Position implements Serializable {
    // Attributes
    - row: int
    - col: int

    // Constructor
    + Position(row: int, col: int)

    // Accessors
    + getRow(): int
    + getCol(): int

    // Movement Calculation
    + plus(direction: Direction): Position

    // Validation
    + static isValid(row: int, col: int): boolean

    // Object Methods
    + equals(obj: Object): boolean
    + hashCode(): int
    + toString(): String
}
```

**Relationships:**
- Uses `Direction` enum
- Used by `Player`, `GameEngine`, all cell classes

### 4. Direction (Enum)

```java
enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    // Attributes
    - deltaRow: int
    - deltaCol: int

    // Constructor
    - Direction(deltaRow: int, deltaCol: int)

    // Accessors
    + getDeltaRow(): int
    + getDeltaCol(): int

    // Input Mapping
    + static fromKeyCode(keyCode: KeyCode): Direction
}
```

**Relationships:**
- Used by `Position`, `GameEngine`, GUI controllers

### 5. GameMap (Factory Pattern)

```java
class GameMap implements Serializable {
    // Constants
    + static final SIZE: int = 10

    // Attributes
    - cells: Cell[][]
    - difficulty: int
    - rng: Random

    // Constructor
    + GameMap(difficulty: int, rng: Random)

    // Cell Access
    + getCell(position: Position): Cell
    + getCell(row: int, col: int): Cell
    + setCell(position: Position, cell: Cell): void

    // **Factory Pattern Methods**
    - generateMap(): void
    - placeRandomCells(count: int, cellFactory: Supplier<Cell>): void

    // Utility
    + toString(): String
}
```

**Relationships:**
- **Factory Pattern**: Creates different cell types using `Supplier<Cell>`
- Aggregates `Cell[][]` array
- Uses `Random` for procedural generation

---

## Action Logging System (Strategy Pattern)

### 6. ActionLogger (Strategy Interface)

```java
interface ActionLogger {
    + logAction(message: String): void
    + clear(): void
}
```

### 7. ConsoleActionLogger (Concrete Strategy)

```java
class ConsoleActionLogger implements ActionLogger {
    + logAction(message: String): void
    + clear(): void
}
```

### 8. GuiActionLogger (Concrete Strategy)

```java
class GuiActionLogger implements ActionLogger {
    // Attributes
    - textArea: TextArea

    // Constructor
    + GuiActionLogger(textArea: TextArea)

    // Strategy Implementation
    + logAction(message: String): void
    + clear(): void
}
```

**Pattern Implementation:**
- **Strategy Pattern**: `ActionLogger` interface with multiple implementations
- `GameEngine` uses `ActionLogger` strategy without knowing concrete type

---

## Cell System (Strategy Pattern)

### 9. Cell (Strategy Interface)

```java
interface Cell extends Serializable {
    // **Strategy Pattern Method**
    + onEnter(player: Player, engine: GameEngine): void
    + spritePath(): String
}
```

### 10. AbstractItemCell (Abstract Strategy)

```java
abstract class AbstractItemCell implements Cell {
    // Template Method Pattern
    + final onEnter(player: Player, engine: GameEngine): void
    + abstract onPickup(player: Player, engine: GameEngine): void
    + abstract getItemName(): String
}
```

### 11. Concrete Cell Implementations

#### EmptyCell
```java
class EmptyCell implements Cell {
    + onEnter(player: Player, engine: GameEngine): void
    + spritePath(): String
}
```

#### WallCell
```java
class WallCell implements Cell {
    + onEnter(player: Player, engine: GameEngine): void
    + spritePath(): String
}
```

#### EntryCell
```java
class EntryCell implements Cell {
    + onEnter(player: Player, engine: GameEngine): void
    + spritePath(): String
}
```

#### GoldCell (Strategy Implementation)
```java
class GoldCell extends AbstractItemCell {
    // Attributes
    - points: int

    // Constructor
    + GoldCell()

    // Strategy Implementation
    + onPickup(player: Player, engine: GameEngine): void
    + getItemName(): String
    + spritePath(): String
}
```

#### HealthPotionCell (Strategy Implementation)
```java
class HealthPotionCell extends AbstractItemCell {
    // Attributes
    - healAmount: int

    // Constructor
    + HealthPotionCell()

    // Strategy Implementation
    + onPickup(player: Player, engine: GameEngine): void
    + getItemName(): String
    + spritePath(): String
}
```

#### TrapCell (Strategy Implementation)
```java
class TrapCell implements Cell {
    // Attributes
    - damage: int

    // Constructor
    + TrapCell()

    // Strategy Implementation
    + onEnter(player: Player, engine: GameEngine): void
    + spritePath(): String
}
```

#### LadderCell (Strategy Implementation)
```java
class LadderCell implements Cell {
    + onEnter(player: Player, engine: GameEngine): void
    + spritePath(): String
}
```

#### MeleeMutantCell (Strategy Implementation)
```java
class MeleeMutantCell implements Cell {
    // Nested Class
    + static class MeleeMutant implements Mutant {
        - damage: int
        - points: int
        + getDamage(): int
        + getPoints(): int
    }

    // Attributes
    - mutant: MeleeMutant

    // Constructor
    + MeleeMutantCell()

    // Strategy Implementation
    + onEnter(player: Player, engine: GameEngine): void
    + spritePath(): String
}
```

#### RangedMutantCell (Advanced Strategy Implementation)
```java
class RangedMutantCell implements Cell {
    // Nested Class
    + static class RangedMutant implements Mutant {
        - damage: int
        - points: int
        - range: int
        - hitChance: double
        
        + getDamage(): int
        + getPoints(): int
        + getRange(): int
        + getHitChance(): double
    }

    // Attributes
    - mutant: RangedMutant
    - rng: Random

    // Constructor
    + RangedMutantCell()

    // Strategy Implementation
    + onEnter(player: Player, engine: GameEngine): void
    + spritePath(): String

    // Advanced Behavior
    + processTurn(playerPosition: Position, gameMap: GameMap, engine: GameEngine): void
    - hasLineOfSight(from: Position, to: Position, gameMap: GameMap): boolean
    - isInRange(from: Position, to: Position): boolean
}
```

**Pattern Implementation:**
- **Strategy Pattern**: `Cell` interface with varied `onEnter()` behaviors
- Each cell type implements different interaction strategies
- `GameEngine` treats all cells uniformly through interface

---

## Persistence Classes

### 12. SaveState (Serialization Pattern)

```java
class SaveState implements Serializable {
    // Nested Classes
    + static class PlayerState implements Serializable {
        + hp: int
        + score: int
        + steps: int
        + level: int
        + position: Position
    }

    + static class CellState implements Serializable {
        + className: String
        + position: Position
    }

    // Attributes
    - playerState: PlayerState
    - cellStates: List<CellState>
    - difficulty: int
    - gameOver: boolean
    - statusMessage: String
    - seed: long

    // Constructor
    + SaveState(engine: GameEngine)

    // Restoration
    + restoreGameEngine(): GameEngine

    // File Operations
    + static saveGame(engine: GameEngine, file: File): void
    + static loadGame(file: File): GameEngine
}
```

### 13. ScoreBoard (JSON Persistence)

```java
class ScoreBoard {
    // Nested Class
    + static class ScoreEntry {
        + playerName: String
        + score: int
        + level: int
        + timestamp: LocalDateTime
        
        // Constructors and getters/setters
    }

    // Attributes
    - scores: List<ScoreEntry>

    // Constructor
    + ScoreBoard()

    // Score Management
    + addScore(playerName: String, score: int, level: int): boolean
    + getTopScores(): List<ScoreEntry>

    // Persistence
    - loadScores(): void
    - saveScores(): void
}
```

---

## GUI Façade (Observer Pattern)

### 14. Controller (Observer Pattern)

```java
class Controller implements Initializable {
    // FXML Components
    @FXML - gridPane: GridPane
    @FXML - hpLabel: Label
    @FXML - scoreLabel: Label
    @FXML - stepsLabel: Label
    @FXML - levelLabel: Label
    @FXML - seedLabel: Label
    @FXML - actionLogArea: TextArea

    // Game State
    - engine: GameEngine
    - cellViews: CellView[][]

    // **Observer Pattern Implementation**
    + initialize(): void
    - updateGui(): void          // Observes game state changes
    - updateLabels(): void       // Observes player state changes

    // Event Handlers
    + handleKeyPressed(event: KeyEvent): void
    + newGame(): void
    + saveGame(): void
    + loadGame(): void
    + showScoreboard(): void

    // Movement
    - handleMove(direction: Direction): void

    // Game Over Handling
    - showGameOverAlert(): void
}
```

### 15. CellView (View Component)

```java
class CellView extends StackPane {
    // Attributes
    - cell: Cell
    - imageView: ImageView
    - playerIndicator: Circle

    // Constructor
    + CellView(cell: Cell)

    // Update Methods
    + updateCell(cell: Cell): void
    + setPlayerPresent(present: boolean): void

    // Utility
    - loadCellImage(spritePath: String): Image
}
```

### 16. RunGame (Application Entry Point)

```java
class RunGame extends Application {
    + start(primaryStage: Stage): void
    + static main(args: String[]): void
}
```

**Pattern Implementation:**
- **Observer Pattern**: `Controller` observes `GameEngine` state changes
- GUI updates automatically when game state changes
- Separation of concerns between game logic and presentation

---

## Key Design Patterns Summary

### 1. Strategy Pattern
- **Interface**: `Cell`
- **Concrete Strategies**: All cell implementations
- **Context**: `GameEngine` uses cells through interface
- **Benefit**: Easy to add new cell types without modifying existing code

### 2. Factory Pattern
- **Factory**: `GameMap`
- **Products**: Various `Cell` types
- **Creation Method**: `placeRandomCells(count, cellFactory)`
- **Benefit**: Centralized cell creation with procedural generation

### 3. Observer Pattern
- **Subject**: `GameEngine`
- **Observer**: `Controller`
- **Notification**: Through method calls (`updateGui()`, `updateLabels()`)
- **Benefit**: GUI automatically reflects game state changes

### 4. Template Method Pattern
- **Abstract Class**: `AbstractItemCell`
- **Template Method**: `onEnter()`
- **Hook Methods**: `onPickup()`, `getItemName()`
- **Benefit**: Common item behavior with customizable specifics

---

## Class Relationships Matrix

| Class | Inherits From | Implements | Aggregates | Uses |
|-------|---------------|------------|------------|------|
| GameEngine | - | - | GameMap, Player, ActionLogger | Direction, Position |
| Player | - | Serializable | Position | - |
| Position | - | Serializable | - | Direction |
| GameMap | - | Serializable | Cell[][] | Random |
| ConsoleActionLogger | - | ActionLogger | - | - |
| GuiActionLogger | - | ActionLogger | TextArea | Platform |
| EmptyCell | - | Cell | - | - |
| WallCell | - | Cell | - | - |
| GoldCell | AbstractItemCell | - | - | - |
| HealthPotionCell | AbstractItemCell | - | - | - |
| TrapCell | - | Cell | - | - |
| LadderCell | - | Cell | - | - |
| MeleeMutantCell | - | Cell | MeleeMutant | - |
| RangedMutantCell | - | Cell | RangedMutant, Random | Position |
| AbstractItemCell | - | Cell | - | - |
| SaveState | - | Serializable | PlayerState, List<CellState> | - |
| ScoreBoard | - | - | List<ScoreEntry> | ObjectMapper |
| Controller | - | Initializable | GameEngine, CellView[][] | Direction |
| CellView | StackPane | - | Cell, ImageView, Circle | Image |
| RunGame | Application | - | - | Stage, Scene |

---

## Multiplicity and Navigation

### GameEngine Associations:
- GameEngine "1" ←→ "1" GameMap
- GameEngine "1" ←→ "1" Player  
- GameEngine "1" ←→ "0..1" ActionLogger

### GameMap Associations:
- GameMap "1" ←→ "100" Cell (10x10 grid)

### Cell Strategy Pattern:
- Cell "1" ←implements→ "*" {EmptyCell, WallCell, GoldCell, etc.}

### GUI Observer Pattern:
- Controller "1" ←observes→ "1" GameEngine
- Controller "1" ←→ "100" CellView

### Persistence:
- SaveState "1" ←→ "1" PlayerState
- SaveState "1" ←→ "*" CellState
- ScoreBoard "1" ←→ "*" ScoreEntry

This UML specification provides complete class information needed to create a comprehensive class diagram showing all engine classes, the GUI façade, and the three key design patterns (Strategy, Factory, Observer) for ICT221 Task 2 assessment.
