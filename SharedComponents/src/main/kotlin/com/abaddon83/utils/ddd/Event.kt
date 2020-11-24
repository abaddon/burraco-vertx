package com.abaddon83.utils.ddd

import java.time.Instant

abstract class Event() {
    abstract fun key():String
    abstract val entityName: String
    val created: Instant = Instant.now()
    val version = 0
}