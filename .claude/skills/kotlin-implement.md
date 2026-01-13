# Kotlin Implementation Skill

**Skill**: `/kotlin-implement`
**Purpose**: Phase 4 - Implement commands, handlers, and Kafka integration in Kotlin

## Usage

```
/kotlin-implement <feature-name>
```

## Prerequisites

Run `/domain-modeling` first to have events and value objects created.

## Instructions

### Step 1: Implement Commands

Location: `[Service]/src/main/kotlin/com/abaddon83/burraco/[service]/commands/[state]/`

```kotlin
// File: [CommandName].kt
package com.abaddon83.burraco.[service].commands.[state]

import com.abaddon83.burraco.common.models.[Context]Identity
import com.abaddon83.burraco.[service].models.[aggregate].[Aggregate]
import com.abaddon83.burraco.[service].models.[aggregate].[TargetState]
import com.abaddon83.kcqrs.core.domain.commands.Command

data class [CommandName](
    override val aggregateID: [Context]Identity,
    val param1: Type1,
    val param2: Type2
) : Command<[Aggregate]>(aggregateID) {

    override fun execute(currentAggregate: [Aggregate]?): Result<[Aggregate]> = runCatching {
        when (val agg = currentAggregate) {
            is [ValidState] -> agg.[action](param1, param2)
            null -> throw IllegalStateException("Aggregate not found: $aggregateID")
            else -> throw UnsupportedOperationException(
                "[CommandName] not valid for state: ${currentAggregate::class.simpleName}"
            )
        }
    }
}
```

### Step 2: Implement REST Handler

Location: `[Service]/src/main/kotlin/.../adapters/commandController/rest/handlers/`

```kotlin
// File: [Feature]RoutingHandler.kt
package com.abaddon83.burraco.[service].adapters.commandController.rest.handlers

import com.abaddon83.burraco.common.models.[Context]Identity
import com.abaddon83.burraco.[service].adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.[service].commands.[state].[CommandName]
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class [Feature]RoutingHandler(
    private val commandControllerAdapter: CommandControllerAdapter
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun handle(ctx: RoutingContext) {
        CoroutineScope(ctx.vertx().dispatcher()).launch {
            try {
                // 1. Extract path parameters
                val gameId = [Context]Identity.create(ctx.pathParam("gameId"))

                // 2. Parse request body
                val body = ctx.body().asJsonObject()
                val param1 = body.getString("param1")
                val param2 = body.getInteger("param2")

                // 3. Validate input
                requireNotNull(param1) { "param1 is required" }
                require(param2 > 0) { "param2 must be positive" }

                // 4. Create and execute command
                val command = [CommandName](
                    aggregateID = gameId,
                    param1 = param1,
                    param2 = param2
                )

                commandControllerAdapter.handle(command)

                // 5. Return success response
                ctx.response()
                    .setStatusCode(200)
                    .putHeader("Content-Type", "application/json")
                    .end("""{"status": "success"}""")

            } catch (e: IllegalArgumentException) {
                log.warn("Validation error: ${e.message}")
                ctx.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end("""{"error": "${e.message}"}""")
            } catch (e: UnsupportedOperationException) {
                log.warn("Invalid state: ${e.message}")
                ctx.response()
                    .setStatusCode(409)
                    .putHeader("Content-Type", "application/json")
                    .end("""{"error": "${e.message}"}""")
            } catch (e: Exception) {
                log.error("Error processing [feature]", e)
                ctx.response()
                    .setStatusCode(500)
                    .putHeader("Content-Type", "application/json")
                    .end("""{"error": "Internal server error"}""")
            }
        }
    }
}
```

### Step 3: Register Route

Find and update the REST verticle:

```kotlin
// In RestHttpServiceVerticle.kt, add to router setup:

// Add new route
router.post("/games/:gameId/[feature]")
    .handler(BodyHandler.create())
    .handler { ctx -> [Feature]RoutingHandler(commandControllerAdapter).handle(ctx) }
```

### Step 4: Implement Kafka Event Handler (if consuming events)

Location: `[Service]/src/main/kotlin/.../adapters/commandController/kafka/`

