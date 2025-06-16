package com.abaddon83.burraco.common.adapter.kafka

interface Handler<E> {
    /**
     * Handle the event.
     *
     * @param event the event to handle
     */
    fun handle(event: E?)
}