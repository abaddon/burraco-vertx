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

### 2. PlayerView Projection
- **Purpose**: Maintains denormalized view of player's cards for queries
- **Storage**: InMemoryPlayerViewRepository
- **Data**: `playerId`, `gameId`, `cards`
- **Updated by**: EventStore subscription to `player-{playerId}` stream

## Events

### Publishes (to `player-events`)
1. **PlayerCreated**
   - Payload: `{ aggregateId: PlayerIdentity, gameIdentity: GameIdentity }`
   - Consumed by: Game service (adds player to game)

### Consumes
1. **CardDealtToPlayer** (from Dealer service, topic: `dealer-events`)
   - Action: Adds card to PlayerAggregate

2. **GameCreated** (from Game service, topic: `game-events`)
   - Action: Creates GameView projection entry

3. **PlayerAdded** (from Game service, topic: `game-events`)
   - Action: Updates GameView projection (adds player to game)

## Build & Test

```bash
# Build
./gradlew :Player:build

# Create Docker image (Shadow JAR)
./gradlew :Player:shadowJar

# Run tests
./gradlew :Player:test
```
