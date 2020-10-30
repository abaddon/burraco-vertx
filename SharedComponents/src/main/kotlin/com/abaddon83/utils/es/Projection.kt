package com.abaddon83.utils.es

//abstract class AggregateRoot<T>(className: String): Entity<T>() {
interface Projection<T> {

    fun applyEvent(event: Event): T
}