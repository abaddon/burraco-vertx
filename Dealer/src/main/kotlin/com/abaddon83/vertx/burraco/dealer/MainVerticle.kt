package com.abaddon83.vertx.burraco.dealer

import io.vertx.core.*
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceDiscoveryOptions
import io.vertx.servicediscovery.types.HttpEndpoint

class MainVerticle : AbstractVerticle() {
    companion object {
        private val log = org.slf4j.LoggerFactory.getLogger(this::class.qualifiedName)
    }

    private val records: MutableList<Record> = mutableListOf()

    private val serviceDiscoveryOptions: ServiceDiscoveryOptions = ServiceDiscoveryOptions()
        .setAnnounceAddress("vertx.discovery.announce")
        .setName("my-name")

    override fun start(startPromise: Promise<Void>?) {
        val discovery: ServiceDiscovery = ServiceDiscovery.create(vertx, serviceDiscoveryOptions)
        val serverOpts = DeploymentOptions().setConfig(config())

        //list of verticle to deploy
        val allFutures: List<Future<Any>> = listOf(
            //deploy(RestApiVerticle::class.qualifiedName!!, serverOpts).future()
        )

        CompositeFuture.all(allFutures).onComplete{
            if (it.succeeded()) {
                //When all vertx are deployed to something
                val restApiRecord = HttpEndpoint.createRecord("command-api-service", "127.0.0.1", 8080, "/")
                discovery.publish(restApiRecord) { ar ->
                    if (ar.succeeded()) {
                        records.add(ar.result())
                        log.info("command-api-service published: ${ar.result()}")
                    } else {
                        log.error("command-api-service not published: ${ar.cause()}")
                    }
                }
                startPromise?.complete()
            } else {
                startPromise?.fail(it.cause())
            }
            discovery.close()
        }
    }

    fun stop(stopFuture: Future<Void>?) {
        val ids = vertx.deploymentIDs()
        for (s in ids) {
            log.info(this.deploymentID())
        }
    }

    private fun deploy(name: String, opts: DeploymentOptions): Promise<Any> {
        val done = Promise.promise<Any>()

        vertx.deployVerticle(name, opts) {
            if (it.failed()) {
                log.error("Failed to deploy verticle $name")
                done.fail(it.cause())
            } else {
                log.info("Deployed verticle $name")
                done.complete()
            }
        }

        return done
    }

    private fun undeploy(name: String, opts: DeploymentOptions): Promise<Any> {
        val done = Promise.promise<Any>()

        vertx.deployVerticle(name, opts) {
            if (it.failed()) {
                log.error("Failed to deploy verticle $name")
                done.fail(it.cause())
            } else {
                log.info("Deployed verticle $name")
                done.complete()
            }
        }

        return done
    }
}
