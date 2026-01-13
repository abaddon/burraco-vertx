# Domain Modeling Skill

**Skill**: `/domain-modeling`
**Purpose**: Phase 3 - Create domain events, value objects, and aggregate changes

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
    // Add feature-specific fields here
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

import com.abaddon83.burraco.common.models.[Context]Identity
import com.abaddon83.burraco.common.models.event.[context].[EventName]
import java.util.UUID

data class [EventName]ExternalEvent(
    override val aggregateId: [Context]Identity,
    override val messageId: UUID,
    // Use primitive/DTO types for serialization
    val specificField: String
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
// File: [ValueObject].kt
package com.abaddon83.burraco.common.models

/**
 * [Description of what this value object represents]
 */
data class [ValueObject] private constructor(
    val property1: Type1,
    val property2: Type2
) {
    init {
        // Invariant checks
        require(property1.isNotBlank()) { "property1 cannot be blank" }
        require(property2 > 0) { "property2 must be positive" }
    }

    companion object {
        fun create(property1: Type1, property2: Type2): [ValueObject] =
            [ValueObject](property1, property2)

        // Factory methods for common cases
        fun empty(): [ValueObject] = [ValueObject]("default", 1)
    }

    // Domain behavior methods
    fun withUpdatedProperty1(newValue: Type1): [ValueObject] =
        copy(property1 = newValue)
}
```

### Step 4: Create Identity Classes (if needed)

Location: `Common/src/main/kotlin/com/abaddon83/burraco/common/models/`

```kotlin
// File: [Entity]Identity.kt
package com.abaddon83.burraco.common.models

import com.abaddon83.kcqrs.core.domain.AggregateID
import java.util.UUID

data class [Entity]Identity(private val id: UUID) : AggregateID {

    override fun valueAsString(): String = id.toString()

    companion object {
        fun create(): [Entity]Identity = [Entity]Identity(UUID.randomUUID())

        fun create(uuid: UUID): [Entity]Identity = [Entity]Identity(uuid)

        fun create(uuidString: String): [Entity]Identity =
            [Entity]Identity(UUID.fromString(uuidString))
    }
}
```

### Step 5: Update Aggregate State Classes

Find the aggregate state class and add methods:

```kotlin
// In existing state class (e.g., GameExecution.kt)

// Add method to handle the new action
fun [actionName](param1: Type1, param2: Type2): [ResultState] {
    // 1. Validate preconditions
    require(someCondition) { "Precondition not met" }

    // 2. Create and raise event
    val event = [EventName].create(
        aggregateId = this.aggregateId,
        specificField = computeValue(param1, param2)
    )

    // 3. Return new state with event applied
    return this.raiseEvent(event) as [ResultState]
}

// Add event application in apply() method
override fun apply(event: IDomainEvent): [State] = when (event) {
    // ... existing cases ...
    is [EventName] -> this.apply(event)
    else -> throw UnsupportedOperationException("Event not supported: ${event::class}")
}

private fun apply(event: [EventName]): [ResultState] {
    // Transform state based on event data
    return [ResultState](
        aggregateId = this.aggregateId,
        // ... updated fields ...
        newField = event.specificField
    )
}
```

### Step 6: Update Jackson Serialization Module

Location: Check existing modules in `[Service]/src/main/kotlin/.../adapters/.../models/`

```kotlin
// Add to existing serialization module or create new one
class [Feature]SerializationModule : SimpleModule() {
    init {
        // Register serializers for new types
        addSerializer([ValueObject]::class.java, [ValueObject]Serializer())
        addDeserializer([ValueObject]::class.java, [ValueObject]Deserializer())
    }
}

// Or add @JsonCreator/@JsonValue annotations to value objects
data class [ValueObject](
    @JsonValue val value: String
) {
    companion object {
        @JsonCreator
        @JvmStatic
        fun fromJson(value: String): [ValueObject] = [ValueObject](value)
    }
}
```

### Step 7: Verification Checklist

Before moving to implementation:

```markdown
## Domain Model Verification

### Events Created
- [ ] Domain event in `Common/models/event/[context]/`
- [ ] External event in `Common/externalEvents/[context]/`
- [ ] Factory methods with `create()`
- [ ] Proper inheritance from base event class

### Value Objects Created (if applicable)
- [ ] Immutable data class
- [ ] Private constructor with factory methods
- [ ] Invariant validation in init block
- [ ] Domain behavior methods

### Identity Classes Created (if applicable)
- [ ] Extends `AggregateID`
- [ ] UUID-based implementation
- [ ] Factory methods for creation and parsing

### Aggregate Updates
- [ ] Action method added to correct state class
- [ ] Event creation in action method
- [ ] Event application method added
- [ ] State transition returns correct new state

### Serialization
- [ ] Jackson annotations or custom serializers
- [ ] External events use primitive types
- [ ] Identity classes serialize to/from strings
```

### Step 8: Files to Create

Generate the list of files:

```markdown
## Files to Create

### Common Module
1. `Common/src/main/kotlin/com/abaddon83/burraco/common/models/event/game/[EventName].kt`
2. `Common/src/main/kotlin/com/abaddon83/burraco/common/externalEvents/game/[EventName]ExternalEvent.kt`
3. (Optional) `Common/src/main/kotlin/com/abaddon83/burraco/common/models/[ValueObject].kt`

### Service Module (Game/Player/Dealer)
4. Modify: `[Service]/src/main/kotlin/.../models/[Aggregate]/[State].kt`
```

## Reference: Existing Patterns

### Event Example
Read: `Common/src/main/kotlin/com/abaddon83/burraco/common/models/event/game/GameCreated.kt`

### External Event Example
Read: `Common/src/main/kotlin/com/abaddon83/burraco/common/externalEvents/game/GameCreatedExternalEvent.kt`

### Value Object Example
Read: `Common/src/main/kotlin/com/abaddon83/burraco/common/models/card/Card.kt`

### State Class Example
Read: `Game/src/main/kotlin/com/abaddon83/burraco/game/models/game/GameExecution.kt`
