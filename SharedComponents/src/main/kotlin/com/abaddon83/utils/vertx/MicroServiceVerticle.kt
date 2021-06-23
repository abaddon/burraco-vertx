package com.abaddon83.utils.vertx

import io.vertx.core.*
import io.vertx.core.impl.ConcurrentHashSet
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceDiscoveryOptions
import io.vertx.servicediscovery.types.EventBusService
import io.vertx.servicediscovery.types.HttpEndpoint
import io.vertx.servicediscovery.types.MessageSource

/**
 * An implementation of [Verticle] taking care of the discovery and publication of services.
 *
 * @author [Clement Escoffier](http://escoffier.me)
 */
open class MicroServiceVerticle : AbstractVerticle() {
    protected var discovery: ServiceDiscovery? = null
    protected var registeredRecords: MutableSet<Record> = ConcurrentHashSet()

    override fun start() {
        discovery = ServiceDiscovery.create(vertx, ServiceDiscoveryOptions().setBackendConfiguration(config()))
    }

    fun publishHttpEndpoint(name: String?, host: String?, port: Int, completionHandler: Handler<AsyncResult<Void?>?>) {
        val record: Record = HttpEndpoint.createRecord(name, host, port, "/")
        publish(record, completionHandler)
    }

    fun publishMessageSource(
        name: String?,
        address: String?,
        contentClass: Class<*>?,
        completionHandler: Handler<AsyncResult<Void?>?>
    ) {
        val record: Record = MessageSource.createRecord(name, address, contentClass)
        publish(record, completionHandler)
    }

    fun publishMessageSource(name: String?, address: String?, completionHandler: Handler<AsyncResult<Void?>?>) {
        val record: Record = MessageSource.createRecord(name, address)
        publish(record, completionHandler)
    }

    fun publishEventBusService(
        name: String?,
        address: String?,
        serviceClass: Class<*>?,
        completionHandler: Handler<AsyncResult<Void?>?>
    ) {
        val record: Record = EventBusService.createRecord(name, address, serviceClass)
        publish(record, completionHandler)
    }

    protected fun publish(record: Record, completionHandler: Handler<AsyncResult<Void?>?>) {
        if (discovery == null) {
            try {
                start()
            } catch (e: Exception) {
                throw RuntimeException("Cannot create discovery service")
            }
        }
        discovery!!.publish(record, Handler { ar: AsyncResult<io.vertx.servicediscovery.Record?> ->
            if (ar.succeeded()) {
                registeredRecords.add(record)
            }
            completionHandler.handle(ar.map(null as Void?))
        })
    }

    @Throws(Exception::class)
    override fun stop(future: Promise<Void>) {
        val promises: MutableList<Promise<Void>> =  arrayListOf()
        for (record in registeredRecords) {
            val unregisteredPromise: Promise<Void> = Promise.promise()
            promises.add(unregisteredPromise)
            discovery!!.unpublish(record.getRegistration(), unregisteredPromise)
        }
        if (promises.isEmpty()) {
            discovery!!.close()
            future.complete()
        } else {
//            val composite: CompositeFuture = CompositeFuture.all(promises)
//            composite.handle( ar ->
//                discovery!!.close()
//                if (ar.failed()) {
//                    future.fail(ar.cause())
//                } else {
//                    future.complete()
//                }
//            )
//            }
        }
    }
}