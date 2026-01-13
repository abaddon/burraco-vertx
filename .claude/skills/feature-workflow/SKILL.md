---
name: feature-workflow
description: Main workflow orchestrator for adding new features to the distributed Burraco card game system. Use this when implementing new game features across Game, Player, and Dealer services following CQRS, Event Sourcing, and Hexagonal Architecture patterns.
---

# Feature Workflow Orchestrator

This skill orchestrates the full feature development workflow for the Burraco distributed system.

## Usage

```
/feature-workflow <feature-description>
```

## Instructions

When this skill is invoked, follow these steps:

### Step 1: Understand the Feature Request

Parse the user's feature description and identify:
- What the feature does
- Which game rules it relates to (see Burraco rules in CLAUDE.md)
- Expected user interactions

### Step 2: Create a Todo List

Use the TodoWrite tool to create a structured task list:

```
1. Discovery: Analyze feature and assess impact
2. Design: Define events and service interactions
3. Domain Modeling: Create events and value objects
4. Implementation: Write commands and handlers
5. Testing: Create BDD and unit tests
6. Integration: Update documentation
```

### Step 3: Execute Phases Sequentially

For each phase, you have two options:

**Option A: Manual Phase Execution**
Guide the user through each phase, providing analysis and recommendations.

**Option B: Invoke Individual Skills**
Tell the user they can run individual phase skills:
- `/feature-discovery` - Phase 1
- `/feature-design` - Phase 2
- `/domain-modeling` - Phase 3
- `/kotlin-implement` - Phase 4
- `/bdd-test` - Phase 5
- `/integration-docs` - Phase 6

### Step 4: Generate Feature Summary

After understanding the feature, generate this summary:

```markdown
## Feature: [Name]

### Affected Services
- Game: [Yes/No - describe changes]
- Player: [Yes/No - describe changes]
- Dealer: [Yes/No - describe changes]

### New Events Required
- [EventName]: [Description]

### New Commands Required
- [CommandName]: [Description]

### New REST Endpoints
- [METHOD] [Path]: [Description]

### State Machine Changes
- [From State] --[Action]--> [To State]

### Estimated Complexity
- [ ] Simple (1 service, no new events)
- [ ] Medium (1-2 services, new events)
- [ ] Complex (all services, state machine changes)
```

### Step 5: Confirm with User

Before proceeding to implementation, confirm the analysis with the user:
- Ask if the feature summary is correct
- Ask which phases to execute
- Ask about any additional requirements

## Project Context

This is a distributed Burraco card game with:
- **Game Service**: REST API, game state machine, CQRS commands
- **Player Service**: Player lifecycle, read projections
- **Dealer Service**: Card dealing (event-driven only)
- **Communication**: Kafka events (choreography pattern)
- **Persistence**: Event Store (KurrentDB)

## Key Patterns to Apply

1. **CQRS**: Separate commands (writes) from queries (reads)
2. **Event Sourcing**: All state changes as immutable events
3. **Hexagonal Architecture**: Ports and adapters pattern
4. **DDD**: Aggregates, value objects, bounded contexts

## Files to Reference

- `/CLAUDE.md` - Full project documentation and Burraco rules
- `/Game/CLAUDE.md` - Game service details
- `/Player/CLAUDE.md` - Player service details
- `/Dealer/CLAUDE.md` - Dealer service details
