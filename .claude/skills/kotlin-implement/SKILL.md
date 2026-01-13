---
name: kotlin-implement
description: Phase 4 of feature development - Implement commands, REST handlers, Kafka consumers/producers in Kotlin for the Burraco distributed system. Use after domain-modeling to write the implementation code.
---

# Kotlin Implementation Skill

This skill implements the feature code including commands, REST handlers, and Kafka integration.

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
package com.abaddon83.burraco.[service].commands.[state]

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
class [Feature]RoutingHandler(
    private val commandControllerAdapter: CommandControllerAdapter
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun handle(ctx: RoutingContext) {
        CoroutineScope(ctx.vertx().dispatcher()).launch {
            try {
                val gameId = [Context]Identity.create(ctx.pathParam("gameId"))
                val body = ctx.body().asJsonObject()
                val param1 = body.getString("param1")

                requireNotNull(param1) { "param1 is required" }

                val command = [CommandName](aggregateID = gameId, param1 = param1)
                commandControllerAdapter.handle(command)

                ctx.response()
                    .setStatusCode(200)
                    .putHeader("Content-Type", "application/json")
                    .end("""{"status": "success"}""")

            } catch (e: IllegalArgumentException) {
                ctx.response().setStatusCode(400).end("""{"error": "${e.message}"}""")
            } catch (e: UnsupportedOperationException) {
                ctx.response().setStatusCode(409).end("""{"error": "${e.message}"}""")
            } catch (e: Exception) {
                log.error("Error processing [feature]", e)
                ctx.response().setStatusCode(500).end("""{"error": "Internal server error"}""")
            }
        }
    }
}
```

### Step 3: Register Route

In `RestHttpServiceVerticle.kt`:

```kotlin
router.post("/games/:gameId/[feature]")
    .handler(BodyHandler.create())
    .handler { ctx -> [Feature]RoutingHandler(commandControllerAdapter).handle(ctx) }
```

### Step 4: Implement Kafka Event Handler (if consuming events)

```kotlin
class [Feature]EventHandler(
    private val commandControllerAdapter: CommandControllerAdapter
) : KafkaEventHandler<[EventName]ExternalEvent> {

    override val eventClass = [EventName]ExternalEvent::class.java

    override suspend fun handle(event: [EventName]ExternalEvent) {
        val command = [CommandName](
            aggregateID = event.aggregateId,
            param1 = event.field1
        )
        commandControllerAdapter.handle(command)
    }
}
```

### Step 5: Register Event Handler

In Kafka consumer verticle:

```kotlin
router.register([Feature]EventHandler(commandControllerAdapter))
```

### Step 6: Update External Event Publisher (if publishing events)

In `KafkaExternalEventPublisherAdapter.kt`:

```kotlin
override fun publish(event: [Aggregate]Event) {
    when (event) {
        is [EventName] -> publishToKafka([EventName]ExternalEvent.from(event))
        // ... existing cases ...
    }
}
```

### Step 7: Build and Verify

```bash
./gradlew :[Service]:compileKotlin
./gradlew :[Service]:test
./gradlew :[Service]:build
```

### Step 8: Implementation Checklist

```markdown
### Commands
- [ ] Command class created in correct package
- [ ] execute() method validates state
- [ ] Correct state transition on success

### REST Handler (if applicable)
- [ ] Handler class created
- [ ] Input validation implemented
- [ ] Route registered in verticle

### Kafka Handler (if applicable)
- [ ] Handler implements KafkaEventHandler
- [ ] Handler registered in consumer

### Event Publisher (if applicable)
- [ ] External event mapping added
- [ ] Partition key is gameId
```

## Reference Files

- Command: `Game/src/main/kotlin/com/abaddon83/burraco/game/commands/gameDraft/CreateGame.kt`
- REST Handler: `Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/commandController/rest/handlers/NewGameRoutingHandler.kt`
- Kafka Handler: `Game/src/main/kotlin/com/abaddon83/burraco/game/adapters/commandController/kafka/KafkaDealerConsumerVerticle.kt`
