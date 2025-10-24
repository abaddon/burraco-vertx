# CLAUDE.md

## Summary
This is a **Kotlin/Vert.x microservices project** implementing a Burraco card game using **CQRS/Event Sourcing** patterns.

## Core Modules
- **Game** (`/Game`) - Main game service with REST API and game logic state machine
- **Dealer** (`/Dealer`) - Card dealing service, purely event-driven
- **Common** (`/Common`) - Shared domain models and Vert.x utilities
- **KafkaAdapter** (`/KafkaAdapter`) - Reusable Kafka integration patterns
- **ReadModel** (`/ReadModel`) - CQRS read projections with MySQL/in-memory storage
- **Player** (`/Player`) - Player service used to manage the Player behaviour

## Key Technologies
- **Vert.x 4.5.14** - Reactive application framework with Kotlin coroutines
- **KCQRS** - Custom CQRS/Event Sourcing framework
- **KurrentDB** - Event Store database for event sourcing
- **Kafka/Redpanda** - Event streaming between services
- **Docker** - Containerized services with docker-compose

## Architecture Patterns
- **Event Sourcing** - All business state changes stored as events in EventStore
- **CQRS** - Commands modify state via aggregates, queries use read models
- **Hexagonal Architecture** - Ports/Adapters pattern with clear domain boundaries
- **Domain-Driven Design** - Bounded contexts for Game and Dealer domains

## Error Handling Mantra
- Diagnose, Don’t Guess: When encountering a bug or failing test, first explain possible causes step-by-step : Check assumptions, inputs, and relevant code paths.
- Graceful Handling: Code should handle errors gracefully. For example, use try/catch around async calls, and return user-friendly error messages or fallback values when appropriate.
- Logging: Include helpful console logs or error logs for critical failures (but avoid log spam in production code).
- No Silent Failures: Do not swallow exceptions silently. Always surface errors either by throwing or logging them.
- On critical bugs, consider binary search through git history or add temporary console.debug statements to isolate the issue.

## Clean Code Guidelines
- Function Size: Aim for functions ≤ 50 lines. If a function is doing too much, break it into smaller helper functions.
- Single Responsibility: Each function/module should have one clear purpose. Don’t lump unrelated logic together.
- Naming: Use descriptive names. Avoid generic names like `tmp`, `data`, `handleStuff`. For example, prefer `calculateInvoiceTotal` over `doCalc`.
- DRY Principle: Do not duplicate code. If similar logic exists in two places, refactor into a shared function (or clarify why both need their own implementation).
- Comments: Explain non-obvious logic, but don’t over-comment self-explanatory code. Remove any leftover debug or commented-out code. 

## Security Guidelines
- Input Validation: Validate all inputs (especially from users or external APIs). Never trust user input – e.g., check for valid email format, string length limits, etc.
- Authentication: Never store passwords in plain text. Use bcrypt with a salt for hashing passwords. Implement account lockout or rate limiting on repeated failed logins.
- Database Safety: Use parameterized queries or an ORM to prevent SQL injection. Do not concatenate user input in SQL queries directly.

## Edge Case Considerations
- Always consider edge and corner cases for any logic:
- Empty or null inputs (e.g., an empty list, missing fields, zero values).
- Max/min values and overflow (e.g., extremely large numbers, very long text).
- Invalid states (e.g., end date before start date, negative quantities).
- Concurrency issues (e.g., two users editing the same data simultaneously).
- If an edge case is identified, handle it in code or at least flag it with a comment/TODO.
- Prefer to fail fast on bad input (throw an error or return a safe default) rather than proceeding with wrong assumptions.

## Workflow & Planning Guidelines
- For any complex or multi-step task, Claude should first output a clear plan or outline of the approach anthropic.com. (E.g., list the steps or modules needed).
- Incremental Development: Implement in logical chunks. After each chunk, verify it aligns with the plan and passes tests before moving on.
- Think Aloud: Use extended reasoning (“think harder or ultrathink”) for complex decisions. It’s okay to spend more tokens to ensure a solid approach rather than rushing coding.
- User Approval: Pause for confirmation after providing a plan or major design decision. Only proceed once the user/developer confirms.
- Error Recovery: If a solution isn’t working, Claude should backtrack and rethink rather than stubbornly persisting. Consider alternative approaches if tests fail or constraints are hit.

