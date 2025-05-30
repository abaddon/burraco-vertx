package com.abaddon83.burraco.common

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

interface ApplicationCoroutineScope {

    fun coroutineContext(): CoroutineContext
    fun scope(): CoroutineScope
    fun close()
}