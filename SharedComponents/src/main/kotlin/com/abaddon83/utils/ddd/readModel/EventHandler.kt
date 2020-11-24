package com.abaddon83.utils.ddd.readModel

import com.abaddon83.utils.ddd.Event

interface EventHandler<T> {

    fun processEvent(projectionKey: ProjectionKey, e: Event): Unit

    fun getProjection(projectionKey: ProjectionKey): T
}