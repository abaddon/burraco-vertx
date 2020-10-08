package com.abaddon83.vertx.eventStore.adapters.repositoryAdapter.vertx

import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.vertx.eventStore.commands.EventError
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.OutcomeDetail
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLConnectOptions
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.Tuple

class MysqlService(vertx: Vertx) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
        private val EVENT_INSERT = ""
    }

    val vertx: Vertx = vertx;
    private val poolOptions: PoolOptions = poolOptionsOf(maxSize = 5)
    private val connectOptions: MySQLConnectOptions = mySQLConnectOptionsOf(
        database = "the-db",
        host = "the-host",
        password = "secret", port = 3306,
        user = "user"
    )
    private val client = MySQLPool.pool(connectOptions, poolOptions)

    fun saveHandler(event: Event, resultHandler: Handler<AsyncResult<Validated<EventError, OutcomeDetail>>>) {
        client.getConnection { ar ->
            if (ar.failed()) {
                log.error("Connection failed: ${ar.cause()}")
                resultHandler.handle(
                    Future.succeededFuture(Invalid(EventError("Connection failed: ${ar.cause()}")))
                )
            }
            val connection = ar.result()
            val row = connection
                .preparedQuery(EVENT_INSERT)
                .execute(
                    Tuple.of(event.name, event.entityKey, event.entityName, event.instant, event.jsonPayload)
                ) { ar ->
                    if (ar.succeeded()) {
                        val rows = ar.result()
                        println("Got ${rows.size()} rows ")
                        resultHandler.handle(
                            Future.succeededFuture(Valid(mapOf("result" to rows.first().toString())))
                        )
                    } else {
                        println("Failure: ${ar.cause()}")
                        Future.succeededFuture(Invalid(EventError("Failure: ${ar.cause()}")))
                    }
                }
        }
    }
}