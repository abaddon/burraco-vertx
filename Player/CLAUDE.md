# Player Service Documentation

## Service Overview
- **Purpose**: Player service managing player behavior and read projections
- **Responsibilities**:
    - Player lifecycle management (create, delete)
    - Player-specific data (cards in hand)
    - Maintains GameView projection for validation
    - Maintains PlayerView projection for queries
- **Aggregate**: PlayerAggregate
- **Event Store**: KurrentDB (stream: player-{playerId})

## REST API
**Port**: 8082

```
POST   /player                               - Create new player
GET    /player/{playerId}/game/{gameId}     - Get player view (cards in hand)
DELETE /player/{playerId}                    - Delete player
GET    /health                               - Health check
```

## Projections (CQRS)

### 1. GameView Projection
- **Purpose**: Maintains denormalized view of games for validation
- **Storage**: InMemoryGameViewRepository
- **Data**: `gameId`, `state`, `players`, `maxPlayers`
- **Updated by**:
    - `GameCreated` (from Game) → Creates new GameView
    - `PlayerAdded` (from Game) → Adds player to GameView
    - `GameStarted` (from Game) → Updates state to PLAYING

### 2. PlayerView Projection
- **Purpose**: Maintains denormalized view of player's cards and turn status for queries
- **Storage**: InMemoryPlayerViewRepository
- **Data**: `playerId`, `gameId`, `cards`, `isMyTurn`, `teamMateId`
- **Updated by**: EventStore subscription to `player-{playerId}` stream
- **New Fields**:
    - `isMyTurn`: Boolean flag indicating if it's this player's turn
    - `teamMateId`: PlayerIdentity of the player's teammate (nullable)

## Events

### Domain Events (Published to `player-events`)
1. **PlayerCreated**
   - Payload: `{ aggregateId: PlayerIdentity, gameIdentity: GameIdentity, user: String }`
   - Consumed by: Game service (adds player to game)
   - State Transition: N/A → PlayerDraft

2. **PlayerActivated**
   - Payload: `{ aggregateId: PlayerIdentity, gameIdentity: GameIdentity, teamMateId: PlayerIdentity? }`
   - Consumed by: Internal (projection updates)
   - State Transition: PlayerDraft → PlayerActive
   - Triggered when: Game starts and it's this player's turn

3. **PlayerWaitingTurn**
   - Payload: `{ aggregateId: PlayerIdentity, gameIdentity: GameIdentity, teamMateId: PlayerIdentity? }`
   - Consumed by: Internal (projection updates)
   - State Transition: PlayerDraft → PlayerWaiting
   - Triggered when: Game starts but it's NOT this player's turn

### Consumes (Command Processing)
1. **CardDealtToPlayer** (from Dealer service, topic: `dealer-events`)
   - Action: Adds card to PlayerAggregate (PlayerDraft only)

2. **GameStarted** (from Game service, topic: `game-events`)
   - Action: Activates players based on turn
   - Behavior:
     - Player whose turn it is → ActivatePlayer command → PlayerActive
     - All other players → SetPlayerWaiting command → PlayerWaiting
     - Extracts and assigns teammates from teams structure
   - Fail Fast: Throws exception if player doesn't exist

### Consumes (Projection Updates)
1. **GameCreated** (from Game service, topic: `game-events`)
   - Action: Creates GameView projection entry

2. **PlayerAdded** (from Game service, topic: `game-events`)
   - Action: Updates GameView projection (adds player to game)

3. **GameStarted** (from Game service, topic: `game-events`)
   - Action: Updates GameView state to PLAYING

## Player States

The Player aggregate follows a state machine pattern with distinct state classes:

### 1. PlayerDraft
- **Initial state** when player is created
- **Capabilities**:
  - Can collect cards from dealer (during initial deal)
  - Can transition to PlayerActive or PlayerWaiting
- **Transitions**:
  - `activatePlayer(teamMateId)` → PlayerActive
  - `setWaiting(teamMateId)` → PlayerWaiting

### 2. PlayerActive
- **State when it's player's turn**
- **Capabilities**:
  - Cannot collect cards (per design decision - only draft state can)
  - Future: Will implement turn actions (draw, meld, discard)
- **Transitions**:
  - Future: PlayerActive → PlayerWaiting (via PlayerTurnEnded event - not yet implemented)

### 3. PlayerWaiting
- **State when waiting for turn**
- **Capabilities**:
  - Cannot collect cards (per design decision - only draft state can)
  - Can only observe game state
- **Transitions**:
  - Future: PlayerWaiting → PlayerActive (via PlayerTurnStarted event - not yet implemented)

## Commands

### PlayerDraft Commands
1. **CreatePlayer**
   - Creates new player in draft state
   - Required: playerIdentity, gameIdentity, user

2. **AddCardToPlayer**
   - Adds card to player's hand (only in draft state)
   - Required: playerIdentity, gameIdentity, card
   - Triggered by: CardDealtToPlayer event from Dealer

3. **ActivatePlayer**
   - Transitions player from draft to active (turn started)
   - Required: playerIdentity, gameIdentity, teamMateId (optional)
   - Triggered by: GameStarted event from Game

4. **SetPlayerWaiting**
   - Transitions player from draft to waiting (not player's turn)
   - Required: playerIdentity, gameIdentity, teamMateId (optional)
   - Triggered by: GameStarted event from Game

5. **DeletePlayer**
   - Removes player from game
   - Required: playerIdentity

## Kafka Consumer Verticles

### 1. KafkaDealerConsumerVerticle
- **Topic**: `dealer-events`
- **Consumer Group**: `player-command-dealer`
- **Handlers**:
  - AddCardToPlayerHandlerKafka (CardDealtToPlayer)

### 2. KafkaGameConsumerVerticle
- **Topic**: `game-events`
- **Consumer Group**: `player-command-game`
- **Purpose**: Command processing for game events
- **Handlers**:
  - GameStartedHandlerKafka (GameStarted)
- **Behavior**: Activates/deactivates players based on turn

### 3. GameViewProjectionKafkaVerticle
- **Topic**: `game-events`
- **Consumer Group**: `player-projection-game` (⚠️ CRITICAL: Different from KafkaGameConsumerVerticle)
- **Purpose**: Projection updates for read models
- **Handlers**:
  - GameCreatedProjectionKafkaEventHandler
  - PlayerAddedProjectionKafkaEventHandler
  - GameStartedProjectionKafkaEventHandler
- **Why Separate Consumer Group**: Using the same group as KafkaGameConsumerVerticle would cause Kafka to load-balance messages between them. Each message would go to ONLY ONE consumer, breaking projection updates. With separate groups, BOTH consumers receive ALL messages.

## Build & Test

```bash
# Build
./gradlew :Player:build

# Create Docker image (Shadow JAR)
./gradlew :Player:shadowJar

# Run tests
./gradlew :Player:test

# Run specific test
./gradlew :Player:test --tests "PlayerActivationTest"
```
