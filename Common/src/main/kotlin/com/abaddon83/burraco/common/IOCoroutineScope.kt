package com.abaddon83.burraco.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class IOCoroutineScope : ApplicationCoroutineScope {
    private val job = SupervisorJob()
    private val dispatcher = Dispatchers.IO // or your preferred dispatcher
    private val coroutineContext: CoroutineContext = job + dispatcher
    private val scope: CoroutineScope = CoroutineScope(coroutineContext)
    override fun coroutineContext(): CoroutineContext = coroutineContext

    override fun scope(): CoroutineScope = scope

    override fun close() {
        job.cancel()
    }
}