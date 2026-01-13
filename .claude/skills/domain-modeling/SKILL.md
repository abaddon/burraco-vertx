---
name: domain-modeling
description: Phase 3 of feature development - Create domain events, external events, value objects, and aggregate changes for the Burraco system. Use after feature-design to implement the domain model.
---

# Domain Modeling Skill

This skill creates the domain model components including events, value objects, and aggregate modifications.

## Usage

```
/domain-modeling <feature-name>
```

## Prerequisites

Run `/feature-design` first to have the design specifications.

## Instructions

### Step 1: Create Domain Events

Location: `Common/src/main/kotlin/com/abaddon83/burraco/common/models/event/[context]/`

```kotlin
// File: [EventName].kt
package com.abaddon83.burraco.common.models.event.[context]

import com.abaddon83.burraco.common.models.[Context]Identity
import com.abaddon83.burraco.common.models.event.EventHeader
import java.util.UUID

data class [EventName](
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: [Context]Identity,
    val specificField: SpecificType
) : [Context]Event() {

    companion object Factory {
        fun create(
            aggregateId: [Context]Identity,
            specificField: SpecificType
        ): [EventName] = [EventName](
            messageId = UUID.randomUUID(),
            header = EventHeader.create("[context]"),
            aggregateId = aggregateId,
            specificField = specificField
        )
    }
}
```

### Step 2: Create External Events

Location: `Common/src/main/kotlin/com/abaddon83/burraco/common/externalEvents/[context]/`

```kotlin
// File: [EventName]ExternalEvent.kt
package com.abaddon83.burraco.common.externalEvents.[context]

data class [EventName]ExternalEvent(
    override val aggregateId: [Context]Identity,
    override val messageId: UUID,
    val specificField: String  // Use primitive types for serialization
) : [Context]ExternalEvent() {

    companion object {
        fun from(event: [EventName]): [EventName]ExternalEvent =
            [EventName]ExternalEvent(
                aggregateId = event.aggregateId,
                messageId = event.messageId,
                specificField = event.specificField.toString()
            )
    }
}
```

### Step 3: Create Value Objects (if needed)

Location: `Common/src/main/kotlin/com/abaddon83/burraco/common/models/`

```kotlin
data class [ValueObject] private constructor(
    val property1: Type1,
    val property2: Type2
) {
    init {
        require(property1.isNotBlank()) { "property1 cannot be blank" }
    }

    companion object {
        fun create(property1: Type1, property2: Type2): [ValueObject] =
            [ValueObject](property1, property2)
    }
}
```

### Step 4: Update Aggregate State Classes

Find the aggregate state class and add methods:

```kotlin
// In existing state class (e.g., GameExecution.kt)

fun [actionName](param1: Type1, param2: Type2): [ResultState] {
    // 1. Validate preconditions
    require(someCondition) { "Precondition not met" }

    // 2. Create and raise event
    val event = [EventName].create(aggregateId, computeValue(param1, param2))

    // 3. Return new state with event applied
    return this.raiseEvent(event) as [ResultState]
}

// Add event application
override fun apply(event: IDomainEvent): [State] = when (event) {
    is [EventName] -> this.apply(event)
    else -> throw UnsupportedOperationException("Event not supported")
}

private fun apply(event: [EventName]): [ResultState] {
    return [ResultState](
        aggregateId = this.aggregateId,
        newField = event.specificField
    )
}
```

### Step 5: Verification Checklist

```markdown
## Domain Model Verification

### Events Created
- [ ] Domain event in `Common/models/event/[context]/`
- [ ] External event in `Common/externalEvents/[context]/`
- [ ] Factory methods with `create()`

### Value Objects Created (if applicable)
- [ ] Immutable data class
- [ ] Private constructor with factory methods
- [ ] Invariant validation in init block

### Aggregate Updates
- [ ] Action method added to correct state class
- [ ] Event creation in action method
- [ ] Event application method added
```

## Reference Files

- Event Example: `Common/src/main/kotlin/com/abaddon83/burraco/common/models/event/game/GameCreated.kt`
- External Event Example: `Common/src/main/kotlin/com/abaddon83/burraco/common/externalEvents/game/GameCreatedExternalEvent.kt`
- Value Object Example: `Common/src/main/kotlin/com/abaddon83/burraco/common/models/card/Card.kt`
- State Class Example: `Game/src/main/kotlin/com/abaddon83/burraco/game/models/game/GameExecution.kt`
