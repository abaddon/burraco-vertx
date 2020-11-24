package com.abaddon83.utils.ddd.readModel

import com.abaddon83.utils.ddd.Event

//abstract class AggregateRoot<T>(className: String): Entity<T>() {
interface Projection<T> {
     val key:ProjectionKey

    fun applyEvent(event: Event): T
}