## Documentation Management
- Every time the code change you must verify if the documentation has to be updated
- All events have to be keep updated and documented
- All the modules interactions have to be keep updated and well explained
- Maintain a clean map of the relationship
- Each module has to have a clear definition, scope and ownership

## Build & Development Commands

**Build all modules:**
```bash
./gradlew build
```

**Build specific module:**
```bash
./gradlew :Game:build
./gradlew :Dealer:build
```

**Run tests:**
```bash
./gradlew test
./gradlew :Game:test
```

**Run single test:**
```bash
./gradlew :Game:test --tests "*.SpecificTestClass"
```

**Create Docker images:**
```bash
./gradlew :Game:shadowJar
./gradlew :Dealer:shadowJar
```

**Start local environment:**
```bash
docker-compose up -d
```

**Clean build:**
```bash
./gradlew clean build
```


### Game State Machine

Game flows through distinct phases:
1. **GameDraft** - Creating game and adding players
2. **GameWaitingDealer** - Waiting for cards to be dealt
3. **GameExecution** - Active gameplay with turns (PickUp → Play → End phases)
4. **GameTerminated** - Game completed

### Inter-Service Communication

Services communicate via **Kafka events**:
- Game service publishes `CardsRequestedToDealer` 
- Dealer service publishes `CardGivenToDeck` events
- All events are JSON serialized and handled asynchronously

### Configuration

Uses **HOCON format** in `application.conf` files with environment variable overrides:
- REST API ports, Kafka brokers, EventStore connections
- Development defaults in config, production via env vars

### Testing

- **Unit tests** for domain logic using JUnit5
- **Integration tests** with Testcontainers for Kafka/databases
- **Vert.x async testing** with coroutines support
- Rich test factories for domain object creation

### Local Development

1. Start infrastructure: `docker-compose up -d`
2. Build services: `./gradlew build`
3. Game API available at http://localhost:8081
4. Kafka UI at http://localhost:8181
5. EventStore UI at http://localhost:2113

### API Testing

Bruno collection in `/Game/brunoCollection/` provides REST API examples for:
- Creating games
- Adding players
- Starting games
- Requesting card deals

## Burrago Game Rules
### Burraco Game Rules - Claude Code Reference

#### Game Overview
Burraco is a card game for 4 players in fixed partnerships (North-South vs East-West). The objective is to form melds (sets and sequences) and score points by placing them on the table.

#### Game Components

##### Cards
- **Total**: 108 cards (2 standard 52-card decks + 4 jokers)
- **Wild Cards**:
    - 4 Jokers ("jolly") - always wild
    - 8 Twos ("pinelle") - can be wild OR natural in sequences

##### Card Values (for scoring)
```
Joker: 30 points
Two: 20 points  
Ace: 15 points
K, Q, J, 10, 9, 8: 10 points each
7, 6, 5, 4, 3: 5 points each
```

##### Card Rank (high to low)
`Joker > 2 > A > K > Q > J > 10 > 9 > 8 > 7 > 6 > 5 > 4 > 3`

**Suit Rank**: Spades > Hearts > Diamonds > Clubs

#### Game Setup

##### Initial Deal
1. Each player receives **11 cards**
2. Create **2 pozzetti** (face-down piles of 11 cards each)
3. **Draw pile**: 41 cards face-down
4. **Discard pile**: 1 card face-up next to draw pile

##### Dealer Selection
- Draw cards to determine first dealer (lowest card deals)
- Deal rotates clockwise after each hand

#### Core Game Mechanics

##### Melds (Card Combinations)

###### Sets (Combinazione)
- **Definition**: 3+ cards of same rank
- **Wild Card Limit**: Maximum 1 wild card per set
- **Maximum Size**: 9 cards (4 natural + 1 wild)
- **Restriction**: Cannot be all wild cards
- **Team Limit**: Only 1 set per rank per team

**Valid Examples**: `10-10-10`, `4-4-4-4-2`, `K-K-joker`

###### Sequences (Sequenze)
- **Definition**: 3+ consecutive cards of same suit
- **Order**: `(A)-2-3-4-5-6-7-8-9-10-J-Q-K-(A)`
- **Wild Card Limit**: Maximum 1 wild card per sequence
- **Ace Placement**: Either end (above K or below 2), but not both ends
- **Two Usage**: Can be natural (in position) AND/OR wild card

