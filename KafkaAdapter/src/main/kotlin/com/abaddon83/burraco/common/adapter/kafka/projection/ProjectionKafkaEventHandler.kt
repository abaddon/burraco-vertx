package com.abaddon83.burraco.common.adapter.kafka.projection

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.helpers.Validated
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.github.abaddon.kcqrs.core.projections.IProjection
import kotlinx.coroutines.CoroutineScope

/**
 * Adapter that bridges KafkaEventHandler with KafkaStoreProjectionHandler.
 *
 * @param TProjection The type of projection being handled
 * @param projectionHandler The projection handler that processes domain events
 * @param expectedEventName The name of the Kafka event this handler processes
 */
abstract class ProjectionKafkaEventHandler<TProjection : IProjection>(
    private val projectionHandler: KafkaStoreProjectionHandler<TProjection>,
    private val expectedEventName: String

) : KafkaEventHandler(expectedEventName) {

    /**
     * Returns the coroutine scope from the projection handler.
     * This ensures all projection operations run in the correct scope.
     */
    override fun getCoroutineIOScope(): CoroutineScope {
        // ProjectionHandler extends CoroutineScope
        return projectionHandler as CoroutineScope
    }

    /**
     * Handles a Kafka event by converting it to a domain event and processing it through
     * the projection handler.
     *
     * @param event The Kafka event to process
     * @return Validated.Valid(Unit) on success, Validated.Invalid(error) on failure
     */
    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        return try {
            log.info("Processing projection event: ${event.eventName}")

            // Step 1: Convert KafkaEvent to DomainEvent
            val domainEvent = try {
                toDomainEvent(event)
            } catch (ex: IllegalArgumentException) {
                log.error("Unknown or unsupported event type: ${event.eventName}", ex)
                return Validated.Invalid("Unsupported event type: ${event.eventName}")
            } catch (ex: Exception) {
                log.error("Failed to convert KafkaEvent to DomainEvent: ${event.eventName}", ex)
                return Validated.Invalid("Event conversion failed: ${ex.message}")
            }

            val result = projectionHandler.onEvent(domainEvent)

            // Step 3: Convert Result to Validated
            if (result.isSuccess) {
                log.debug("Successfully processed projection event: ${event.eventName}")
                Validated.Valid(Unit)
            } else {
                val error = result.exceptionOrNull()
                val errorMessage = error?.message ?: "Unknown projection processing error"
                log.error("Projection processing failed for ${event.eventName}: $errorMessage", error)
                Validated.Invalid(errorMessage)
            }

        } catch (ex: Exception) {
            // Catch-all for unexpected errors
            log.error("Unexpected error processing projection event: ${event.eventName}", ex)
            Validated.Invalid(ex.message ?: "Unexpected error during projection processing")
        }
    }

    abstract fun toDomainEvent(kafkaEvent: KafkaEvent): IDomainEvent
}