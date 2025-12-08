# Dealer Service Documentation

## Service Overview
- **Purpose**: Card dealing service - purely event-driven (no REST API)
- **Responsibilities**:
    - Deals cards according to Burraco rules
    - 11 cards to each player
    - 11 cards to pozzetto 1
    - 11 cards to pozzetto 2
    - Remaining cards to deck
    - 1 card to discard pile
- **Aggregate**: DealerAggregate
- **Event Store**: KurrentDB (stream: dealer-{gameId})

## REST API
None (Event-driven only)

## Card Dealing Algorithm
1. **For each player (11 cards)**: Draw card, Execute `DealCardToPlayer`, Publish `CardDealtToPlayer`
2. **For pozzetto 1 (11 cards)**: Draw card, Execute `DealCardToPlayerDeck1`, Publish `CardDealtToPlayerDeck1`
3. **For pozzetto 2 (11 cards)**: Draw card, Execute `DealCardToPlayerDeck2`, Publish `CardDealtToPlayerDeck2`
4. **For main deck (remaining cards)**: Draw card, Execute `DealCardToDeck`, Publish `CardDealtToDeck`
5. **For discard pile (1 card)**: Draw card, Execute `DealCardToDiscardDeck`, Publish `CardDealtToDiscardDeck`
6. **Complete dealing**: Execute `CompleteDealingCards`, Publish `DealingCompleted`

## Events

### Publishes (to `dealer-events`)
1. **CardDealtToPlayer**
   - Payload: `{ aggregateId: DealerId, gameId: GameIdentity, playerId: PlayerIdentity, card: String }`
   - Consumed by: Game service, Player service

2. **CardDealtToDeck**
   - Payload: `{ aggregateId: DealerId, gameId: GameIdentity, card: String }`
   - Consumed by: Game service

3. **CardDealtToPlayerDeck1**
   - Payload: `{ aggregateId: DealerId, gameId: GameIdentity, card: String }`
   - Consumed by: Game service

4. **CardDealtToPlayerDeck2**
   - Payload: `{ aggregateId: DealerId, gameId: GameIdentity, card: String }`
   - Consumed by: Game service

5. **CardDealtToDiscardDeck**
   - Payload: `{ aggregateId: DealerId, gameId: GameIdentity, card: String }`
   - Consumed by: Game service

6. **DealingCompleted**
   - Payload: `{ aggregateId: DealerId, gameId: GameIdentity }`
   - Consumed by: Game service
   - Purpose: Signals completion of card dealing phase, triggers game to transition to execution phase

### Consumes
1. **CardsRequestedToDealer** (from Game service, topic: `game-events`)
   - Action: Creates DealerAggregate and deals all cards, then publishes DealingCompleted

## Build & Test

```bash
# Build
./gradlew :Dealer:build

# Create Docker image (Shadow JAR)
./gradlew :Dealer:shadowJar

# Run tests
./gradlew :Dealer:test
```
