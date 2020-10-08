package com.abaddon83.utils.es

import java.time.LocalDateTime

abstract class Event() {
    abstract fun key():String
    val created: String = LocalDateTime.now().toString()
    val version = 0
}