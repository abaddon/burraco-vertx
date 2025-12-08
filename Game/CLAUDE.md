# Game Service Documentation

## Service Overview
- **Purpose**: Main game service with REST API and game state machine
- **Responsibilities**:
    - Game lifecycle management (create, start, terminate)
    - Game state machine (Draft → WaitingDealer → Execution → Terminated)
    - Player turn management
    - Card deck management
- **Aggregate**: GameAggregate
- **Event Store**: KurrentDB (stream: game-{gameId})

## REST API
**Port**: 8081

```
POST   /game                      - Create new game
POST   /game/{gameId}/start       - Request card dealing
GET    /health                    - Health check
```

## Game State Machine

### State Transitions
```
GameDraft → GameWaitingDealer → GameExecution → GameTerminated
```

#### **1. GameDraft**
- **Purpose**: Creating game and adding players
- **Allowed Commands**:
    - `CreateGame` - Initialize new game
    - `AddPlayer` - Add player to game (triggered by PlayerCreated event)
    - `RemovePlayer` - Remove player from game
    - `RequestDealCards` - Request dealer to deal cards
- **Transitions To**: GameWaitingDealer (when RequestDealCards is executed)
- **Business Rules**:
    - Minimum 2 players required
    - Maximum 4 players allowed

#### **2. GameWaitingDealer**
- **Purpose**: Waiting for cards to be dealt by Dealer service
- **Allowed Commands**:
    - `AddCardPlayer` - Add card to player's hand
    - `AddCardDeck` - Add card to main deck
    - `AddCardFirstPlayerDeck` - Add card to pozzetto 1
    - `AddCardSecondPlayerDeck` - Add card to pozzetto 2
    - `AddCardDiscardDeck` - Add card to discard pile
    - `StartPlayerTurn` - Transition to execution phase
- **Transitions To**: GameExecution (when all cards are dealt)
- **Business Rules**:
    - Each player must have 11 cards
    - Each pozzetto must have 11 cards
    - Main deck must have remaining cards
    - Discard pile must have 1 card

#### **3. GameExecution**
- **Purpose**: Active gameplay with player turns
- **Sub-phases**:
    - **PickUp Phase**: Player draws card(s)
        - `PickUpACardFromDeck` - Draw 1 card from deck
        - `PickUpCardsFromDiscardPile` - Take entire discard pile
    - **Play Phase**: Player makes moves
        - `DropTris` - Place a set (3+ same rank)
        - `DropStraight` - Place a sequence (3+ consecutive same suit)
        - `AppendCardsOnATris` - Add cards to existing set
        - `AppendCardsOnStraight` - Add cards to existing sequence
        - `PickUpPlayerDeckDuringTurn` - Take pozzetto when hand is empty
        - `DropCardOnDiscardPile` - Discard a card
    - **End Phase**: End turn or game
        - `StartNextPlayerTurn` - Next player's turn
        - `EndGame` - Terminate the game
- **Transitions To**: GameTerminated (when EndGame is executed)
- **Business Rules**:
    - Player must draw before playing
    - Player must discard to end turn
    - Cannot go out without team burraco

#### **4. GameTerminated**
- **Purpose**: Game completed
- **Allowed Commands**: None (terminal state)
- **Final State**: Game is over, scores are calculated

## Events

### Publishes (to `game-events`)
1. **GameCreated**
   - Payload: `{ aggregateId: GameIdentity }`
   - Consumed by: Player service (GameView projection)

2. **PlayerAdded**
   - Payload: `{ aggregateId: GameIdentity, playerIdentity: PlayerIdentity }`
   - Consumed by: Player service (GameView projection)

3. **CardsRequestedToDealer**
   - Payload: `{ aggregateId: GameIdentity, players: List<PlayerIdentity> }`
   - Consumed by: Dealer service (triggers card dealing)

4. **GameStarted**
   - Payload: `{ aggregateId: GameIdentity, playerTurn: PlayerIdentity, teams: List<List<PlayerIdentity>> }`
   - Consumed by: Player service (GameView projection)
   - Purpose: Signals game has transitioned from waiting to execution phase

### Consumes
1. **PlayerCreated** (from Player service, topic: `player-events`)
   - Action: Adds player to GameDraft aggregate

2. **CardDealtToPlayer** (from Dealer service, topic: `dealer-events`)
   - Action: Adds card to player's hand in game

3. **CardDealtToDeck** (from Dealer service, topic: `dealer-events`)
   - Action: Adds card to main deck

4. **CardDealtToPlayerDeck1** (from Dealer service, topic: `dealer-events`)
   - Action: Adds card to first pozzetto

5. **CardDealtToPlayerDeck2** (from Dealer service, topic: `dealer-events`)
   - Action: Adds card to second pozzetto

6. **CardDealtToDiscardDeck** (from Dealer service, topic: `dealer-events`)
   - Action: Adds card to discard pile

7. **DealingCompleted** (from Dealer service, topic: `dealer-events`)
   - Action: Triggers game transition from GameWaitingDealer to GameExecutionPickUpPhase
   - Executes: StartPlayerTurn command which calls startGame() on GameWaitingDealer aggregate
   - Result: Game moves from WAITING_DEALER status to STARTED status

## Build & Test

```bash
# Build
./gradlew :Game:build

# Create Docker image (Shadow JAR)
./gradlew :Game:shadowJar

# Run tests
./gradlew :Game:test
./gradlew :Game:test --tests "*.SpecificTest"
```
