package com.abaddon83.burraco.common


import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class VertxCoroutineScope(
    val vertx: Vertx
) : ApplicationCoroutineScope {
    private val job = SupervisorJob()
    private val dispatcher = vertx.dispatcher()
    private val coroutineContext: CoroutineContext = job + dispatcher
    private val scope: CoroutineScope = CoroutineScope(coroutineContext)

    override fun coroutineContext(): CoroutineContext = coroutineContext

    override fun scope(): CoroutineScope = scope

    override fun close() {
        job.cancel()
    }
}