---
name: integration-docs
description: Phase 6 of feature development - Update documentation and verify integration for the Burraco system. Use after bdd-test to complete the feature with proper documentation.
---

# Integration & Documentation Skill

This skill updates documentation and verifies the complete integration of the feature.

## Usage

```
/integration-docs <feature-name>
```

## Prerequisites

Run `/bdd-test` first to verify the feature works correctly.

## Instructions

### Step 1: Update Module CLAUDE.md Files

#### Game Service (`Game/CLAUDE.md`)

```markdown
## [Feature Name]

### Overview
[Brief description]

### REST Endpoint
`[METHOD] /games/{gameId}/[feature]`

**Request Body**
{"field1": "type", "field2": 0}

**Response (200)**
{"status": "success"}

### Commands
- `[CommandName]`: [What it does]

### Events
- `[EventName]`: [When published, who consumes]

### State Machine
[From State] --[[CommandName]]--> [To State]
```

### Step 2: Update Main CLAUDE.md

Add to Service Interactions section:

```markdown
### [Feature Name] Flow

┌────────┐     ┌────────┐     ┌────────┐
│ Client │     │  Game  │     │ Player │
└───┬────┘     └───┬────┘     └───┬────┘
    │              │              │
    │ POST /[feat] │              │
    ├─────────────>│              │
    │              │ [EventName]  │
    │              ├─────────────>│
    │<─────────────┤              │
    │   200 OK     │              │
```

### Step 3: Update Event Catalog

```markdown
### [EventName]

| Property | Type | Description |
|----------|------|-------------|
| messageId | UUID | Unique identifier |
| aggregateId | Identity | Aggregate ID |
| [field1] | [Type1] | [Description] |

**Triggered By**: `[CommandName]`
**Published To**: Kafka topic `[topic]`
**Consumed By**: [Services]
```

### Step 4: Update Bruno Collection

Location: `Game/brunoCollection/`

```
# File: [Feature Name].bru
meta {
  name: [Feature Name]
  type: http
}

post {
  url: {{baseUrl}}/games/:gameId/[feature]
  body: json
}

body:json {
  {"field1": "value", "field2": 42}
}
```

### Step 5: Run Full E2E Suite

```bash
./gradlew clean build
./gradlew :e2eTest:e2eTestClean
```

### Step 6: Integration Verification

```markdown
## Integration Checklist

### Kafka Event Flow
- [ ] Event published to correct topic
- [ ] Correct partition key (gameId)
- [ ] Consumer processes event

### REST API
- [ ] Endpoint accessible
- [ ] Request validation works
- [ ] Error handling appropriate

### Projections
- [ ] Events update projections
- [ ] Queries return updated data
```

### Step 7: Generate Change Summary

```markdown
## Feature: [Feature Name]

### Summary
[2-3 sentence description]

### New Files
- `Common/.../event/game/[EventName].kt`
- `Game/.../commands/[CommandName].kt`
- `e2eTest/.../features/[feature].feature`

### Modified Files
- `Game/.../models/game/[State].kt`
- `Game/CLAUDE.md`
- `CLAUDE.md`

### Testing
- [X] Unit tests pass
- [X] E2E tests pass
```

### Step 8: Final Checklist

```markdown
## Documentation
- [ ] Game/CLAUDE.md updated
- [ ] Player/CLAUDE.md updated (if applicable)
- [ ] Main CLAUDE.md updated
- [ ] Bruno collection updated

## Integration
- [ ] ./gradlew clean build passes
- [ ] ./gradlew :e2eTest:e2eTestClean passes
- [ ] Services start without errors
```

## Commit Message Template

```
feat([scope]): [Short description]

[Longer description]

Changes:
- Add [EventName] event
- Implement [CommandName] command
- Add REST endpoint POST /games/{gameId}/[feature]

Testing:
- Unit tests for command execution
- E2E tests for full flow
```
