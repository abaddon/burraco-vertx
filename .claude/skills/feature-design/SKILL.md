---
name: feature-design
description: Phase 2 of feature development - Design architecture, events, commands, and service interactions for the Burraco distributed system. Use after running feature-discovery to create the technical design.
---

# Feature Design Skill

This skill creates the technical design for a feature including events, commands, state machines, and service interactions.

## Usage

```
/feature-design <feature-name>
```

## Prerequisites

Run `/feature-discovery` first to understand the feature context.

## Instructions

### Step 1: Design Domain Events

For each state change, define a domain event:

```kotlin
// Template: Domain Event
data class [FeatureName]Event(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: [Aggregate]Identity,
    val field1: Type1,
    val field2: Type2
) : [Aggregate]Event() {
    companion object Factory {
        fun create(aggregateId: [Aggregate]Identity, ...): [FeatureName]Event =
            [FeatureName]Event(
                messageId = UUID.randomUUID(),
                header = EventHeader.create("[aggregate]"),
                aggregateId = aggregateId,
                ...
            )
    }
}
```

List all events needed:

| Event Name | Aggregate | Published By | Consumed By | Kafka Topic |
|------------|-----------|--------------|-------------|-------------|
| [Event1]   | Game      | Game         | Player      | game        |

### Step 2: Design External Events

For cross-service communication via Kafka:

```kotlin
// Template: External Event
data class [FeatureName]ExternalEvent(
    override val aggregateId: [Aggregate]Identity,
    override val messageId: UUID,
    val field1: String,
    val field2: Int
) : [Aggregate]ExternalEvent() {
    companion object {
        fun from(event: [FeatureName]Event): [FeatureName]ExternalEvent =
            [FeatureName]ExternalEvent(
                aggregateId = event.aggregateId,
                messageId = event.messageId,
                field1 = event.field1.toString(),
                field2 = event.field2
            )
    }
}
```

### Step 3: Design Commands

For each user action or event handler:

```kotlin
// Template: Command
data class [ActionName]Command(
    override val aggregateID: [Aggregate]Identity,
    val param1: Type1,
    val param2: Type2
) : Command<[Aggregate]>(aggregateID) {
    override fun execute(currentAggregate: [Aggregate]?): Result<[Aggregate]> = runCatching {
        when (val agg = currentAggregate) {
            is [ExpectedState] -> agg.[action](param1, param2)
            else -> throw UnsupportedOperationException("Invalid state")
        }
    }
}
```

### Step 4: Design State Machine Changes

```markdown
## State Machine Design

### Current States
[List existing states from codebase]

### New States (if any)
- [NewState]: [Description]

### Transitions
[CurrentState] --[Command/Event]--> [NewState]
  Guards: [conditions]
  Events: [events produced]
```

### Step 5: Design Event Flow (Choreography)

```
┌────────┐     ┌────────┐     ┌────────┐     ┌────────┐
│ Client │     │  Game  │     │ Player │     │ Dealer │
└───┬────┘     └───┬────┘     └───┬────┘     └───┬────┘
    │              │              │              │
    │ 1. REST      │              │              │
    ├─────────────>│              │              │
    │              │ 2. Event     │              │
    │              ├─────────────>│              │
    │              │              │              │
    │<─────────────┤              │              │
    │ 3. Response  │              │              │
```

### Step 6: Design REST API

```markdown
### Endpoint: [METHOD] /[path]

**Request**
{
  "field1": "type",
  "field2": 0
}

**Response (200 OK)**
{
  "status": "success"
}

**Error Responses**
| Code | Condition |
|------|-----------|
| 400  | Invalid input |
| 409  | Invalid state |
```

### Step 7: Design Projections

If read models need updates:
- **GameView Changes**: New field `fieldName: Type`
- **PlayerView Changes**: New field `fieldName: Type`

### Step 8: Generate Design Document

```markdown
# Feature Design: [Feature Name]

## 1. Overview
## 2. Events
## 3. Commands
## 4. State Machine
## 5. Event Flow
## 6. REST API
## 7. Projections
## 8. Files to Create/Modify
```

## Patterns Reference

- **Event Naming**: Past tense (`GameCreated`, `CardDealt`)
- **Command Naming**: Imperative (`CreateGame`, `DealCard`)
- **State Naming**: Noun/adjective (`GameDraft`, `PlayerActive`)
