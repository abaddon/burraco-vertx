# Feature Discovery Skill

**Skill**: `/feature-discovery`
**Purpose**: Phase 1 - Analyze feature requirements and assess impact on the distributed system

## Usage

```
/feature-discovery <feature-description>
```

## Instructions

When this skill is invoked, perform a comprehensive analysis:

### Step 1: Parse Feature Requirements

Extract from the feature description:
- **Business Goal**: What problem does this solve?
- **User Story**: As a [role], I want [action], so that [benefit]
- **Acceptance Criteria**: List of testable conditions

### Step 2: Analyze Burraco Game Rules

Reference the Burraco rules in `/CLAUDE.md` to understand:
- Does this feature relate to existing game mechanics?
- What game rules apply?
- Are there edge cases from the rules?

### Step 3: Search for Related Code

Use these search patterns to find related code:

```bash
# Find related aggregates
Grep: pattern="class.*Aggregate" or "sealed class"

# Find existing commands
Grep: pattern="Command<" in Game/src, Player/src, Dealer/src

# Find existing events
Grep: pattern="Event(" in Common/src/main/kotlin

# Find state machine states
Grep: pattern="is Game" or "is Player" or "when.*is"

# Find existing handlers
Grep: pattern="RoutingHandler" or "EventHandler"
```

### Step 4: Map Service Impact

Create an impact matrix:

```markdown
## Impact Matrix

| Service | Impact Level | Changes Required |
|---------|-------------|------------------|
| Game    | [None/Low/Medium/High] | [Description] |
| Player  | [None/Low/Medium/High] | [Description] |
| Dealer  | [None/Low/Medium/High] | [Description] |
| Common  | [None/Low/Medium/High] | [Description] |

### Game Service Impact
- [ ] New command required
- [ ] State machine change required
- [ ] New REST endpoint required
- [ ] Kafka consumer update required
- [ ] External event publisher update required

### Player Service Impact
- [ ] New command required
- [ ] Projection update required
- [ ] Kafka consumer update required
- [ ] REST endpoint update required

### Dealer Service Impact
- [ ] New command required
- [ ] Kafka consumer update required
- [ ] Event publisher update required
```

### Step 5: Identify Dependencies

Search for dependencies:

```bash
# Find what calls/uses the affected components
Grep: pattern="<AffectedClass>" to find references

# Check Kafka topic usage
Grep: pattern="topic.*=" in application.conf files

# Check event flows
Read: Common/src/main/kotlin/com/abaddon83/burraco/common/externalEvents/
```

### Step 6: Assess Risks

Identify potential risks:

```markdown
## Risk Assessment

### Technical Risks
- [ ] Breaking changes to existing APIs
- [ ] Event schema changes (versioning needed)
- [ ] State machine complexity increase
- [ ] Performance implications
- [ ] Data migration required

### Business Risks
- [ ] Game rule violations possible
- [ ] Edge cases not covered
- [ ] Multiplayer consistency issues
```

### Step 7: Generate Discovery Report

Output a complete discovery report:

```markdown
# Feature Discovery Report: [Feature Name]

## 1. Feature Summary
[2-3 sentence description]

## 2. Business Requirements
- Goal: [What this achieves]
- User Story: As a [role], I want [action], so that [benefit]
- Acceptance Criteria:
  1. [Criterion 1]
  2. [Criterion 2]
  3. [Criterion 3]

## 3. Related Game Rules
[Reference specific rules from CLAUDE.md]

## 4. Codebase Analysis

### Existing Related Code
- [File 1]: [What it does]
- [File 2]: [What it does]

### Patterns to Follow
- Similar feature: [existing feature path]
- Command pattern: [example command]
- Event pattern: [example event]

## 5. Impact Assessment
[Impact matrix from Step 4]

## 6. Dependencies
- Upstream: [services that this depends on]
- Downstream: [services that depend on this]

## 7. Risks & Mitigations
[Risk assessment from Step 6]

## 8. Recommendation
- Complexity: [Simple/Medium/Complex]
- Estimated files to modify: [number]
- Services affected: [list]
- Ready for design phase: [Yes/No]
```

## Tools to Use

1. **Grep**: Search for patterns in codebase
2. **Glob**: Find files by pattern
3. **Read**: Read specific files for context
4. **Task with Explore agent**: For complex searches

## Example Searches

```
# Find all game states
Glob: pattern="**/models/game/*.kt"

# Find all commands
Glob: pattern="**/commands/**/*.kt"

# Find all events
Glob: pattern="**/event/**/*.kt"

# Find REST handlers
Grep: pattern="class.*RoutingHandler"

# Find Kafka handlers
Grep: pattern="class.*EventHandler"
```
