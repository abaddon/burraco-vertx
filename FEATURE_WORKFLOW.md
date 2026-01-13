# Feature Development Workflow for Burraco-Vertx

**Version**: 2.0
**Last Updated**: 2025-01-13

---

## Quick Start - Claude Code Skills

This workflow is implemented as **executable Claude Code skills**. Use these slash commands to run each phase:

### Available Skills

| Skill | Command | Description |
|-------|---------|-------------|
| **Main Workflow** | `/feature-workflow` | Full workflow orchestrator - start here |
| **Phase 1** | `/feature-discovery` | Analyze requirements and assess impact |
| **Phase 2** | `/feature-design` | Design events, commands, and service interactions |
| **Phase 3** | `/domain-modeling` | Create domain events, value objects, aggregates |
| **Phase 4** | `/kotlin-implement` | Implement commands, handlers, Kafka integration |
| **Phase 5** | `/bdd-test` | Create BDD/Cucumber tests and unit tests |
| **Phase 6** | `/integration-docs` | Update documentation and verify integration |

### Usage Examples

```bash
# Start the full workflow for a new feature
/feature-workflow Add ability for players to place melds on the table

# Run individual phases
/feature-discovery Place Meld feature
/feature-design Place Meld
/domain-modeling Place Meld
/kotlin-implement Place Meld
/bdd-test Place Meld
/integration-docs Place Meld
```

### Recommended Flow

1. **Start with `/feature-workflow`** - This will analyze your feature and guide you through all phases
2. **Or run phases individually** - For more control, run each phase skill separately
3. **Each skill provides**:
   - Detailed instructions
   - Code templates
   - Checklists
   - Reference examples from the codebase

### Skill Files Location

All skills are defined in `.claude/skills/`:
- `.claude/settings.json` - Skill configuration
- `.claude/skills/feature-workflow.md` - Main orchestrator
- `.claude/skills/feature-discovery.md` - Phase 1
- `.claude/skills/feature-design.md` - Phase 2
- `.claude/skills/domain-modeling.md` - Phase 3
- `.claude/skills/kotlin-implement.md` - Phase 4
- `.claude/skills/bdd-test.md` - Phase 5
- `.claude/skills/integration-docs.md` - Phase 6

---

