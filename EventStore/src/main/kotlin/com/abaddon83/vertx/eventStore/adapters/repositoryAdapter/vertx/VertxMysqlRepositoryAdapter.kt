package com.abaddon83.vertx.eventStore.adapters.repositoryAdapter.vertx

import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.commands.EventError
import com.abaddon83.vertx.eventStore.ports.OutcomeDetail
import com.abaddon83.vertx.eventStore.ports.RepositoryPort
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory

class VertxMysqlRepositoryAdapter(vertx: Vertx) : RepositoryPort {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }
    val mysqlService = MysqlService(vertx)

    override fun save(event: Event): Validated<EventError, OutcomeDetail> {
        mysqlService.saveHandler(event){ar ->
            if(ar.failed()){
                Future.succeededFuture(Invalid(EventError("Connection failed: ${ar.cause()}")))
            }
            Future.succeededFuture(Valid(mapOf<String,String>()))
        }

        TODO("I don't know how return the value without change the interface.. but I don't want to change it..")
    }
}