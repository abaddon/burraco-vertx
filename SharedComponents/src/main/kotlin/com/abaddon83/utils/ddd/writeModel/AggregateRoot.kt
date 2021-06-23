package com.abaddon83.utils.ddd.writeModel

import com.abaddon83.utils.ddd.Event

interface AggregateType


abstract class AggregateRoot<T>(className: String): Entity<T>() {

    abstract fun aggregateType(): AggregateType

    private val uncommittedChanges = ArrayList<Event>()

    fun getUncommittedChanges(): Iterable<Event> = uncommittedChanges.toList().asIterable()

    fun markChangesAsCommitted() {
        log.debug("Marking all changes as committed")
        uncommittedChanges.clear()
    }


    fun <A: AggregateRoot<T>> applyAndQueueEvent(event: Event): A {
        log.debug("Applying {}", event)
        val updatedAggregate = applyEvent(event) // Remember: this never fails

        log.debug("Queueing uncommitted change {}", event)
        updatedAggregate.uncommittedChanges.add(event)
        return updatedAggregate as A
    }

    fun <A: AggregateRoot<T>> applyAndQueueEvents(events: List<Event>): A {
        log.debug("Applying ${events.size} events")

        val updatedAggregate = events.fold( this, { agg, event  -> agg.applyEvent(event) as A })

        log.debug("Queueing ${events.size} uncommitted changes events" )
        updatedAggregate.uncommittedChanges.addAll(events)
        return updatedAggregate  as A
    }

    abstract fun applyEvent(event: Event): AggregateRoot<T>

    fun <A: AggregateRoot<T>> loadFromHistory(aggregate: A, history: Iterable<Event>): A {
        log.debug("Reloading aggregate $aggregate state from history")
        log.debug("history size: ${history.toList().size}")

        // Rebuilding an Aggregate state from Events is a 'fold' operation
        return history.fold( aggregate, { agg, event  -> agg.applyEvent(event) as A })
    }
}

class UnsupportedEventException(eventClass: Class<out Event>)
    : Exception("Unsupported event ${eventClass.canonicalName}")