## Table of Contents
- [Quick Start](#quick-start---claude-code-skills)
- [Overview](#overview)
- [Agent Architecture](#agent-architecture)
- [Workflow Phases](#workflow-phases)
  - [Phase 1: Feature Discovery & Analysis](#phase-1-feature-discovery--analysis)
  - [Phase 2: Architecture & Design](#phase-2-architecture--design)
  - [Phase 3: Domain Modeling](#phase-3-domain-modeling)
  - [Phase 4: Implementation](#phase-4-implementation)
  - [Phase 5: Testing](#phase-5-testing)
  - [Phase 6: Integration & Documentation](#phase-6-integration--documentation)
- [Agent Specifications](#agent-specifications)
- [LSP Integration](#lsp-integration)
- [Checklists](#checklists)
- [Examples](#examples)

---

## Overview

This document defines a structured workflow for adding new features to the Burraco-Vertx distributed system. The workflow leverages specialized agents, each equipped with domain-specific skills and LSP (Language Server Protocol) capabilities for intelligent code navigation, analysis, and manipulation.

### Core Principles

1. **Separation of Concerns**: Each phase uses the most appropriate specialized agent
2. **Pattern Consistency**: All phases follow established architectural patterns (CQRS, Event Sourcing, Hexagonal)
3. **Test-Driven**: BDD scenarios define acceptance criteria before implementation
4. **Event-First Design**: New features start with event definitions to ensure proper service choreography
5. **LSP-Enabled**: All agents leverage LSP for code intelligence

---

## Agent Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Feature Development Pipeline                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌───────────────┐    ┌───────────────┐    ┌───────────────┐              │
│   │   Discovery   │───>│    Design     │───>│   Domain      │              │
│   │    Agent      │    │    Agent      │    │   Agent       │              │
│   │  (Analysis)   │    │ (Architect)   │    │  (Modeling)   │              │
│   └───────────────┘    └───────────────┘    └───────────────┘              │
│           │                    │                    │                       │
│           │ LSP: Find Refs     │ LSP: Symbols       │ LSP: Type Defs        │
│           │ Pattern Analysis   │ Refactoring        │ Code Generation       │
│           │                    │                    │                       │
│           v                    v                    v                       │
│   ┌───────────────┐    ┌───────────────┐    ┌───────────────┐              │
│   │ Implementation│───>│   Testing     │───>│ Integration   │              │
│   │    Agent      │    │    Agent      │    │    Agent      │              │
│   │   (Kotlin)    │    │    (BDD)      │    │    (Docs)     │              │
│   └───────────────┘    └───────────────┘    └───────────────┘              │
│           │                    │                    │                       │
│           │ LSP: Diagnostics   │ LSP: Test Refs     │ LSP: Documentation    │
│           │ Completion         │ Step Navigation    │ Cross-Reference       │
│           │                    │                    │                       │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Workflow Phases

### Phase 1: Feature Discovery & Analysis

**Agent**: `DiscoveryAgent`
**Skills**: Pattern Recognition, Codebase Analysis, Impact Assessment
**LSP Features**: Find References, Go to Definition, Workspace Symbols

#### Objectives
1. Understand the feature requirements
2. Identify affected services and modules
3. Map dependencies and data flows
4. Assess impact on existing functionality

#### Activities

```
┌─────────────────────────────────────────────────────────────────┐
│                    Discovery Agent Tasks                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. REQUIREMENT ANALYSIS                                        │
│     ├── Parse feature specification                             │
│     ├── Identify business rules (Burraco game rules)            │
│     └── Extract acceptance criteria                             │
│                                                                 │
│  2. CODEBASE EXPLORATION (LSP-assisted)                         │
│     ├── lsp.findReferences(relatedAggregates)                   │
│     ├── lsp.getWorkspaceSymbols(domainConcepts)                 │
│     └── lsp.getCallHierarchy(existingFlows)                     │
│                                                                 │
│  3. PATTERN IDENTIFICATION                                      │
│     ├── Identify similar existing features                      │
│     ├── Map to CQRS patterns (Command/Query)                    │
│     ├── Identify Event Sourcing implications                    │
│     └── Check Hexagonal Architecture boundaries                 │
│                                                                 │
│  4. IMPACT ASSESSMENT                                           │
│     ├── List affected services: [Game, Player, Dealer]          │
│     ├── Identify state machine transitions                      │
│     ├── Map Kafka topic requirements                            │
│     └── Assess breaking changes                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Outputs
- **Feature Analysis Document**: Detailed breakdown of requirements
- **Impact Matrix**: Services × Changes mapping
- **Dependency Graph**: Visual representation of affected components
- **Risk Assessment**: Potential issues and mitigations

#### LSP Queries Used
```kotlin
// Find all related state transitions
lsp.findReferences("GameExecution")

// Discover existing command patterns
lsp.getWorkspaceSymbols("*Command*")

// Trace event flows
lsp.getCallHierarchy("publish", Direction.INCOMING)
```

---

### Phase 2: Architecture & Design

**Agent**: `ArchitectAgent`
**Skills**: System Design, Pattern Application, Event Storming
**LSP Features**: Workspace Symbols, Type Hierarchy, Refactoring Preview

#### Objectives
1. Design the feature architecture
2. Define service interactions
3. Specify event flows
4. Plan state machine changes

#### Activities

```
┌─────────────────────────────────────────────────────────────────┐
│                    Architect Agent Tasks                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. EVENT STORMING                                              │
│     ├── Define domain events (immutable facts)                  │
│     ├── Define external events (Kafka messages)                 │
│     ├── Sequence event choreography                             │
│     └── Validate eventual consistency                           │
│                                                                 │
│  2. COMMAND DESIGN                                              │
│     ├── Define command parameters                               │
│     ├── Specify validation rules                                │
│     ├── Map command → aggregate state transitions               │
│     └── Design idempotency strategy                             │
│                                                                 │
│  3. STATE MACHINE DESIGN                                        │
│     ├── Define new states (if any)                              │
│     ├── Define transitions with guards                          │
│     ├── Validate against Burraco rules                          │
│     └── Document state invariants                               │
│                                                                 │
│  4. SERVICE INTERACTION DESIGN                                  │
│     ├── Define Kafka topics/partitions                          │
│     ├── Specify consumer groups                                 │
│     ├── Design projection updates                               │
│     └── Plan REST API changes                                   │
│                                                                 │
│  5. PATTERN APPLICATION                                         │
│     ├── Apply CQRS (Command vs Query paths)                     │
│     ├── Apply Event Sourcing (event-first)                      │
│     ├── Apply Hexagonal (ports/adapters)                        │
│     └── Apply DDD (bounded contexts)                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Design Artifacts

**Event Sequence Diagram**
```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  Client  │     │   Game   │     │  Dealer  │     │  Player  │
└────┬─────┘     └────┬─────┘     └────┬─────┘     └────┬─────┘
     │                │               │               │
     │ REST Request   │               │               │
     ├───────────────>│               │               │
     │                │ DomainEvent   │               │
     │                ├─────────────> │               │
     │                │               │               │
     │                │               │ ExternalEvent │
     │                │               ├──────────────>│
     │                │<──────────────┤               │
     │                │ ResponseEvent │               │
     │<───────────────┤               │               │
     │ HTTP Response  │               │               │
```

**State Transition Specification**
```kotlin
// State: GameExecution
sealed class GameExecutionPhase {
    object PickUpPhase : GameExecutionPhase()
    object PlayPhase : GameExecutionPhase()
    object EndPhase : GameExecutionPhase()
    // NEW: object NewFeaturePhase : GameExecutionPhase()
}

// Transitions
PickUpPhase --[PickUpCard]--> PlayPhase
PlayPhase --[PlaceMeld]--> PlayPhase
PlayPhase --[EndTurn]--> EndPhase
EndPhase --[NextPlayer]--> PickUpPhase
```

#### Outputs
- **Architecture Decision Record (ADR)**: Design decisions and rationale
- **Event Flow Diagram**: Visual event choreography
- **State Machine Diagram**: Updated state transitions
- **API Specification**: OpenAPI/REST endpoint changes
- **Implementation Plan**: Ordered task list

---

### Phase 3: Domain Modeling

**Agent**: `DomainModelingAgent`
**Skills**: DDD Modeling, Value Object Design, Aggregate Design
**LSP Features**: Type Definition, Go to Implementation, Rename Symbol

#### Objectives
1. Define domain events
2. Design value objects
3. Model aggregate changes
4. Ensure domain integrity

#### Activities

```
┌─────────────────────────────────────────────────────────────────┐
│                 Domain Modeling Agent Tasks                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. EVENT DEFINITION (Common module)                            │
│     ├── Create domain event class                               │
│     │   └── Location: Common/models/event/{context}/            │
│     ├── Create external event class                             │
│     │   └── Location: Common/externalEvents/{context}/          │
│     └── Define serialization (Jackson modules)                  │
│                                                                 │
│  2. VALUE OBJECT DESIGN                                         │
│     ├── Identify new value objects                              │
│     ├── Ensure immutability                                     │
│     ├── Implement equality/hashCode                             │
│     └── Add factory methods                                     │
│                                                                 │
│  3. AGGREGATE MODIFICATION                                      │
│     ├── Add state handling methods                              │
│     ├── Implement event application                             │
│     ├── Define invariant checks                                 │
│     └── Update state transitions                                │
│                                                                 │
│  4. IDENTITY CLASSES (if needed)                                │
│     ├── Create identity value object                            │
│     ├── UUID generation strategy                                │
│     └── Serialization support                                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Code Templates

**Domain Event Template**
```kotlin
// Location: Common/src/main/kotlin/com/abaddon83/burraco/common/models/event/game/

data class NewFeatureEvent(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val featureSpecificData: FeatureData
) : GameEvent() {

    companion object Factory {
        fun create(
            aggregateId: GameIdentity,
            data: FeatureData
        ): NewFeatureEvent = NewFeatureEvent(
            messageId = UUID.randomUUID(),
            header = EventHeader.create("game"),
            aggregateId = aggregateId,
            featureSpecificData = data
        )
    }
}
```

**External Event Template**
```kotlin
// Location: Common/src/main/kotlin/com/abaddon83/burraco/common/externalEvents/game/

data class NewFeatureExternalEvent(
    override val aggregateId: GameIdentity,
    override val messageId: UUID,
    val data: FeatureDataDTO
) : GameExternalEvent() {

    companion object {
        fun from(event: NewFeatureEvent): NewFeatureExternalEvent =
            NewFeatureExternalEvent(
                aggregateId = event.aggregateId,
                messageId = event.messageId,
                data = FeatureDataDTO.from(event.featureSpecificData)
            )
    }
}
```

#### Outputs
- **Event Classes**: Domain and external events
- **Value Objects**: Immutable domain concepts
- **Aggregate Updates**: Modified state classes
- **Serialization Modules**: Jackson configuration

---

### Phase 4: Implementation

**Agent**: `KotlinImplementationAgent`
**Skills**: Kotlin/Vert.x Development, Coroutines, Reactive Patterns
**LSP Features**: Diagnostics, Code Completion, Quick Fix, Inline Hints

#### Objectives
1. Implement commands
2. Create REST handlers
3. Implement Kafka consumers/producers
4. Apply proper error handling

#### Activities

```
┌─────────────────────────────────────────────────────────────────┐
│               Kotlin Implementation Agent Tasks                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. COMMAND IMPLEMENTATION                                      │
│     ├── Create command class                                    │
│     │   └── Location: {Module}/commands/{state}/                │
│     ├── Implement execute() method                              │
│     ├── Add validation logic                                    │
│     └── Return Result<NewState>                                 │
│                                                                 │
│  2. AGGREGATE STATE HANDLING                                    │
│     ├── Add apply(event) method for new events                  │
│     ├── Update state machine transitions                        │
│     ├── Implement business logic                                │
│     └── Ensure immutable state changes                          │
│                                                                 │
│  3. REST API HANDLER                                            │
│     ├── Create routing handler class                            │
│     │   └── Location: {Module}/adapters/.../rest/handlers/      │
│     ├── Implement request validation                            │
│     ├── Call CommandControllerAdapter                           │
│     ├── Handle errors gracefully                                │
│     └── Return proper HTTP responses                            │
│                                                                 │
│  4. KAFKA INTEGRATION                                           │
│     ├── Update event router (add new event type)                │
│     ├── Create event handler                                    │
│     ├── Update external event publisher                         │
│     └── Ensure proper partition key (gameId)                    │
│                                                                 │
│  5. PROJECTION UPDATES (if needed)                              │
│     ├── Update projection handler                               │
│     ├── Add new fields to view models                           │
│     └── Update repository queries                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Code Templates

**Command Implementation**
```kotlin
// Location: Game/src/main/kotlin/com/abaddon83/burraco/game/commands/gameExecution/

data class NewFeatureCommand(
    override val aggregateID: GameIdentity,
    val featureParam: FeatureParam
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Result<Game> = runCatching {
        when (currentAggregate) {
            is GameExecution -> currentAggregate.executeNewFeature(featureParam)
            else -> throw UnsupportedOperationException(
                "Command not valid for state: ${currentAggregate?.javaClass?.simpleName}"
            )
        }
    }
}
```

**REST Handler Implementation**
```kotlin
// Location: Game/src/main/kotlin/.../adapters/commandController/rest/handlers/

class NewFeatureRoutingHandler(
    private val commandControllerAdapter: CommandControllerAdapter
) : CoroutineRoutingHandler {

    override suspend fun handle(ctx: RoutingContext) {
        try {
            val gameId = GameIdentity.create(ctx.pathParam("gameId"))
            val request = ctx.body().asJsonObject().mapTo(NewFeatureRequest::class.java)

            // Validate request
            validateRequest(request)

            // Execute command
            val command = NewFeatureCommand(gameId, request.toParam())
            commandControllerAdapter.handle(command)

            // Return success
            ctx.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end(JsonObject().put("status", "success").encode())

        } catch (e: ValidationException) {
            ctx.response().setStatusCode(400).end(e.toJson())
        } catch (e: Exception) {
            log.error("Error processing new feature", e)
            ctx.response().setStatusCode(500).end()
        }
    }
}
```

**Kafka Event Handler**
```kotlin
// Location: Game/src/main/kotlin/.../adapters/commandController/kafka/

class NewFeatureEventHandler(
    private val commandControllerAdapter: CommandControllerAdapter
) : KafkaEventHandler<NewFeatureExternalEvent> {

    override val eventClass = NewFeatureExternalEvent::class.java

    override suspend fun handle(event: NewFeatureExternalEvent) {
        log.info("Processing NewFeatureExternalEvent: ${event.messageId}")

        val command = NewFeatureCommand(
            aggregateID = event.aggregateId,
            featureParam = event.data.toParam()
        )

        commandControllerAdapter.handle(command)
    }
}
```

#### LSP-Assisted Development
```kotlin
// LSP provides real-time assistance:

// 1. Diagnostics - catch errors during typing
lsp.onDiagnostics { diagnostic ->
    if (diagnostic.severity == ERROR) highlightLine(diagnostic.range)
}

// 2. Code Completion - suggest members
lsp.getCompletions(position) // Returns: execute, aggregateID, etc.

// 3. Quick Fix - auto-import, implement interface
lsp.getCodeActions(diagnostic) // Returns: Add import, Implement method

// 4. Inline Hints - show inferred types
lsp.getInlayHints(range) // Shows: `: Result<Game>`
```

#### Outputs
- **Command Classes**: Business logic implementation
- **State Transitions**: Updated aggregate methods
- **REST Handlers**: HTTP endpoint implementations
- **Kafka Handlers**: Event processing logic
- **Projection Updates**: Read model changes

---

### Phase 5: Testing

**Agent**: `BDDTestingAgent`
**Skills**: Cucumber/Gherkin, Step Definitions, Test Containers
**LSP Features**: Go to Step Definition, Find Step Usages, Test Navigation

#### Objectives
1. Write BDD acceptance scenarios
2. Implement step definitions
3. Create unit tests
4. Verify integration

#### Activities

```
┌─────────────────────────────────────────────────────────────────┐
│                   BDD Testing Agent Tasks                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. FEATURE FILE CREATION                                       │
│     ├── Write Gherkin scenarios                                 │
│     │   └── Location: e2eTest/src/test/resources/features/      │
│     ├── Define Given-When-Then steps                            │
│     ├── Include edge cases                                      │
│     └── Cover error scenarios                                   │
│                                                                 │
│  2. STEP DEFINITIONS                                            │
│     ├── Create step definition class                            │
│     │   └── Location: e2eTest/src/test/kotlin/.../steps/        │
│     ├── Implement Given steps (setup)                           │
│     ├── Implement When steps (action)                           │
│     ├── Implement Then steps (verification)                     │
│     └── Use TestContext for state sharing                       │
│                                                                 │
│  3. UNIT TESTS                                                  │
│     ├── Create test specification                               │
│     │   └── Location: {Module}/src/test/kotlin/.../             │
│     ├── Follow Given_When_Then naming                           │
│     ├── Test command execution                                  │
│     ├── Verify events produced                                  │
│     └── Test error conditions                                   │
│                                                                 │
│  4. INTEGRATION TESTS                                           │
│     ├── Test Kafka event flow                                   │
│     ├── Use Testcontainers                                      │
│     ├── Verify cross-service communication                      │
│     └── Test eventual consistency                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Test Templates

**Gherkin Feature File**
```gherkin
# Location: e2eTest/src/test/resources/features/new-feature.feature

Feature: New Feature Functionality
  As a Burraco player
  I want to use the new feature
  So that I can enhance my gameplay experience

  Background:
    Given a game with 2 players is created
    And the game has been started with cards dealt

  Scenario: Successfully execute new feature
    Given the current player has the required cards
    When the player executes the new feature action
    Then the feature effect should be applied
    And the game state should be updated accordingly

  Scenario: Reject new feature when conditions not met
    Given the current player does not have required cards
    When the player attempts to execute the new feature action
    Then the action should be rejected with error message "Insufficient cards"
    And the game state should remain unchanged

  Scenario Outline: New feature with various inputs
    Given the player has <card_count> cards
    When the player executes new feature with <parameter>
    Then the result should be <expected_result>

    Examples:
      | card_count | parameter | expected_result |
      | 3          | valid     | success         |
      | 2          | valid     | error           |
      | 3          | invalid   | error           |
```

**Step Definitions**
```kotlin
// Location: e2eTest/src/test/kotlin/com/abaddon83/burraco/e2e/steps/

class NewFeatureStepDefinitions(private val context: TestContext) {

    private val httpClient = context.httpClient
    private val kafkaHelper = context.kafkaHelper

    @Given("the current player has the required cards")
    fun givenPlayerHasRequiredCards() {
        // Setup: Ensure player has necessary cards via context
        val playerView = httpClient.getPlayerView(
            context.currentPlayerId,
            context.gameId
        )
        assertThat(playerView.cards).hasSizeGreaterThanOrEqualTo(3)
    }

    @When("the player executes the new feature action")
    fun whenPlayerExecutesNewFeature() {
        val request = NewFeatureRequest(
            playerId = context.currentPlayerId,
            parameter = "test-value"
        )

        context.lastResponse = httpClient.post(
            "/games/${context.gameId}/new-feature",
            request
        )
    }

    @Then("the feature effect should be applied")
    fun thenFeatureEffectApplied() {
        assertThat(context.lastResponse.statusCode).isEqualTo(200)

        // Verify via Kafka events
        val events = kafkaHelper.consumeEvents<NewFeatureExternalEvent>(
            topic = "game",
            timeout = Duration.ofSeconds(10)
        )

        assertThat(events).anyMatch {
            it.aggregateId == context.gameId
        }
    }

    @Then("the game state should be updated accordingly")
    fun thenGameStateUpdated() {
        // Wait for eventual consistency
        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            val playerView = httpClient.getPlayerView(
                context.currentPlayerId,
                context.gameId
            )
            assertThat(playerView.featureApplied).isTrue()
        }
    }
}
```

**Unit Test Specification**
```kotlin
// Location: Game/src/test/kotlin/com/abaddon83/burraco/game/commands/

internal class Given_GameExecution_When_NewFeature_Then_FeatureApplied :
    KcqrsAggregateTestSpecification<Game>() {

    private val gameId = GameIdentity.create()
    private val playerId = PlayerIdentity.create()

    override val aggregateId = gameId

    override fun emptyAggregate() = { GameDraft.empty() }

    override fun given(): List<IDomainEvent> = listOf(
        GameCreated.create(gameId),
        PlayerAdded.create(gameId, playerId, "Player1", Team.TEAM1),
        // ... other setup events
        GameStarted.create(gameId, playerId, Team.TEAM1)
    )

    override fun `when`(): ICommand<Game> = NewFeatureCommand(
        aggregateID = gameId,
        featureParam = FeatureParam("test-value")
    )

    override fun expected(): List<IDomainEvent> = listOf(
        NewFeatureEvent.create(gameId, FeatureData("test-value"))
    )

    override fun expectedException(): Exception? = null
}

internal class Given_WrongState_When_NewFeature_Then_Error :
    KcqrsAggregateTestSpecification<Game>() {

    private val gameId = GameIdentity.create()

    override val aggregateId = gameId
    override fun emptyAggregate() = { GameDraft.empty() }

    override fun given(): List<IDomainEvent> = listOf(
        GameCreated.create(gameId)
        // Game is in Draft state, not Execution
    )

    override fun `when`(): ICommand<Game> = NewFeatureCommand(
        aggregateID = gameId,
        featureParam = FeatureParam("test-value")
    )

    override fun expected(): List<IDomainEvent> = listOf() // No events

    override fun expectedException() = UnsupportedOperationException::class.java
}
```

#### Test Execution Commands
```bash
# Run all unit tests
./gradlew test

# Run specific module tests
./gradlew :Game:test --tests "*NewFeature*"

# Run E2E tests with fresh Docker images (recommended)
./gradlew :e2eTest:e2eTestClean

# Run E2E tests using existing images
./gradlew :e2eTest:test

# Run specific feature
./gradlew :e2eTest:test --tests "*" -Dcucumber.filter.tags="@new-feature"
```

#### Outputs
- **Feature Files**: Gherkin acceptance scenarios
- **Step Definitions**: Cucumber step implementations
- **Unit Tests**: Command/aggregate test specifications
- **Integration Tests**: Kafka flow verification
- **Test Reports**: JUnit/Cucumber HTML reports

---

### Phase 6: Integration & Documentation

**Agent**: `IntegrationAgent`
**Skills**: Documentation, API Specs, System Integration
**LSP Features**: Cross-Reference, Symbol Documentation, Hover Information

#### Objectives
1. Update module documentation
2. Update API specifications
3. Verify cross-service integration
4. Complete feature delivery

#### Activities

```
┌─────────────────────────────────────────────────────────────────┐
│                  Integration Agent Tasks                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. DOCUMENTATION UPDATES                                       │
│     ├── Update module CLAUDE.md files                           │
│     ├── Add event to event catalog                              │
│     ├── Update service interaction diagrams                     │
│     └── Document new REST endpoints                             │
│                                                                 │
│  2. API SPECIFICATION                                           │
│     ├── Update Bruno collection                                 │
│     │   └── Location: Game/brunoCollection/                     │
│     ├── Add request/response examples                           │
│     └── Document error codes                                    │
│                                                                 │
│  3. INTEGRATION VERIFICATION                                    │
│     ├── Run full E2E test suite                                 │
│     ├── Verify Kafka topic flows                                │
│     ├── Check projection consistency                            │
│     └── Validate error handling                                 │
│                                                                 │
│  4. CODE REVIEW PREPARATION                                     │
│     ├── Generate change summary                                 │
│     ├── List affected files                                     │
│     ├── Document design decisions                               │
│     └── Prepare PR description                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Documentation Templates

**Event Catalog Entry**
```markdown
### NewFeatureEvent

**Type**: Domain Event
**Module**: Game
**Published To**: Kafka topic `game`

#### Payload
| Field | Type | Description |
|-------|------|-------------|
| messageId | UUID | Unique event identifier |
| header | EventHeader | Event metadata |
| aggregateId | GameIdentity | Game identifier |
| featureSpecificData | FeatureData | Feature-specific payload |

#### Triggered By
- Command: `NewFeatureCommand`
- REST: `POST /games/{gameId}/new-feature`

#### Consumed By
- Player service: Updates PlayerView projection
- (Other consumers as applicable)
```

**API Documentation**
```markdown
### POST /games/{gameId}/new-feature

Execute the new feature action for a game.

#### Request
```json
{
  "playerId": "uuid",
  "parameter": "string"
}
```

#### Response (200 OK)
```json
{
  "status": "success",
  "result": {
    "featureApplied": true
  }
}
```

#### Errors
| Code | Description |
|------|-------------|
| 400 | Invalid request parameters |
| 404 | Game not found |
| 409 | Invalid game state |
| 500 | Internal server error |
```

#### Outputs
- **Updated Documentation**: Module CLAUDE.md files
- **API Collection**: Bruno/Postman updates
- **Event Catalog**: New event documentation
- **PR Description**: Ready for code review

---

## Agent Specifications

### Agent Configuration Matrix

| Agent | Primary Skills | LSP Features | Tools |
|-------|---------------|--------------|-------|
| **DiscoveryAgent** | Pattern Analysis, Impact Assessment | Find References, Workspace Symbols, Call Hierarchy | Grep, Glob, Read |
| **ArchitectAgent** | System Design, Event Storming, DDD | Type Hierarchy, Symbols, Rename | Read, Diagrams |
| **DomainModelingAgent** | DDD, Value Objects, Events | Type Definition, Go to Implementation | Read, Write, Edit |
| **KotlinImplementationAgent** | Kotlin, Vert.x, Coroutines | Diagnostics, Completion, Quick Fix, Inline Hints | Read, Write, Edit, Bash |
| **BDDTestingAgent** | Cucumber, Gherkin, TDD | Go to Step, Find Step Usages | Read, Write, Edit, Bash |
| **IntegrationAgent** | Documentation, API Specs | Cross-Reference, Hover | Read, Write, Edit |

### Agent Skill Profiles

```yaml
DiscoveryAgent:
  skills:
    - pattern_recognition:
        patterns: [CQRS, EventSourcing, Hexagonal, DDD]
    - codebase_analysis:
        search: [grep, glob, ast]
    - impact_assessment:
        scope: [services, events, states]
  lsp:
    - textDocument/references
    - workspace/symbol
    - callHierarchy/incomingCalls
    - callHierarchy/outgoingCalls

ArchitectAgent:
  skills:
    - event_storming:
        artifacts: [domain_events, external_events, flows]
    - system_design:
        patterns: [choreography, saga, cqrs]
    - state_machine_design:
        notation: [mermaid, plantuml]
  lsp:
    - textDocument/typeHierarchy
    - workspace/symbol
    - textDocument/rename

DomainModelingAgent:
  skills:
    - ddd_modeling:
        concepts: [aggregate, entity, value_object, event]
    - kotlin_idioms:
        features: [data_class, sealed_class, companion]
    - serialization:
        frameworks: [jackson, kotlinx]
  lsp:
    - textDocument/definition
    - textDocument/implementation
    - textDocument/rename

KotlinImplementationAgent:
  skills:
    - kotlin_development:
        features: [coroutines, extension_functions, dsl]
    - vertx_patterns:
        components: [verticle, router, handler]
    - reactive_programming:
        patterns: [async, non_blocking, backpressure]
  lsp:
    - textDocument/diagnostic
    - textDocument/completion
    - textDocument/codeAction
    - textDocument/inlayHint
    - textDocument/formatting

BDDTestingAgent:
  skills:
    - cucumber_bdd:
        artifacts: [feature, scenario, step_definition]
    - test_patterns:
        patterns: [given_when_then, arrange_act_assert]
    - testcontainers:
        containers: [kafka, mysql, eventstore]
  lsp:
    - textDocument/definition  # Go to step definition
    - textDocument/references  # Find step usages
    - workspace/symbol        # Find all steps

IntegrationAgent:
  skills:
    - documentation:
        formats: [markdown, openapi, mermaid]
    - api_specification:
        tools: [bruno, postman, openapi]
    - verification:
        methods: [e2e, smoke, integration]
  lsp:
    - textDocument/hover
    - textDocument/documentLink
    - workspace/symbol
```

---

## LSP Integration

All agents leverage LSP (Language Server Protocol) for intelligent code assistance. The LSP server provides:

### LSP Capabilities Used

```
┌─────────────────────────────────────────────────────────────────┐
│                     LSP Feature Matrix                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  NAVIGATION                                                     │
│  ├── textDocument/definition     - Go to definition             │
│  ├── textDocument/declaration    - Go to declaration            │
│  ├── textDocument/implementation - Go to implementation         │
│  ├── textDocument/references     - Find all references          │
│  └── textDocument/typeDefinition - Go to type definition        │
│                                                                 │
│  INTELLIGENCE                                                   │
│  ├── textDocument/completion     - Code completion              │
│  ├── textDocument/hover          - Hover information            │
│  ├── textDocument/signatureHelp  - Parameter hints              │
│  └── textDocument/inlayHint      - Inline type hints            │
│                                                                 │
│  DIAGNOSTICS                                                    │
│  ├── textDocument/diagnostic     - Real-time error checking     │
│  ├── textDocument/codeAction     - Quick fixes                  │
│  └── textDocument/codeLens       - Inline actions               │
│                                                                 │
│  REFACTORING                                                    │
│  ├── textDocument/rename         - Rename symbol                │
│  ├── textDocument/formatting     - Code formatting              │
│  └── textDocument/rangeFormatting- Format selection             │
│                                                                 │
│  WORKSPACE                                                      │
│  ├── workspace/symbol            - Find symbols                 │
│  ├── workspace/workspaceFolders  - Multi-root support           │
│  └── workspace/configuration     - Settings sync                │
│                                                                 │
│  HIERARCHY                                                      │
│  ├── textDocument/typeHierarchy  - Type hierarchy               │
│  ├── callHierarchy/incoming      - Who calls this               │
│  └── callHierarchy/outgoing      - What does this call          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### LSP Integration per Phase

#### Phase 1: Discovery
```kotlin
// Find all references to GameExecution
lsp.textDocument.references(
    uri = "file:///Game/.../models/game/GameExecution.kt",
    position = Position(10, 6)  // class GameExecution
)
// Returns: All files/locations referencing GameExecution

// Find all command classes
lsp.workspace.symbol(query = "*Command")
// Returns: CreateGame, AddPlayer, PickUpCard, etc.
```

#### Phase 2: Design
```kotlin
// Get type hierarchy for Game aggregate
lsp.textDocument.typeHierarchy(
    uri = "file:///Game/.../models/game/Game.kt",
    position = Position(5, 6)
)
// Returns: Game -> GameDraft, GameExecution, GameTerminated

// Preview rename impact
lsp.textDocument.prepareRename(
    uri = "file:///Common/.../event/game/GameEvent.kt",
    position = Position(3, 10)
)
// Returns: Range and placeholder for rename
```

#### Phase 3: Domain Modeling
```kotlin
// Go to implementation of Event interface
lsp.textDocument.implementation(
    uri = "file:///Common/.../event/game/GameEvent.kt",
    position = Position(1, 15)  // interface IDomainEvent
)
// Returns: All event implementations

// Type definition for serialization
lsp.textDocument.typeDefinition(
    uri = "file:///Common/.../event/game/GameCreated.kt",
    position = Position(8, 20)  // EventHeader
)
```

#### Phase 4: Implementation
```kotlin
// Real-time diagnostics during coding
lsp.textDocument.diagnostic(
    uri = "file:///Game/.../commands/NewFeatureCommand.kt"
)
// Returns: Errors, warnings, hints

// Code completion
lsp.textDocument.completion(
    uri = "file:///Game/.../commands/NewFeatureCommand.kt",
    position = Position(15, 20),
    context = { triggerKind = TriggerCharacter, triggerCharacter = "." }
)
// Returns: Available methods, properties

// Quick fix suggestions
lsp.textDocument.codeAction(
    uri = "file:///Game/.../commands/NewFeatureCommand.kt",
    range = Range(Position(10, 0), Position(10, 50)),
    context = { diagnostics = [...] }
)
// Returns: Import suggestions, implement interface, etc.
```

#### Phase 5: Testing
```kotlin
// Go to step definition from feature file
lsp.textDocument.definition(
    uri = "file:///e2eTest/.../features/new-feature.feature",
    position = Position(10, 10)  // "the player executes"
)
// Returns: Location in NewFeatureStepDefinitions.kt

// Find all usages of a step
lsp.textDocument.references(
    uri = "file:///e2eTest/.../steps/NewFeatureStepDefinitions.kt",
    position = Position(20, 15)  // @When annotation
)
// Returns: All feature files using this step
```

#### Phase 6: Integration
```kotlin
// Generate documentation hover
lsp.textDocument.hover(
    uri = "file:///Game/.../rest/handlers/NewFeatureRoutingHandler.kt",
    position = Position(5, 10)
)
// Returns: KDoc/documentation for symbol

// Find related documentation
lsp.textDocument.documentLink(
    uri = "file:///Game/CLAUDE.md"
)
// Returns: All links to related files
```

---

## Checklists

### Pre-Implementation Checklist

```markdown
## Feature: [Feature Name]

### Discovery Phase
- [ ] Feature requirements documented
- [ ] Affected services identified
- [ ] Impact assessment completed
- [ ] Dependencies mapped
- [ ] Risks identified

### Design Phase
- [ ] Event flow designed
- [ ] State transitions defined
- [ ] Commands specified
- [ ] API endpoints designed
- [ ] ADR created

### Domain Modeling Phase
- [ ] Domain events created in Common
- [ ] External events created in Common
- [ ] Value objects defined
- [ ] Aggregate changes designed
- [ ] Serialization verified
```

### Implementation Checklist

```markdown
### Implementation Phase

#### Game Service (if applicable)
- [ ] Command class created
- [ ] Aggregate state handling added
- [ ] REST handler implemented
- [ ] Kafka consumer updated
- [ ] External event publisher updated
- [ ] Unit tests written

#### Player Service (if applicable)
- [ ] Command class created
- [ ] Projection handler updated
- [ ] Kafka consumer updated
- [ ] Unit tests written

#### Dealer Service (if applicable)
- [ ] Command class created
- [ ] Event handler updated
- [ ] Unit tests written
```

### Testing Checklist

```markdown
### Testing Phase
- [ ] Gherkin feature file created
- [ ] Step definitions implemented
- [ ] Unit tests pass (./gradlew test)
- [ ] Integration tests pass
- [ ] E2E tests pass (./gradlew :e2eTest:e2eTestClean)
- [ ] Edge cases covered
- [ ] Error scenarios tested
```

### Integration Checklist

```markdown
### Integration Phase
- [ ] Module CLAUDE.md updated
- [ ] Main CLAUDE.md updated
- [ ] Event catalog updated
- [ ] Bruno collection updated
- [ ] Diagrams updated
- [ ] Full E2E suite passes
- [ ] Code reviewed
- [ ] PR created with proper description
```

---

## Examples

### Example: Adding "Place Meld" Feature

Here's a complete walkthrough of adding the "Place Meld" (Tris/Straight) feature:

#### Phase 1: Discovery

```
DiscoveryAgent Analysis:
├── Feature: Allow players to place melds (Tris/Straight) on the table
├── Affected Services:
│   ├── Game: New command, state transition, REST endpoint
│   ├── Player: Projection update, card removal
│   └── Dealer: No changes (only deals initial cards)
├── Dependencies:
│   ├── Player must be in PlayPhase
│   ├── Cards must form valid meld (3+ cards)
│   └── Team meld validation (one Tris per rank)
└── Impact:
    ├── New events: MeldPlaced, TrisCreated, StraightCreated
    ├── New state: GameExecution tracks melds per team
    └── New REST: POST /games/{gameId}/melds
```

#### Phase 2: Design

```
ArchitectAgent Design:

Event Flow:
┌────────┐          ┌────────┐          ┌────────┐
│ Client │          │  Game  │          │ Player │
└───┬────┘          └───┬────┘          └───┬────┘
    │ POST /melds       │                   │
    ├──────────────────>│                   │
    │                   │ MeldPlaced        │
    │                   ├──────────────────>│
    │                   │                   │ Update PlayerView
    │<──────────────────┤                   │ (remove cards)
    │ 200 OK            │                   │

State Machine:
GameExecution.PlayPhase --[PlaceMeld]--> GameExecution.PlayPhase
                        --[EndTurn]----> GameExecution.EndPhase

Command: PlaceMeld
├── Input: gameId, playerId, cards[]
├── Validation: Valid meld, player owns cards, correct phase
└── Output: MeldPlaced event
```

#### Phase 3: Domain Modeling

```kotlin
// Common/models/event/game/MeldPlaced.kt
data class MeldPlaced(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerId: PlayerIdentity,
    val meldType: MeldType,
    val cards: List<Card>
) : GameEvent()

// Common/externalEvents/game/MeldPlacedExternalEvent.kt
data class MeldPlacedExternalEvent(
    override val aggregateId: GameIdentity,
    override val messageId: UUID,
    val playerId: PlayerIdentity,
    val meldType: String,
    val cards: List<CardDTO>
) : GameExternalEvent()
```

#### Phase 4: Implementation

```kotlin
// Game/commands/gameExecution/PlaceMeld.kt
data class PlaceMeld(
    override val aggregateID: GameIdentity,
    val playerId: PlayerIdentity,
    val cards: List<Card>
) : Command<Game>(aggregateID) {
    override fun execute(currentAggregate: Game?): Result<Game> = runCatching {
        when (val game = currentAggregate) {
            is GameExecutionPlayPhase -> game.placeMeld(playerId, cards)
            else -> throw UnsupportedOperationException("Must be in PlayPhase")
        }
    }
}
```

#### Phase 5: Testing

```gherkin
# e2eTest/resources/features/place-meld.feature
Feature: Place Meld
  Scenario: Player places a valid Tris
    Given a game in play phase
    And the current player has cards "10♠,10♥,10♦"
    When the player places a meld with those cards
    Then a Tris meld should be created
    And the player should no longer have those cards
```

```kotlin
// Unit test
class Given_PlayPhase_When_PlaceMeld_Then_MeldPlaced :
    KcqrsAggregateTestSpecification<Game>() {

    override fun given() = listOf(/* setup events */)
    override fun `when`() = PlaceMeld(gameId, playerId, cards)
    override fun expected() = listOf(MeldPlaced.create(gameId, playerId, TRIS, cards))
}
```

#### Phase 6: Integration

```markdown
// Game/CLAUDE.md update
## New Feature: Place Meld

### REST Endpoint
POST /games/{gameId}/melds

### Events
- MeldPlaced: Published when a player places a valid meld

### State Transitions
PlayPhase --[PlaceMeld]--> PlayPhase
```

---

## Summary

This workflow provides a structured approach to feature development in the Burraco-Vertx distributed system. Key principles:

1. **Agent Specialization**: Each phase uses the most appropriate agent with specific skills
2. **LSP Integration**: All agents leverage LSP for intelligent code assistance
3. **Pattern Consistency**: CQRS, Event Sourcing, and Hexagonal patterns guide all implementations
4. **Test-Driven**: BDD scenarios define acceptance before implementation
5. **Event-First**: New features start with event design for proper choreography
6. **Documentation**: Every change updates relevant documentation

Following this workflow ensures consistent, well-tested, and documented features that integrate smoothly with the existing distributed architecture.
