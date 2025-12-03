# KafkaAdapter Module Documentation

## Module Overview
- **Purpose**: Provides reusable, standardized Kafka integration patterns for all services.
- **Scope**: Contains abstract Verticles and helper classes for consuming and producing Kafka events.
- **Key Components**:
    - `KafkaConsumerVerticle`: Abstract base for consuming events.
    - `KafkaProducerVerticle`: Abstract base for producing events.
    - `EventRouterHandler`: Routes incoming Kafka records to specific handlers.

## Architecture

### 1. Consumer Architecture

The consumer architecture is designed to be robust, supporting pause/resume backpressure and ordered processing.

- **`KafkaConsumerVerticle`**:
    - Abstract class extending `AbstractVerticle`.
    - Manages the `KafkaConsumer` lifecycle (subscribe, unsubscribe).
    - Implements a **pause/resume** pattern:
        - Pauses consumption before processing a record.
        - Processes record on a worker thread (via `executeBlocking`).
        - Commits offset after successful processing.
        - Resumes consumption only after commit (or failure handling).
    - **Usage**: Extend this class and implement `loadHandlers()`.

- **`EventRouterHandler`**:
    - Dispatches incoming `KafkaConsumerRecord` to the appropriate `KafkaEventHandler` based on the event name.
    - Handles missing handlers gracefully (logs warning).

- **`KafkaEventHandler`**:
    - Interface for handling specific event types.

#### Usage Example (Consumer)

```kotlin
class MyServiceConsumerVerticle(
    kafkaConfig: KafkaConsumerConfig
) : KafkaConsumerVerticle(kafkaConfig) {

    override fun loadHandlers(): EventRouterHandler {
        val router = EventRouterHandler()
        router.addHandler(MySpecificEventHandler())
        return router
    }
}
```

### 2. Producer Architecture

The producer architecture simplifies publishing domain events as integration events.

- **`KafkaProducerVerticle`**:
    - Abstract class for publishing events.
    - Generic types: `DE` (Domain Event), `AR` (Aggregate Root).
    - **`publishOnKafka(aggregate, domainEvent)`**:
        - Converts domain event to `ExternalEvent` (via `chooseExternalEvent`).
        - Wraps it in a `KafkaEvent` envelope.
        - Publishes to the configured topic.
        - Awaits acknowledgement.

#### Usage Example (Producer)

```kotlin
class MyServiceProducerVerticle(
    producer: KafkaProducer<String, String>,
    topic: String
) : KafkaProducerVerticle<MyDomainEvent, MyAggregate>(producer, topic) {

    override fun chooseExternalEvent(aggregate: MyAggregate, domainEvent: MyDomainEvent): ExternalEvent? {
        return when(domainEvent) {
            is SomethingHappened -> SomethingHappenedExternalEvent(...)
            else -> null // Don't publish internal events
        }
    }
    
    // ... implement onSuccess/onFailure logging
}
```

## Key Files

- `src/main/kotlin/com/abaddon83/burraco/common/adapter/kafka/consumer/KafkaConsumerVerticle.kt`: Core consumer logic.
- `src/main/kotlin/com/abaddon83/burraco/common/adapter/kafka/consumer/EventRouterHandler.kt`: Event routing logic.
- `src/main/kotlin/com/abaddon83/burraco/common/adapter/kafka/producer/KafkaProducerVerticle.kt`: Core producer logic.
