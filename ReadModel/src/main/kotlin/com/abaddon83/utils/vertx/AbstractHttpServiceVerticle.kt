package com.abaddon83.utils.vertx

import io.vertx.core.AbstractVerticle
import io.vertx.core.CompositeFuture
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.HttpEndpoint
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

abstract class AbstractHttpServiceVerticle : AbstractVerticle() {

    private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!


    protected val discovery: ServiceDiscovery by lazy {
        ServiceDiscovery.create(vertx)
    }
    private var record: Record? = null
    protected var server: HttpServer? = null

    /**
     * Automatically unpublish the service when it stops
     */
    override fun stop(endPromise: Promise<Void>?) {
        log.info("Stopping " + this::class.qualifiedName)

        // Shutting the server AND waiting for the answer should prevents a
        //  java.net.BindException: Address already in use with gradle (but does not)
        //launch(vertx.dispatcher()) {
        CompositeFuture.all(unpublishRecord().future(), shutdownServer().future()).onComplete {
            if(it.succeeded()) {
                log.info("Stopped " + this::class.qualifiedName)
                endPromise?.complete()
            }else {
                log.error("Stopping failed" + this::class.qualifiedName)
                endPromise?.fail(it.cause())
            }
        }
    }

    /**
     * Publish the service record
     */
    fun publishServiceRecord(
        name: String,
        host: String,
        port: Int,
        root: String
    ): Promise<Any> {
        val done = Promise.promise<Any>()
        log.info("Publishing service:")
        val record = HttpEndpoint.createRecord(name, host, port, root)
        discovery.publish(record).onComplete { ar ->
            log.info("$name published")
            done.complete()
        }.onFailure { failure ->
            log.error("$name not published.", failure)
            done.fail(failure)
        }
        return done;
    }

    /**
     * Unpublish the service record
     */
    private fun unpublishRecord(): Promise<Any> {
        val done = Promise.promise<Any>()
        log.info("Unpublishing service")
        val record = this.record
        if (record != null) {
            // Unpublish and nullify the record
            discovery.unpublish(record.registration)
                .onSuccess {
                    this.record = null
                    discovery.close()
                    done.complete()
                }
                .onFailure {
                    this.record = null
                    discovery.close()
                    done.fail(it)
                }
        } else {
            done.complete()
        }
        return done;
    }

    private fun shutdownServer(): Promise<Any> {
        val done = Promise.promise<Any>()
        if (server != null) {
            server!!.close()
                .onSuccess {
                    server = null;
                    done.complete()
                }
                .onFailure(done::fail)
        } else {
            done.complete()
        }

        return done;
    }

//
// Utilities

    /**
     * Extension to the HTTP response to output JSON objects.
     */
    fun HttpServerResponse.endWithJson(data: Any) {

        this.putHeader("Content-Type", "application/json; charset=utf-8")

        if (data is JsonObject) {
            this.end(data.encodePrettily())
        } else {
            this.end(io.vertx.core.json.Json.encodePrettily(data))
        }
    }
}