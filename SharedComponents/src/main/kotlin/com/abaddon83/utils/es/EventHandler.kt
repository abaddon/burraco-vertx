package com.abaddon83.utils.es

interface EventHandler<T> {

    fun processEvent(e: Event): Unit

    fun getProjection(key: String): T
}