```kotlin
// File: [Feature]EventHandler.kt
package com.abaddon83.burraco.[service].adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.[context].[EventName]ExternalEvent
import com.abaddon83.burraco.[service].adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.[service].commands.[state].[CommandName]
import org.slf4j.LoggerFactory

class [Feature]EventHandler(
    private val commandControllerAdapter: CommandControllerAdapter
) : KafkaEventHandler<[EventName]ExternalEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override val eventClass = [EventName]ExternalEvent::class.java

    override suspend fun handle(event: [EventName]ExternalEvent) {
        log.info("Processing [EventName]ExternalEvent: ${event.messageId}")

        try {
            val command = [CommandName](
                aggregateID = event.aggregateId,
                param1 = event.field1,
                param2 = event.field2
            )

            commandControllerAdapter.handle(command)
            log.info("Successfully processed [EventName]: ${event.messageId}")

        } catch (e: Exception) {
            log.error("Failed to process [EventName]: ${event.messageId}", e)
            throw e // Let Kafka retry mechanism handle
        }
    }
}
```

### Step 5: Register Event Handler

Update the Kafka consumer verticle:

```kotlin
// In Kafka[Source]ConsumerVerticle.kt, add to router:

router.register([Feature]EventHandler(commandControllerAdapter))
```

### Step 6: Update External Event Publisher (if publishing events)

Location: `[Service]/src/main/kotlin/.../adapters/externalEventPublisher/kafka/`

```kotlin
// In KafkaExternalEventPublisherAdapter.kt, add case:

override fun publish(event: [Aggregate]Event) {
    when (event) {
        // ... existing cases ...
        is [EventName] -> publishToKafka([EventName]ExternalEvent.from(event))
    }
}
```

### Step 7: Update Projections (if needed)

Location: `Player/src/main/kotlin/.../projection/[view]/`

```kotlin
// In [View]ProjectionHandler.kt, add handler:

suspend fun handle(event: [EventName]ExternalEvent) {
    val currentView = repository.find(event.aggregateId)

    if (currentView != null) {
        val updatedView = currentView.copy(
            newField = event.fieldValue
        )
        repository.save(updatedView)
        log.info("Updated [View] for ${event.aggregateId}")
    }
}

// In router registration:
router.register(object : KafkaEventHandler<[EventName]ExternalEvent> {
    override val eventClass = [EventName]ExternalEvent::class.java
    override suspend fun handle(event: [EventName]ExternalEvent) {
        projectionHandler.handle(event)
    }
})
```

### Step 8: Build and Verify

Run these commands to verify the implementation:

```bash
# Compile the module
./gradlew :[Service]:compileKotlin

# Run unit tests
./gradlew :[Service]:test

# Check for compilation errors
./gradlew :[Service]:build
```

### Step 9: Implementation Checklist

```markdown
## Implementation Verification

### Commands
- [ ] Command class created in correct package
- [ ] execute() method validates state
- [ ] Correct state transition on success
- [ ] Proper exception on invalid state

### REST Handler (if applicable)
- [ ] Handler class created
- [ ] Input validation implemented
- [ ] Command execution via adapter
- [ ] Proper HTTP status codes
- [ ] Error handling with logging
- [ ] Route registered in verticle

### Kafka Handler (if applicable)
- [ ] Handler class implements KafkaEventHandler
- [ ] Event class specified correctly
- [ ] Command created from event data
- [ ] Error handling allows retry
- [ ] Handler registered in consumer

### Event Publisher (if applicable)
- [ ] External event mapping added
- [ ] Kafka topic is correct
- [ ] Partition key is gameId

### Projections (if applicable)
- [ ] View model updated with new fields
- [ ] Projection handler processes event
- [ ] Repository save works correctly

### Build Verification
- [ ] ./gradlew :[Service]:compileKotlin passes
- [ ] ./gradlew :[Service]:test passes
- [ ] No runtime errors in logs
```

## Common Patterns

### Error Handling
```kotlin
// Use Result for expected failures
fun riskyOperation(): Result<Data> = runCatching {
    // operation that might fail
}

// Use exceptions for unexpected failures
throw IllegalStateException("This should never happen")
```

### Coroutine Usage
```kotlin
// In Vert.x handlers
CoroutineScope(ctx.vertx().dispatcher()).launch {
    // async code here
}

// Suspending functions
suspend fun asyncOperation(): Result {
    // can call other suspend functions
}
```

### Logging
```kotlin
private val log = LoggerFactory.getLogger(this::class.java)

log.info("Processing event: ${event.messageId}")
log.warn("Validation failed: ${e.message}")
log.error("Unexpected error", e)
```

## Reference Files

### Command Example
Read: `Game/src/main/kotlin/com/abaddon83/burraco/game/commands/gameDraft/CreateGame.kt`

### REST Handler Example
Read: `Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/commandController/rest/handlers/NewGameRoutingHandler.kt`

### Kafka Handler Example
Read: `Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/commandController/kafka/KafkaDealerConsumerVerticle.kt`

### Projection Handler Example
Read: `Player/src/main/kotlin/com/abaddon83/burraco/player/projection/playerview/PlayerViewProjectionHandler.kt`
