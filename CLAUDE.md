# Comprehensive Summary & Service Interactions

**Last Updated**: 2025-10-27

---

## Table of Contents
- [Project Overview](#project-overview)
- [Core Architecture](#core-architecture)
- [Development Guidelines](#development-guidelines)
- [Game State Machine](#game-state-machine)
- [Build & Development](#build--development)
- [Burraco Game Rules](#burraco-game-rules)
- [Service Interactions](#service-interactions)
- [Event Flow Examples](#event-flow-examples)
- [Architectural Patterns](#architectural-patterns)

---

## Project Overview

**Kotlin/Vert.x microservices project** implementing a **Burraco card game** using **CQRS/Event Sourcing** patterns with **event-driven choreography** via Kafka.

### Core Characteristics
- **Architecture**: Event-driven microservices with CQRS and Event Sourcing
- **Communication**: Asynchronous via Kafka (choreography pattern)
- **Persistence**: Event Store (KurrentDB) as source of truth
- **Framework**: Vert.x 4.5.14 with Kotlin coroutines
- **Containerization**: Docker with docker-compose

---

## Core Architecture

### Modules (6)

#### 1. **Game** (`/Game`)
- **Purpose**: Main game service with REST API and game state machine
- **Responsibilities**:
    - Game lifecycle management (create, start, terminate)
    - Game state machine (Draft → WaitingDealer → Execution → Terminated)
    - Player turn management
    - Card deck management
- **Aggregate**: GameAggregate
- **REST API**: Yes (port 8081)
- **Publishes Events**: GameCreated, PlayerAdded, CardsRequestedToDealer
- **Consumes Events**: PlayerCreated, CardDealtToPlayer, CardDealtToDeck, CardDealtToPlayerDeck1, CardDealtToPlayerDeck2, CardDealtToDiscardDeck

#### 2. **Player** (`/Player`)
- **Purpose**: Player service managing player behavior and read projections
- **Responsibilities**:
    - Player lifecycle management (create, delete)
    - Player-specific data (cards in hand)
    - Maintains GameView projection for validation
    - Maintains PlayerView projection for queries
- **Aggregate**: PlayerAggregate
- **REST API**: Yes (port 8082)
- **Publishes Events**: PlayerCreated
- **Consumes Events**: CardDealtToPlayer (from Dealer), GameCreated (for projection), PlayerAdded (for projection)
- **Projections**: GameView (in-memory), PlayerView (in-memory)

#### 3. **Dealer** (`/Dealer`)
- **Purpose**: Card dealing service - purely event-driven (no REST API)
- **Responsibilities**:
    - Deals cards according to Burraco rules
    - 11 cards to each player
    - 11 cards to pozzetto 1
    - 11 cards to pozzetto 2
    - Remaining cards to deck
    - 1 card to discard pile
- **Aggregate**: DealerAggregate
- **REST API**: No (event-driven only)
- **Publishes Events**: CardDealtToPlayer, CardDealtToDeck, CardDealtToPlayerDeck1, CardDealtToPlayerDeck2, CardDealtToDiscardDeck
- **Consumes Events**: CardsRequestedToDealer

#### 4. **Common** (`/Common`)
- **Purpose**: Shared domain models and Vert.x utilities
- **Contents**: Common value objects, domain events, external events, identity classes

#### 5. **KafkaAdapter** (`/KafkaAdapter`)
- **Purpose**: Reusable Kafka integration patterns
- **Contents**: Kafka consumer/producer utilities, event routing

#### 6. **ReadModel** (`/ReadModel`)
- **Purpose**: CQRS read projections (MySQL/in-memory storage)
- **Note**: Currently integrated into Player service (GameView, PlayerView)

### Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Vert.x** | 4.5.14 | Reactive application framework with Kotlin coroutines |
| **KCQRS** | Custom | CQRS/Event Sourcing framework |
| **KurrentDB** | Latest | Event Store database (EventStore fork) |
| **Kafka/Redpanda** | 25.1.5 | Event streaming between services |
| **Docker** | Latest | Containerization |
| **Kotlin** | 2.1.0 | Programming language |
| **Gradle** | 9.1.0 | Build tool |
| **JDK** | 21 | Java runtime |

### Architectural Patterns

#### 1. **Event Sourcing**
- **All business state changes stored as events** in KurrentDB
- **Events are immutable** and form the source of truth
- **Aggregates are reconstructed** by replaying events from the event store
- **Example**: GameCreated, PlayerAdded, CardDealtToPlayer

#### 2. **CQRS (Command Query Responsibility Segregation)**
- **Commands modify state** via aggregates (write side)
- **Queries use read models** (read side)
- **Separate paths** for writes and reads
- **Example**: Player service maintains GameView projection for fast queries

#### 3. **Hexagonal Architecture (Ports & Adapters)**
- **Domain core**: Pure business logic (Game, Player, Dealer models)
- **Ports**: Interfaces defining how external world interacts
    - `ExternalEventPublisherPort` - Publishing events
    - `CommandControllerPort` - Receiving commands
    - `QueryControllerPort` - Handling queries
- **Adapters**: Concrete implementations
    - `KafkaExternalEventPublisherAdapter` - Kafka event publishing
    - `RestHttpServiceVerticle` - REST API
    - `KafkaConsumerVerticle` - Kafka event consumption

#### 4. **Domain-Driven Design (DDD)**
- **Bounded contexts**: Game, Dealer, Player
- **Aggregates**: GameAggregate, PlayerAggregate, DealerAggregate
- **Value objects**: Card, GameIdentity, PlayerIdentity
- **Domain events**: GameCreated, PlayerAdded, CardDealtToPlayer

---

## Development Guidelines

### Error Handling Mantra

#### **Diagnose, Don't Guess**
- When encountering a bug, explain possible causes step-by-step
- Check assumptions, inputs, and relevant code paths
- Don't jump to solutions without understanding the problem

#### **Graceful Handling**
- Code should handle errors gracefully
- Use try/catch around async calls
- Return user-friendly error messages or fallback values
- Example:
```kotlin
try {
    val result = someAsyncOperation()
    Result.success(result)
} catch (e: Exception) {
    log.error("Operation failed", e)
    Result.failure(UserFriendlyException("Unable to process request", e))
}
```

#### **Logging**
- Include helpful console logs or error logs for critical failures
- Avoid log spam in production code
- Use appropriate log levels (error, warn, info, debug)

#### **No Silent Failures**
- Do not swallow exceptions silently
- Always surface errors either by throwing or logging them
- Example (BAD):
```kotlin
try {
    dangerousOperation()
} catch (e: Exception) {
    // Silent failure - BAD!
}
```

#### **Binary Search for Bugs**
- On critical bugs, consider binary search through git history
- Add temporary console.debug statements to isolate the issue

### Clean Code Guidelines

#### **Function Size**
- Aim for functions **≤ 50 lines**
- If a function is doing too much, break it into smaller helper functions
- Example:
```kotlin
// BAD - Too long (100+ lines)
fun processComplexOperation() { /* ... */ }

// GOOD - Broken down
fun processComplexOperation() {
    val data = fetchData()
    val validated = validateData(data)
    val transformed = transformData(validated)
    return saveData(transformed)
}
```

#### **Single Responsibility Principle**
- Each function/module should have **one clear purpose**
- Don't lump unrelated logic together
- Example:
```kotlin
// BAD - Multiple responsibilities
fun saveUserAndSendEmail(user: User) {
    database.save(user)
    emailService.send(user.email, "Welcome!")
}

// GOOD - Separated responsibilities
fun saveUser(user: User) = database.save(user)
fun sendWelcomeEmail(user: User) = emailService.send(user.email, "Welcome!")
```

#### **Naming**
- Use **descriptive names**
- Avoid generic names like `tmp`, `data`, `handleStuff`
- Prefer `calculateInvoiceTotal` over `doCalc`
- Prefer `userRepository` over `repo`

#### **DRY Principle (Don't Repeat Yourself)**
- Do not duplicate code
- If similar logic exists in two places, refactor into a shared function
- If code must be duplicated, clarify why both need their own implementation

#### **Comments**
- Explain **non-obvious logic**
- Don't over-comment self-explanatory code
- Remove any leftover debug or commented-out code
- Example:
```kotlin
// GOOD - Explains non-obvious business rule
// Wild cards must be placed at lower end of sequence per Burraco rules
val wildCardPosition = sequence.minPosition

// BAD - Obvious comment
// Increment counter by 1
counter++
```

### Security Guidelines

#### **Input Validation**
- Validate **all inputs** (especially from users or external APIs)
- Never trust user input
- Check for valid email format, string length limits, etc.
- Example:
```kotlin
fun validateEmail(email: String): Result<String> {
    return when {
        email.isBlank() -> Result.failure(ValidationError("Email cannot be empty"))
        !email.matches(emailRegex) -> Result.failure(ValidationError("Invalid email format"))
        email.length > 255 -> Result.failure(ValidationError("Email too long"))
        else -> Result.success(email)
    }
}
```

#### **Authentication**
- **Never store passwords in plain text**
- Use bcrypt with a salt for hashing passwords
- Implement account lockout or rate limiting on repeated failed logins

#### **Database Safety**
- Use **parameterized queries** or an ORM to prevent SQL injection
- Do not concatenate user input in SQL queries directly
- Example:
```kotlin
// BAD - SQL Injection vulnerability
val query = "SELECT * FROM users WHERE email = '${userInput}'"

// GOOD - Parameterized query
val query = "SELECT * FROM users WHERE email = ?"
preparedStatement.setString(1, userInput)
```

### Edge Case Considerations

Always consider edge and corner cases for any logic:

#### **Empty or null inputs**
- Empty lists, missing fields, zero values
- Example: What happens if player list is empty?

#### **Max/min values and overflow**
- Extremely large numbers, very long text
- Example: What if gameId string is 1 million characters?

#### **Invalid states**
- End date before start date, negative quantities
- Example: Can a game transition from TERMINATED back to DRAFT?

#### **Concurrency issues**
- Two users editing the same data simultaneously
- Example: Two players trying to pick up the same card

#### **Handling**
- If an edge case is identified, handle it in code or at least flag it with a comment/TODO
- Prefer to **fail fast** on bad input (throw an error or return a safe default) rather than proceeding with wrong assumptions

### Workflow & Planning Guidelines

#### **Planning**
- For any complex or multi-step task, output a **clear plan or outline**
- List the steps or modules needed
- Example:
```
Plan to implement player turn management:
1. Add TurnPhase enum (PickUp, Play, End)
2. Create PlayerTurn value object
3. Implement turn transition logic in GameExecution
4. Add commands: StartTurn, EndTurn
5. Update REST API handlers
6. Write unit tests for turn transitions
```

#### **Incremental Development**
- Implement in **logical chunks**
- After each chunk, verify it aligns with the plan and passes tests before moving on
- Don't implement everything at once

#### **Think Aloud**
- Use **extended reasoning** ("think harder or ultrathink") for complex decisions
- It's okay to spend more tokens to ensure a solid approach rather than rushing coding

#### **User Approval**
- **Pause for confirmation** after providing a plan or major design decision
- Only proceed once the user/developer confirms
- Don't assume the user wants a specific implementation

#### **Error Recovery**
- If a solution isn't working, **backtrack and rethink** rather than stubbornly persisting
- Consider alternative approaches if tests fail or constraints are hit

### Documentation Management

#### **Keep Documentation Updated**
- **Every time the code changes**, verify if the documentation needs updating
- Don't let documentation drift from reality

#### **Document All Events**
- All events must be **kept updated and documented**
- Include event payload structure, when it's published, who consumes it

#### **Module Interactions**
- All module interactions must be **kept updated and well explained**
- Maintain a clean map of relationships

#### **Module Definition**
- Each module must have a **clear definition, scope, and ownership**
- Document what the module is responsible for and what it's NOT responsible for

---

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

---

## Build & Development

### Build Commands

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :Game:build
./gradlew :Dealer:build
./gradlew :Player:build

# Run tests
./gradlew test                                    # All tests
./gradlew :Game:test                              # Game module tests
./gradlew :Game:test --tests "*.SpecificTest"     # Single test

# Create Docker images (Shadow JARs)
./gradlew :Game:shadowJar
./gradlew :Dealer:shadowJar
./gradlew :Player:shadowJar

# Clean build
./gradlew clean build

# Start local environment
docker-compose up -d

# Stop local environment
docker-compose down -v
```

### E2E Testing

```bash
# Run E2E tests (uses existing images or builds if needed)
./gradlew :e2eTest:test

# Run E2E tests with fresh Docker images (recommended after code changes)
./gradlew :e2eTest:e2eTestClean

# Clean Docker images only
./gradlew :e2eTest:cleanDockerImages

# Build Docker images only
./gradlew :e2eTest:buildDockerImages
```

### Local Development Setup

```bash
# 1. Start infrastructure
docker-compose up -d

# 2. Build services
./gradlew build

# 3. Services available at:
# Game API: http://localhost:8081
# Player API: http://localhost:8082
# Kafka UI (Redpanda Console): http://localhost:8181
# EventStore UI: http://localhost:2113
```

### API Testing

- Bruno collection available in `/Game/brunoCollection/`
- REST API examples for:
    - Creating games
    - Adding players
    - Starting games
    - Requesting card deals

### Configuration

- **Format**: HOCON (application.conf files)
- **Environment Variable Overrides**: Supported for production
- **Configures**:
    - REST API ports
    - Kafka brokers
    - EventStore connections
    - Database connections

Example:
```hocon
http {
  port = 8081
  port = ${?HTTP_PORT}  # Override with env var
}

kafka {
  bootstrap.servers = "localhost:19092"
  bootstrap.servers = ${?KAFKA_BOOTSTRAP_SERVERS}
}
```

### Testing Strategy

#### **Unit Tests**
- Domain logic using **JUnit5**
- Test individual commands, aggregates, and business rules
- Example: Test that AddPlayer command validates max players

#### **Integration Tests**
- **Testcontainers** for Kafka/databases
- Test service integration with external dependencies
- Example: Test that events are published to Kafka

#### **Vert.x Async Testing**
- Coroutines support
- Test async handlers and verticles
- Example: Test REST API handlers

#### **E2E Tests**
- **Cucumber/Gherkin** for BDD scenarios
- **docker-compose** for full stack testing
- Tests entire service interactions
- Example: Create game, add players, deal cards, verify player has 11 cards

---

## Burraco Game Rules

### Game Overview
- **Players**: 4 players in fixed partnerships (North-South vs East-West)
- **Objective**: Form melds (sets and sequences) and score points by placing them on the table
- **First to 2000 points wins**

### Card Components

#### **Deck Composition**
- **Total**: 108 cards (2 standard 52-card decks + 4 jokers)
- **Wild Cards**:
    - 4 Jokers ("jolly") - always wild
    - 8 Twos ("pinelle") - can be wild OR natural in sequences

#### **Card Values (for scoring)**
```
Joker:        30 points
Two:          20 points
Ace:          15 points
K,Q,J,10,9,8: 10 points each
7,6,5,4,3:     5 points each
```

#### **Card Rank (high to low)**
```
Joker > 2 > A > K > Q > J > 10 > 9 > 8 > 7 > 6 > 5 > 4 > 3
```

**Suit Rank**: Spades > Hearts > Diamonds > Clubs

### Initial Deal

```
Each player:     11 cards
Pozzetto 1:      11 cards (face-down)
Pozzetto 2:      11 cards (face-down)
Draw pile:       41 cards (face-down)
Discard pile:     1 card (face-up)
```

**Dealer Selection**: Draw cards to determine first dealer (lowest card deals). Deal rotates clockwise after each hand.

### Melds (Card Combinations)

#### **Sets (Combinazione)**
- **Definition**: 3+ cards of same rank
- **Wild Card Limit**: Maximum 1 wild card per set
- **Maximum Size**: 9 cards total (4 natural + 1 wild, since 4 of a rank in 2 decks + 1 wild = 9)
- **Restriction**: Cannot be all wild cards
- **Team Limit**: Only 1 set per rank per team

**Valid Examples**:
- `10♠-10♥-10♦` (3 natural)
- `4♠-4♥-4♦-4♣-2♠` (4 natural + 1 wild)
- `K♠-K♥-Joker` (2 natural + 1 wild)

#### **Sequences (Sequenze)**
- **Definition**: 3+ consecutive cards of same suit
- **Order**: `(A)-2-3-4-5-6-7-8-9-10-J-Q-K-(A)`
- **Wild Card Limit**: Maximum 1 wild card per sequence
- **Ace Placement**: Either end (above K or below 2), but not both ends
- **Two Usage**: Can be natural (in position between A and 3) AND/OR wild card

**Valid Examples**:
- `7♠-8♠-9♠` (3 natural)
- `A♥-2♥-3♥-2♣-5♥` (4 natural + 1 wild representing 4♥)
- `9♦-2♠-J♦` (2 natural + 1 wild representing 10♦)

#### **Burraco**
- **Definition**: Meld of 7+ cards
- **Types**:
    - **Clean (Pulito)**: No wild cards = **200 bonus points**
    - **Dirty (Sporco)**: Contains wild card = **100 bonus points**

### Turn Structure

#### **Player Actions (in order)**
1. **Draw**: Take top card from draw pile OR entire discard pile
2. **Meld** (optional): Place valid sets/sequences, extend team's existing melds
3. **Discard**: Place 1 card face-up on discard pile

#### **Drawing Rules**
- **From Draw Pile**: Take **1 card only**
- **From Discard Pile**: Must take **ALL cards** in pile
- **No Restrictions**: Can always take discard pile (no meld requirement like in some variants)

### Pozzetti System

Each team gets **ONE pozzetto** when first player runs out of cards.

#### **Method 1: Direct Take**
1. Draw card
2. Meld **ALL cards** from hand
3. Take pozzetto immediately
4. Continue playing with pozzetto as new hand

#### **Method 2: Take on Discard**
1. Draw card
2. Meld all but 1 card
3. Discard final card
4. Take pozzetto **face-down** (don't look)
5. Wait for partner's turn to complete
6. Then use pozzetto as new hand

### Wild Card Rules

#### **Placement in Sequences**
- Wild cards must be placed at **lower end** of sequence
- Example: `2♠-6♥-7♥` (wild at position 2, representing 5♥)
- NOT: `6♥-7♥-2♠` (invalid - wild not at lower end)

#### **Replacement Rules**
- Wild card can only be replaced by **exact natural card it represents**
- When replaced, wild card moves to lower end of sequence
- Natural two can become wild if needed for sequence extension

#### **Special Cases**
- 13-card sequence + wild card can become 14-card sequence when wild replaced
- Natural two at sequence start can become wild for top extension

### Game End Conditions

#### **Method 1: Going Out (Chiusura)**
**Requirements**:
1. Team has taken their pozzetto
2. Team has at least 1 burraco (7+ card meld)
3. Player melds all but 1 card and discards final card
4. Final discard **cannot be wild card**

**Bonus**: **100 points**

#### **Method 2: Draw Pile Exhaustion**
- Game ends when draw pile has only **2 cards left**
- No going out bonus awarded

#### **Method 3: Stalemate**
- All players repeatedly take single-card discard pile
- No progress possible, game ends
- No going out bonus awarded

### Scoring System

```
+ Card values of melds on table
- Card values of cards in hand
+ 200 points per clean burraco
+ 100 points per dirty burraco
+ 100 points for going out
- 100 points if team hasn't taken pozzetto
```

**Game Victory**: First team to exceed **2000 points** wins. Higher score wins if both exceed 2000.

### Legal Move Constraints

#### **Forbidden Actions**
- Cannot add cards to opponent's melds
- Cannot go out without team burraco
- Cannot go out by discarding wild card
- Cannot discard same card just picked from single-card pile
- Cannot have multiple sets of same rank per team
- Cannot split or join sequences once placed

#### **Allowed Actions**
- Can discard wild cards (except when going out)
- Can discard any card from multi-card pile pickup
- Can extend sequences in either direction
- Can replace wild cards with natural cards in sequences

### Three-Player Variation

#### **Setup Differences**
- Each player gets 11 cards
- **Pozzetti**: 1 pile of 18 cards, 1 pile of 11 cards
- First player to meld all cards takes 18-card pozzetto (plays alone)
- Other two become partners, first to empty hand takes 11-card pozzetto

#### **Scoring**
- Partners split their combined score
- All other rules remain the same

### Implementation Notes

#### **Key Data Structures Needed**
- `Card`: suit, rank, isWild
- `Meld`: cards[], type (SET/SEQUENCE), isClean, isBurraco
- `Player`: hand[], team
- `Game`: drawPile, discardPile, pozzetti[], teams[], currentPlayer

#### **Critical Validation Functions**
- `isValidSet(cards[])` - Validate set rules
- `isValidSequence(cards[])` - Validate sequence rules
- `canGoOut(team)` - Check pozzetto taken + has burraco
- `calculateMeldPoints(meld)` - Calculate points
- `isLegalDiscard(card, isGoingOut)` - Validate discard

#### **Game State Tracking**
- Track which team has taken pozzetto
- Track burraco count per team
- Track wild card positions in sequences
- Enforce single set per rank per team rule

---

## Service Interactions

### Architecture Overview

The system follows a **choreography-based event-driven architecture** where services communicate asynchronously via Kafka events. Each service is autonomous and reacts to events from other services without a central orchestrator.

### Communication Patterns

#### **Synchronous (REST API)**
- User → Game service (HTTP)
- User → Player service (HTTP)

#### **Asynchronous (Kafka Events)**
- Game → Dealer (via Kafka)
- Dealer → Game (via Kafka)
- Dealer → Player (via Kafka)
- Player → Game (via Kafka)
- Game → Player (via Kafka, for projections)

### Service Details

#### **Game Service**

**File Location**: `/Game`

**REST API Endpoints**:
```
POST   /game                      - Create new game
POST   /game/{gameId}/start       - Request card dealing
GET    /health                    - Health check
```

**Kafka Topics**:
```
Publishes to: game-events
Consumes from: player-events, dealer-events
```

**Events Published** (Game → Kafka → Other Services):
```kotlin
// File: Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/externalEventPublisher/kafka/KafkaExternalEventPublisherAdapter.kt

1. GameCreated
   - Topic: game-events
   - Payload: { aggregateId: GameIdentity }
   - Consumed by: Player service (GameView projection)

2. PlayerAdded
   - Topic: game-events
   - Payload: { aggregateId: GameIdentity, playerIdentity: PlayerIdentity }
   - Consumed by: Player service (GameView projection)

3. CardsRequestedToDealer
   - Topic: game-events
   - Payload: { aggregateId: GameIdentity, players: List<PlayerIdentity> }
   - Consumed by: Dealer service (triggers card dealing)
```

**Events Consumed** (Kafka → Game Service):
```kotlin
// File: Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/commandController/kafka/KafkaPlayerConsumerVerticle.kt
// File: Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/commandController/kafka/KafkaDealerConsumerVerticle.kt

1. PlayerCreated (from Player service)
   - Topic: player-events
   - Handler: AddPlayerHandlerKafka
   - Action: Adds player to GameDraft aggregate
   - Command: AddPlayer

2. CardDealtToPlayer (from Dealer service)
   - Topic: dealer-events
   - Handler: AddCardPlayerHandlerKafka
   - Action: Adds card to player's hand in game
   - Command: AddCardPlayer

3. CardDealtToDeck (from Dealer service)
   - Topic: dealer-events
   - Handler: AddCardDeckHandlerKafka
   - Action: Adds card to main deck
   - Command: AddCardDeck

4. CardDealtToPlayerDeck1 (from Dealer service)
   - Topic: dealer-events
   - Handler: AddCardPlayerDeck1HandlerKafka
   - Action: Adds card to first pozzetto
   - Command: AddCardFirstPlayerDeck

5. CardDealtToPlayerDeck2 (from Dealer service)
   - Topic: dealer-events
   - Handler: AddCardPlayerDeck2HandlerKafka
   - Action: Adds card to second pozzetto
   - Command: AddCardSecondPlayerDeck

6. CardDealtToDiscardDeck (from Dealer service)
   - Topic: dealer-events
   - Handler: AddCardDiscardDeckHandlerKafka
   - Action: Adds card to discard pile
   - Command: AddCardDiscardDeck
```

**Aggregate**: GameAggregate
**Event Store**: KurrentDB (stream: game-{gameId})

---

#### **Player Service**

**File Location**: `/Player`

**REST API Endpoints**:
```
POST   /player                               - Create new player
GET    /player/{playerId}/game/{gameId}     - Get player view (cards in hand)
DELETE /player/{playerId}                    - Delete player
GET    /health                               - Health check
```

**Kafka Topics**:
```
Publishes to: player-events
Consumes from: dealer-events, game-events
```

**Events Published** (Player → Kafka → Other Services):
```kotlin
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/adapter/externalEventPublisher/kafka/KafkaExternalEventPublisherAdapter.kt

1. PlayerCreated
   - Topic: player-events
   - Payload: { aggregateId: PlayerIdentity, gameIdentity: GameIdentity }
   - Consumed by: Game service (adds player to game)
```

**Events Consumed** (Kafka → Player Service):
```kotlin
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/adapter/commandController/kafka/KafkaDealerConsumerVerticle.kt
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/adapter/projection/GameViewProjectionKafkaVerticle.kt

1. CardDealtToPlayer (from Dealer service)
   - Topic: dealer-events
   - Handler: AddCardToPlayerHandlerKafka
   - Action: Adds card to PlayerAggregate
   - Command: AddCardToPlayer

2. GameCreated (from Game service) - FOR PROJECTION
   - Topic: game-events
   - Handler: GameCreatedProjectionKafkaEventHandler
   - Action: Creates GameView projection entry
   - Projection: GameView

3. PlayerAdded (from Game service) - FOR PROJECTION
   - Topic: game-events
   - Handler: PlayerAddedProjectionKafkaEventHandler
   - Action: Updates GameView projection (adds player to game)
   - Projection: GameView
```

**Read Projections** (CQRS):

**1. GameView Projection**
```kotlin
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/projection/gameview/GameView.kt
// Purpose: Maintains denormalized view of games for validation

Data Structure:
- gameId: GameIdentity
- state: GameState (DRAFT, STARTED, etc.)
- players: List<PlayerIdentity>
- maxPlayers: Int

Storage: InMemoryGameViewRepository

Updated by Kafka events:
- GameCreated → Creates new GameView
- PlayerAdded → Adds player to GameView

Usage Example:
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/adapter/commandController/rest/handlers/CreatePlayerRoutingHandler.kt
val gameView = gameViewRepository.findByGameId(gameIdentity)
when {
    gameView == null -> responseWithError(404, "GameNotFound")
    gameView.isFull() -> responseWithError(409, "GameFull")
    gameView.state != GameState.DRAFT -> responseWithError(409, "InvalidGameState")
    else -> createPlayer(...)
}
```

**2. PlayerView Projection**
```kotlin
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/projection/playerview/PlayerView.kt
// Purpose: Maintains denormalized view of player's cards for queries

Data Structure:
- playerId: PlayerIdentity
- gameId: GameIdentity
- cards: List<Card>

Storage: InMemoryPlayerViewRepository

Updated by EventStore subscription:
- PlayerViewProjectionEventStoreVerticle subscribes to player-{playerId} stream
- Rebuilds PlayerView from all PlayerEvents

Usage Example:
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/adapter/commandController/rest/handlers/GetPlayerViewRoutingHandler.kt
GET /player/{playerId}/game/{gameId}
Returns: { playerId, gameId, cards: [...] }
```

**Aggregate**: PlayerAggregate
**Event Store**: KurrentDB (stream: player-{playerId})

---

#### **Dealer Service**

**File Location**: `/Dealer`

**REST API**: None (purely event-driven)

**Kafka Topics**:
```
Publishes to: dealer-events
Consumes from: game-events
```

**Events Published** (Dealer → Kafka → Other Services):
```kotlin
// File: Dealer/src/main/kotlin/com/abaddon83/burraco/dealer/adapters/externalEventPublisher/kafka/KafkaExternalEventPublisherAdapter.kt

1. CardDealtToPlayer
   - Topic: dealer-events
   - Payload: { aggregateId: DealerId, gameId: GameIdentity, playerId: PlayerIdentity, card: String }
   - Consumed by: Game service, Player service

2. CardDealtToDeck
   - Topic: dealer-events
   - Payload: { aggregateId: DealerId, gameId: GameIdentity, card: String }
   - Consumed by: Game service

3. CardDealtToPlayerDeck1
   - Topic: dealer-events
   - Payload: { aggregateId: DealerId, gameId: GameIdentity, card: String }
   - Consumed by: Game service

4. CardDealtToPlayerDeck2
   - Topic: dealer-events
   - Payload: { aggregateId: DealerId, gameId: GameIdentity, card: String }
   - Consumed by: Game service

5. CardDealtToDiscardDeck
   - Topic: dealer-events
   - Payload: { aggregateId: DealerId, gameId: GameIdentity, card: String }
   - Consumed by: Game service
```

**Events Consumed** (Kafka → Dealer Service):
```kotlin
// File: Dealer/src/main/kotlin/com/abaddon83/burraco/dealer/adapters/commandController/kafka/KafkaGameConsumerVerticle.kt

1. CardsRequestedToDealer (from Game service)
   - Topic: game-events
   - Handler: CardsRequestedToDealerHandlerKafka
   - Action: Creates DealerAggregate and deals all cards
   - Commands: CreateDecks, DealCardToPlayer, DealCardToDeck, DealCardToPlayerDeck1, DealCardToPlayerDeck2, DealCardToDiscardDeck
```

**Card Dealing Algorithm**:
```kotlin
// File: Dealer/src/main/kotlin/com/abaddon83/burraco/dealer/services/DealerService.kt

For each player (11 cards):
  1. Draw card from deck
  2. Execute DealCardToPlayer command
  3. Publish CardDealtToPlayer event

For pozzetto 1 (11 cards):
  1. Draw card from deck
  2. Execute DealCardToPlayerDeck1 command
  3. Publish CardDealtToPlayerDeck1 event

For pozzetto 2 (11 cards):
  1. Draw card from deck
  2. Execute DealCardToPlayerDeck2 command
  3. Publish CardDealtToPlayerDeck2 event

For main deck (remaining cards):
  1. Draw card from deck
  2. Execute DealCardToDeck command
  3. Publish CardDealtToDeck event

For discard pile (1 card):
  1. Draw card from deck
  2. Execute DealCardToDiscardDeck command
  3. Publish CardDealtToDiscardDeck event
```

**Aggregate**: DealerAggregate
**Event Store**: KurrentDB (stream: dealer-{gameId})

---

## Event Flow Examples

### Example 1: Complete Game Startup Flow (2 Players)

```
┌─────────┐     ┌──────────┐     ┌────────┐     ┌────────────┐
│  User   │     │   Game   │     │ Player │     │   Dealer   │
└────┬────┘     └─────┬────┘     └────┬───┘     └─────┬──────┘
     │                │               │               │
     │ POST /game     │               │               │
     ├───────────────>│               │               │
     │                │               │               │
     │                │ Create        │               │
     │                │ GameDraft     │               │
     │                │ aggregate     │               │
     │                │               │               │
     │                │ Publish       │               │
     │                │ GameCreated   │               │
     │                ├──────────────>│               │
     │                │   (Kafka)     │               │
     │                │               │               │
     │                │               │ Update        │
     │                │               │ GameView      │
     │                │               │ projection    │
     │                │               │               │
     │ { gameId }     │               │               │
     │<───────────────┤               │               │
     │                │               │               │
     │ POST /player   │               │               │
     │ {gameId, user1}│               │               │
     ├───────────────────────────────>│               │
     │                │               │               │
     │                │               │ Validate      │
     │                │               │ via GameView  │
     │                │               │ ✓ exists      │
     │                │               │ ✓ not full    │
     │                │               │ ✓ DRAFT state │
     │                │               │               │
     │                │               │ Create        │
     │                │               │ PlayerDraft   │
     │                │               │ aggregate     │
     │                │               │               │
     │                │               │ Publish       │
     │                │               │ PlayerCreated │
     │                │<──────────────┤               │
     │                │   (Kafka)     │               │
     │                │               │               │
     │                │ Add player    │               │
     │                │ to GameDraft  │               │
     │                │               │               │
     │                │ Publish       │               │
     │                │ PlayerAdded   │               │
     │                ├──────────────>│               │
     │                │   (Kafka)     │               │
     │                │               │               │
     │                │               │ Update        │
     │                │               │ GameView      │
     │                │               │ (1 player)    │
     │                │               │               │
     │ { playerId }   │               │               │
     │<───────────────────────────────┤               │
     │                │               │               │
     │ POST /player   │               │               │
     │ {gameId, user2}│               │               │
     ├───────────────────────────────>│               │
     │                │               │               │
     │   [Repeat player creation flow]               │
     │                │               │               │
     │                │               │ GameView now  │
     │                │               │ shows 2 players│
     │                │               │               │
     │ POST /game/X/start             │               │
     ├───────────────>│               │               │
     │                │               │               │
     │                │ Request       │               │
     │                │ deal cards    │               │
     │                │               │               │
     │                │ Transition    │               │
     │                │ GameDraft →   │               │
     │                │ GameWaiting   │               │
     │                │ Dealer        │               │
     │                │               │               │
     │                │ Publish       │               │
     │                │ CardsRequested│               │
     │                │ ToDealer      │               │
     │                ├──────────────────────────────>│
     │                │   (Kafka)     │               │
     │                │               │               │
     │                │               │               │ Create
     │                │               │               │ DealerAggregate
     │                │               │               │
     │                │               │               │ Deal 11 cards
     │                │               │               │ to player1
     │                │<──────────────────────────────┤
     │                │ CardDealtToPlayer (×11, Kafka)│
     │                │               │<──────────────┤
     │                │               │ CardDealtToPlayer (×11, Kafka)
     │                │               │               │
     │                │ Add cards to  │ Add cards to  │
     │                │ player1 hand  │ player1 agg   │
     │                │               │               │
     │                │               │               │ Deal 11 cards
     │                │               │               │ to player2
     │                │<──────────────────────────────┤
     │                │ CardDealtToPlayer (×11, Kafka)│
     │                │               │<──────────────┤
     │                │               │ CardDealtToPlayer (×11, Kafka)
     │                │               │               │
     │                │ Add cards to  │ Add cards to  │
     │                │ player2 hand  │ player2 agg   │
     │                │               │               │
     │                │               │               │ Deal 11 cards
     │                │               │               │ to pozzetto1
     │                │<──────────────────────────────┤
     │                │ CardDealtToPlayerDeck1 (×11)  │
     │                │               │               │
     │                │ Add cards to  │               │
     │                │ pozzetto1     │               │
     │                │               │               │
     │                │               │               │ Deal 11 cards
     │                │               │               │ to pozzetto2
     │                │<──────────────────────────────┤
     │                │ CardDealtToPlayerDeck2 (×11)  │
     │                │               │               │
     │                │ Add cards to  │               │
     │                │ pozzetto2     │               │
     │                │               │               │
     │                │               │               │ Deal remaining
     │                │               │               │ to main deck
     │                │<──────────────────────────────┤
     │                │ CardDealtToDeck (×N)          │
     │                │               │               │
     │                │ Add cards to  │               │
     │                │ main deck     │               │
     │                │               │               │
     │                │               │               │ Deal 1 card
     │                │               │               │ to discard
     │                │<──────────────────────────────┤
     │                │ CardDealtToDiscardDeck (×1)   │
     │                │               │               │
     │                │ Add card to   │               │
     │                │ discard pile  │               │
     │                │               │               │
     │                │ Transition    │               │
     │                │ GameWaiting → │               │
     │                │ GameExecution │               │
     │                │               │               │
     │ { status:      │               │               │
     │   STARTED }    │               │               │
     │<───────────────┤               │               │
     │                │               │               │
     │ GET /player/   │               │               │
     │   {id}/game/{X}│               │               │
     ├───────────────────────────────>│               │
     │                │               │               │
     │                │               │ Query         │
     │                │               │ PlayerView    │
     │                │               │ projection    │
     │                │               │               │
     │ { playerId,    │               │               │
     │   gameId,      │               │               │
     │   cards: [11] }│               │               │
     │<───────────────────────────────┤               │
     │                │               │               │
```

### Example 2: Event-Driven Saga Pattern - Card Dealing

This example shows how the **card dealing workflow** is implemented as a distributed saga using events:

```
┌────────────┐                         ┌─────────────┐
│ Game       │                         │ Dealer      │
│ Service    │                         │ Service     │
└─────┬──────┘                         └──────┬──────┘
      │                                       │
      │ 1. User requests deal cards           │
      │    POST /game/{id}/start              │
      │                                       │
      │ 2. GameDraft executes                 │
      │    RequestDealCards command           │
      │                                       │
      │ 3. Transition to GameWaitingDealer    │
      │                                       │
      │ 4. Publish CardsRequestedToDealer     │
      ├──────────────────────────────────────>│
      │        Topic: game-events             │
      │                                       │
      │                                       │ 5. Consume event
      │                                       │    CardsRequestedToDealerHandlerKafka
      │                                       │
      │                                       │ 6. Create DealerAggregate
      │                                       │    Execute CreateDecks command
      │                                       │
      │                                       │ 7. Deal cards sequentially
      │                                       │    For each card:
      │                                       │      - Execute command
      │                                       │      - Publish event
      │                                       │
      │ 8. Consume CardDealtToPlayer          │
      │<──────────────────────────────────────┤
      │        Topic: dealer-events           │
      │                                       │
      │ 9. Execute AddCardPlayer command      │
      │    Updates GameWaitingDealer state    │
      │                                       │
      │ 10. After all cards dealt:            │
      │     Execute StartPlayerTurn command   │
      │                                       │
      │ 11. Transition to GameExecution       │
      │                                       │
```

**Key Observations**:
- **No orchestrator**: Dealer service decides when to deal each card
- **Eventual consistency**: Game state is updated card-by-card
- **Idempotency**: If a CardDealtToPlayer event is replayed, the result is the same
- **Error handling**: If Dealer service fails, it can resume from last dealt card (event sourcing)

---

## Architectural Patterns

### 1. Event Sourcing Pattern

**Implementation**:
```
┌─────────────┐
│  Command    │
│  Handler    │
└──────┬──────┘
       │
       │ Execute command
       │
       v
┌─────────────┐      Generate      ┌─────────────┐
│  Aggregate  │─────────────────────>│   Events    │
│   (State)   │                     │ (Immutable) │
└─────────────┘                     └──────┬──────┘
       ^                                   │
       │                                   │
       │ Replay events                     │ Store
       │                                   │
       │                            ┌──────v──────┐
       │                            │  KurrentDB  │
       │                            │ (EventStore)│
       └────────────────────────────┤             │
                                    └─────────────┘
```

**Example**:
```kotlin
// File: Game/src/main/kotlin/com/abaddon83/burraco/game/commands/gameDraft/CreateGame.kt

// Command
data class CreateGame(val aggregateId: GameIdentity)

// Event
data class GameCreated(val aggregateId: GameIdentity, val occurredOn: Instant)

// Aggregate
class GameDraft {
    companion object {
        fun create(gameIdentity: GameIdentity): Validated<GameCreated, DomainError> {
            val event = GameCreated(gameIdentity, Instant.now())
            return Validated.Valid(event)
        }
    }

    fun applyEvent(event: GameCreated): GameDraft {
        // Reconstruct state from event
        return GameDraft(event.aggregateId)
    }
}
```

**Benefits**:
- **Audit trail**: Every state change is recorded
- **Temporal queries**: Reconstruct state at any point in time
- **Event replay**: Rebuild aggregates from scratch
- **Debug**: Trace exactly what happened

### 2. CQRS Pattern

**Implementation**:
```
┌──────────┐                    ┌───────────────┐
│          │   Commands         │               │
│   User   ├───────────────────>│  Write Side   │
│          │                    │  (Aggregates) │
└──────────┘                    └───────┬───────┘
     ^                                  │
     │                                  │ Events
     │                                  │
     │  Query Results            ┌──────v──────┐
     │                           │  KurrentDB  │
     │                           │ (EventStore)│
     │                           └──────┬──────┘
     │                                  │
     │                                  │ Events
     │                                  │
     │                           ┌──────v──────┐
     │          Query            │  Read Side  │
     └───────────────────────────┤ (Projections)│
                                 └─────────────┘
```

**Example**:
```kotlin
// WRITE SIDE
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/command/playerDraft/CreatePlayer.kt
data class CreatePlayer(
    val aggregateId: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val user: String
)

// Event stored in EventStore
data class PlayerCreated(
    val aggregateId: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val occurredOn: Instant
)

// READ SIDE
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/projection/playerview/PlayerView.kt
data class PlayerView(
    val playerId: PlayerIdentity,
    val gameId: GameIdentity,
    val cards: List<Card>
)

// Projection updated by events
class PlayerViewProjectionHandler {
    fun handle(event: CardDealtToPlayer) {
        val playerView = repository.findById(event.playerId)
        val updated = playerView.copy(cards = playerView.cards + event.card)
        repository.save(updated)
    }
}
```

**Benefits**:
- **Optimized reads**: Projections tailored for query patterns
- **Scalability**: Read and write sides can scale independently
- **Flexibility**: Multiple projections from same events
- **Performance**: Fast queries without complex joins

### 3. Hexagonal Architecture (Ports & Adapters)

**Implementation**:
```
┌───────────────────────────────────────────────────┐
│                 Adapters (Outside)                │
│                                                   │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  │
│  │    REST    │  │   Kafka    │  │ EventStore │  │
│  │  Handler   │  │  Consumer  │  │  Adapter   │  │
│  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘  │
│        │               │               │         │
└────────┼───────────────┼───────────────┼─────────┘
         │               │               │
         v               v               v
┌───────────────────────────────────────────────────┐
│                  Ports (Interfaces)               │
│                                                   │
│  ┌────────────────┐  ┌──────────────────────┐    │
│  │ CommandController│  │ ExternalEventPublisher│    │
│  │      Port       │  │        Port          │    │
│  └────────────────┘  └──────────────────────┘    │
└───────────────────────────────────────────────────┘
         │                            ^
         │                            │
         v                            │
┌───────────────────────────────────────────────────┐
│              Domain (Business Logic)              │
│                                                   │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  │
│  │ Aggregates │  │  Commands  │  │   Events   │  │
│  │  (Game,    │  │ (CreateGame│  │ (GameCreated│  │
│  │  Player,   │  │  AddPlayer)│  │ PlayerAdded)│  │
│  │  Dealer)   │  │            │  │            │  │
│  └────────────┘  └────────────┘  └────────────┘  │
└───────────────────────────────────────────────────┘
```

**Example**:
```kotlin
// DOMAIN (Core)
// File: Game/src/main/kotlin/com/abaddon83/burraco/game/models/game/Game.kt
sealed class Game {
    abstract val id: GameIdentity
}

// PORT (Interface)
// File: Game/src/main/kotlin/com/abaddon83/burraco/game/ports/ExternalEventPublisherPort.kt
interface ExternalEventPublisherPort {
    suspend fun publish(aggregate: Game, event: GameEvent): Result<Unit>
}

// ADAPTER (Implementation)
// File: Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/externalEventPublisher/kafka/KafkaExternalEventPublisherAdapter.kt
class KafkaExternalEventPublisherAdapter(
    vertx: Vertx,
    kafkaConfig: KafkaProducerConfig
) : ExternalEventPublisherPort {
    override suspend fun publish(aggregate: Game, event: GameEvent): Result<Unit> {
        // Kafka-specific implementation
    }
}
```

**Benefits**:
- **Testability**: Easy to mock adapters
- **Flexibility**: Swap implementations without changing domain
- **Independence**: Domain doesn't depend on infrastructure
- **Maintainability**: Clear separation of concerns

### 4. Choreography (Event-Driven Saga)

**Implementation**:
```
┌─────────────┐       Event       ┌─────────────┐
│   Service   │──────────────────>│   Service   │
│      A      │                   │      B      │
└─────────────┘                   └──────┬──────┘
                                         │
                                         │ Event
                                         │
                                         v
                                  ┌─────────────┐
                                  │   Service   │
                                  │      C      │
                                  └─────────────┘
```

**No Central Orchestrator**: Each service reacts to events independently.

**Example - Card Dealing Saga**:
```kotlin
// STEP 1: Game service publishes event
// File: Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/externalEventPublisher/kafka/KafkaExternalEventPublisherAdapter.kt
when (domainEvent) {
    is CardDealingRequested -> {
        val event = CardsRequestedToDealerExternalEvent(
            domainEvent.aggregateId,
            listPlayerIdentities
        )
        publishToKafka(event)
    }
}

// STEP 2: Dealer service consumes event and deals cards
// File: Dealer/src/main/kotlin/com/abaddon83/burraco/dealer/adapters/commandController/kafka/handlers/CardsRequestedToDealerHandlerKafka.kt
override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
    val cardsRequestedEvent = Json.decodeValue(event.eventPayload, CardsRequestedToDealerExternalEvent::class.java)
    return commandController.cardRequestedToDealer(
        cardsRequestedEvent.aggregateIdentity,
        cardsRequestedEvent.players
    )
}

// STEP 3: Dealer publishes CardDealtToPlayer events
// File: Dealer/src/main/kotlin/com/abaddon83/burraco/dealer/adapters/externalEventPublisher/kafka/KafkaExternalEventPublisherAdapter.kt
when (domainEvent) {
    is CardDealtToPlayer -> CardDealtToPlayerExternalEvent(
        domainEvent.aggregateId,
        domainEvent.gameId,
        domainEvent.playerId,
        domainEvent.card.label
    )
}

// STEP 4: Game service consumes event and updates state
// File: Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/commandController/kafka/handlers/AddCardPlayerHandlerKafka.kt
override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
    val cardDealtEvent = Json.decodeValue(event.eventPayload, CardDealtToPlayer::class.java)
    return commandController.addCardToPlayer(
        cardDealtEvent.gameId,
        cardDealtEvent.playerId,
        cardDealtEvent.card
    )
}

// STEP 5: Player service also consumes same event
// File: Player/src/main/kotlin/com/abaddon83/burraco/player/adapter/commandController/kafka/handlers/AddCardToPlayerHandlerKafka.kt
override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
    val cardDealtEvent = Json.decodeValue(event.eventPayload, CardDealtToPlayer::class.java)
    return commandController.addCardToPlayer(
        cardDealtEvent.playerId,
        cardDealtEvent.card
    )
}
```

**Benefits**:
- **Loose coupling**: Services don't know about each other
- **Scalability**: Each service scales independently
- **Resilience**: Failure of one service doesn't affect others
- **Flexibility**: Easy to add new consumers

**Challenges**:
- **Eventual consistency**: State updates are not immediate
- **Debugging**: Harder to trace flow across services
- **Compensation**: Need to handle failure scenarios

---

## Summary of Service Responsibilities

| Service | Aggregate | REST API | Publishes Events | Consumes Events | Projections | Event Store |
|---------|-----------|----------|------------------|-----------------|-------------|-------------|
| **Game** | GameAggregate | ✅ Create/Start game (8081) | GameCreated, PlayerAdded, CardsRequestedToDealer | PlayerCreated, CardDealt* (5 types) | None | KurrentDB: game-{id} |
| **Player** | PlayerAggregate | ✅ Create/Query player (8082) | PlayerCreated | CardDealtToPlayer, GameCreated (proj), PlayerAdded (proj) | GameView, PlayerView | KurrentDB: player-{id} |
| **Dealer** | DealerAggregate | ❌ Event-driven only | CardDealtToPlayer, CardDealtToDeck, CardDealtToPlayerDeck1/2, CardDealtToDiscardDeck | CardsRequestedToDealer | None | KurrentDB: dealer-{gameId} |

---

## Key Takeaways

### Architecture
1. **Event-driven choreography** - Services communicate via events, no central orchestrator
2. **Event sourcing** - All state changes stored as immutable events in KurrentDB
3. **CQRS** - Separate paths for writes (commands) and reads (projections)
4. **Hexagonal architecture** - Domain isolated from infrastructure via ports/adapters
5. **Eventual consistency** - Projections may lag behind events

### Development Philosophy
1. **Diagnose before fixing** - Understand the problem before coding
2. **Clean code** - Small functions, descriptive names, DRY principle
3. **Security first** - Validate inputs, use parameterized queries, hash passwords
4. **Edge cases** - Always consider null, empty, max/min, concurrency
5. **Incremental** - Plan, implement in chunks, verify each step

### Service Communication
1. **Game → Dealer** - CardsRequestedToDealer (via Kafka)
2. **Dealer → Game** - CardDealt* events (via Kafka)
3. **Player → Game** - PlayerCreated (via Kafka)
4. **Game → Player** - GameCreated, PlayerAdded (via Kafka, for projections)
5. **Dealer → Player** - CardDealtToPlayer (via Kafka)

### Testing
1. **Unit tests** - Domain logic (JUnit5)
2. **Integration tests** - Kafka/DB (Testcontainers)
3. **E2E tests** - Full stack (Cucumber/docker-compose)
4. **Always run e2eTestClean** after code changes to ensure fresh Docker images

---

**End of Summary**
