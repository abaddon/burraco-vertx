# Integration & Documentation Skill

**Skill**: `/integration-docs`
**Purpose**: Phase 6 - Update documentation and verify integration

## Usage

```
/integration-docs <feature-name>
```

## Prerequisites

Run `/bdd-test` first to verify the feature works correctly.

## Instructions

### Step 1: Update Module CLAUDE.md Files

Update the relevant module documentation:

#### Game Service (`Game/CLAUDE.md`)

```markdown
## [Feature Name]

### Overview
[Brief description of what the feature does]

### REST Endpoint
`[METHOD] /games/{gameId}/[feature]`

**Request Body**
```json
{
  "field1": "type",
  "field2": 0
}
```

**Response (200)**
```json
{
  "status": "success"
}
```

**Errors**
- 400: Invalid request
- 409: Invalid game state
- 500: Server error

### Commands
- `[CommandName]`: [What it does]

### Events
- `[EventName]`: [When it's published, who consumes it]

### State Machine
[From State] --[[CommandName]]--> [To State]
```

#### Player Service (`Player/CLAUDE.md`)

```markdown
## [Feature Name] Impact

### Projection Updates
- `[ViewName]`: Added field `[fieldName]` to track [what]

### Event Consumption
- Consumes `[EventName]` from `[topic]` topic
```

### Step 2: Update Main CLAUDE.md

Add to the Service Interactions section:

```markdown
### [Feature Name] Flow

```
┌────────┐     ┌────────┐     ┌────────┐
│ Client │     │  Game  │     │ Player │
└───┬────┘     └───┬────┘     └───┬────┘
    │              │              │
    │ POST /[feat] │              │
    ├─────────────>│              │
    │              │ [EventName]  │
    │              ├─────────────>│
    │              │              │ Update projection
    │<─────────────┤              │
    │   200 OK     │              │
```
```

### Step 3: Update Event Catalog

Add to the events documentation in `CLAUDE.md`:

```markdown
### [EventName]

| Property | Type | Description |
|----------|------|-------------|
| messageId | UUID | Unique event identifier |
| header | EventHeader | Event metadata |
| aggregateId | [Context]Identity | Aggregate identifier |
| [field1] | [Type1] | [Description] |
| [field2] | [Type2] | [Description] |

**Triggered By**: `[CommandName]` command
**Published To**: Kafka topic `[topic]`
**Consumed By**: [Service names]
```

### Step 4: Update Bruno Collection

Location: `Game/brunoCollection/`

Create a new request file:

```
# File: [Feature Name].bru
meta {
  name: [Feature Name]
  type: http
  seq: [number]
}

post {
  url: {{baseUrl}}/games/:gameId/[feature]
  body: json
  auth: none
}

params:path {
  gameId: {{gameId}}
}

body:json {
  {
    "field1": "value",
    "field2": 42
  }
}

docs {
  # [Feature Name]

  [Description of what this endpoint does]

  ## Prerequisites
  - Game must be created
  - Game must be in [required state]

  ## Response
  Returns 200 on success
}
```

### Step 5: Run Full E2E Suite

Verify everything works together:

```bash
# Clean build all services
./gradlew clean build

# Build Docker images
./gradlew :e2eTest:buildDockerImages

# Run full E2E test suite
./gradlew :e2eTest:e2eTestClean

# Check all tests pass
echo "E2E Test Results:"
cat e2eTest/build/reports/tests/test/index.html | grep -A5 "summary"
```

### Step 6: Verify Cross-Service Integration

Check these integration points:

```markdown
## Integration Verification

### Kafka Event Flow
- [ ] Event published to correct topic
- [ ] Correct partition key (gameId)
- [ ] Consumer group processes event
- [ ] Event serialization/deserialization works

### REST API
- [ ] Endpoint accessible
- [ ] Request validation works
- [ ] Response format correct
- [ ] Error handling appropriate

### Projections
- [ ] Events update projections
- [ ] Queries return updated data
- [ ] Eventual consistency within timeout

### Event Store
- [ ] Events persisted correctly
- [ ] Aggregate can be reconstructed
- [ ] Idempotency on replay
```

### Step 7: Generate Change Summary

Create a summary for PR/commit:

```markdown
## Feature: [Feature Name]

### Summary
[2-3 sentence description of the feature]

### Changes

#### New Files
- `Common/.../event/game/[EventName].kt` - Domain event
- `Common/.../externalEvents/game/[EventName]ExternalEvent.kt` - External event
- `Game/.../commands/[CommandName].kt` - Command implementation
- `Game/.../handlers/[Handler].kt` - REST handler
- `e2eTest/.../features/[feature].feature` - BDD scenarios
- `e2eTest/.../steps/[Feature]StepDefinitions.kt` - Step definitions

#### Modified Files
- `Game/.../models/game/[State].kt` - Added [method]
- `Game/.../rest/RestHttpServiceVerticle.kt` - Added route
- `Game/CLAUDE.md` - Documentation
- `CLAUDE.md` - Event catalog update

### Testing
- [X] Unit tests pass
- [X] E2E tests pass
- [X] Manual testing completed

### Documentation
- [X] Module CLAUDE.md updated
- [X] Main CLAUDE.md updated
- [X] Bruno collection updated
```

### Step 8: Final Checklist

```markdown
## Documentation Checklist

### Code Documentation
- [ ] KDoc comments on public classes/methods
- [ ] Event fields documented
- [ ] Command parameters documented

### Module Documentation
- [ ] Game/CLAUDE.md updated (if applicable)
- [ ] Player/CLAUDE.md updated (if applicable)
- [ ] Dealer/CLAUDE.md updated (if applicable)

### Main Documentation
- [ ] CLAUDE.md event catalog updated
- [ ] CLAUDE.md service interactions updated
- [ ] Event flow diagram added

### API Documentation
- [ ] Bruno collection updated
- [ ] Request/response examples included
- [ ] Error codes documented

### Test Documentation
- [ ] Feature file has clear descriptions
- [ ] Test scenarios cover all cases
- [ ] Test report generated

## Integration Checklist

### Build Verification
- [ ] ./gradlew clean build passes
- [ ] ./gradlew :e2eTest:e2eTestClean passes
- [ ] No compilation warnings

### Runtime Verification
- [ ] Services start without errors
- [ ] Kafka topics receive events
- [ ] Projections update correctly
- [ ] REST endpoints respond correctly

### Consistency Verification
- [ ] Event sourcing works (can rebuild aggregate)
- [ ] Projections eventually consistent
- [ ] No lost events
- [ ] Idempotency maintained
```

## Reference Files

### CLAUDE.md Structure
Read: `/CLAUDE.md` sections for patterns

### Module Documentation
Read: `Game/CLAUDE.md`, `Player/CLAUDE.md`, `Dealer/CLAUDE.md`

### Bruno Collection
Read: `Game/brunoCollection/` for existing request patterns

## Commit Message Template

```
feat([scope]): [Short description]

[Longer description of what the feature does]

Changes:
- Add [EventName] event for [purpose]
- Implement [CommandName] command
- Add REST endpoint POST /games/{gameId}/[feature]
- Update [projection] with [new field]

Testing:
- Unit tests for command execution
- E2E tests for full flow

Documentation:
- Update module CLAUDE.md
- Add event to catalog
- Update Bruno collection
```