**Valid Examples**: `7♠-8♠-9♠`, `A♥-2♥-3♥-2♣-5♥`, `9♦-2♠-J♦`

###### Burraco
- **Definition**: Meld of 7+ cards
- **Types**:
    - **Clean (Pulito)**: No wild cards = 200 bonus points
    - **Dirty (Sporco)**: Contains wild card = 100 bonus points

##### Turn Structure

###### Player Actions (in order)
1. **Draw**: Take top card from draw pile OR entire discard pile
2. **Meld** (optional): Place valid sets/sequences, extend team's existing melds
3. **Discard**: Place 1 card face-up on discard pile

###### Drawing Rules
- **From Draw Pile**: Take 1 card only
- **From Discard Pile**: Must take ALL cards in pile
- **No Restrictions**: Can always take discard pile (no meld requirement)

##### Pozzetti System

###### Taking a Pozzetto
Each team gets ONE pozzetto when first player runs out of cards.

###### Method 1: Direct Take
1. Draw card
2. Meld ALL cards from hand
3. Take pozzetto immediately
4. Continue playing with pozzetto as new hand

###### Method 2: Take on Discard
1. Draw card
2. Meld all but 1 card
3. Discard final card
4. Take pozzetto face-down (don't look)
5. Wait for partner's turn to complete
6. Then use pozzetto as new hand

##### Wild Card Rules

###### Placement in Sequences
- Wild cards must be placed at **lower end** of sequence
- Example: `2♠-6♥-7♥` (not `6♥-7♥-2♠`)

###### Replacement Rules
- Wild card can only be replaced by **exact natural card it represents**
- When replaced, wild card moves to lower end of sequence
- Natural two can become wild if needed for sequence extension

###### Special Cases
- 13-card sequence + wild card can become 14-card sequence when wild replaced
- Natural two at sequence start can become wild for top extension

##### Game End Conditions

###### Method 1: Going Out (Chiusura)
**Requirements**:
1. Team has taken their pozzetto
2. Team has at least 1 burraco (7+ card meld)
3. Player melds all but 1 card and discards final card
4. Final discard cannot be wild card

**Bonus**: 100 points

###### Method 2: Draw Pile Exhaustion
- Game ends when draw pile has only 2 cards left
- No going out bonus awarded

###### Method 3: Stalemate
- All players repeatedly take single-card discard pile
- No progress possible, game ends

#### Scoring System

##### Point Calculation
```
+ Card values of melds on table
- Card values of cards in hand
+ 200 points per clean burraco
+ 100 points per dirty burraco  
+ 100 points for going out
- 100 points if team hasn't taken pozzetto
```

##### Game Victory
- First team to exceed **2000 points** wins
- Higher score wins if both exceed 2000

#### Legal Move Constraints

##### Forbidden Actions
- Cannot add cards to opponent's melds
- Cannot go out without team burraco
- Cannot go out by discarding wild card
- Cannot discard same card just picked from single-card pile
- Cannot have multiple sets of same rank per team
- Cannot split or join sequences once placed

##### Allowed Actions
- Can discard wild cards (except when going out)
- Can discard any card from multi-card pile pickup
- Can extend sequences in either direction
- Can replace wild cards with natural cards in sequences

#### Three-Player Variation

##### Setup Differences
- Each player gets 11 cards
- **Pozzetti**: 1 pile of 18 cards, 1 pile of 11 cards
- First player to meld all cards takes 18-card pozzetto (plays alone)
- Other two become partners, first to empty hand takes 11-card pozzetto

##### Scoring
- Partners split their combined score
- All other rules remain the same

#### Implementation Notes for Code

##### Key Data Structures Needed
- **Card**: suit, rank, isWild
- **Meld**: cards[], type (SET/SEQUENCE), isClean, isBurraco
- **Player**: hand[], team
- **Game**: drawPile, discardPile, pozzetti[], teams[], currentPlayer

##### Critical Validation Functions
- `isValidSet(cards[])`
- `isValidSequence(cards[])`
- `canGoOut(team)` - check pozzetto taken + has burraco
- `calculateMeldPoints(meld)`
- `isLegalDiscard(card, isGoingOut)`

##### Game State Tracking
- Track which team has taken pozzetto
- Track burraco count per team
- Track wild card positions in sequences
- Enforce single set per rank per team rule