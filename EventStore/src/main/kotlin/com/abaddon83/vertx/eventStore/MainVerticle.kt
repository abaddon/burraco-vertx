package com.abaddon83.vertx.eventStore

import com.abaddon83.vertx.eventStore.adapters.controllerAdapter.EventStoreService
import com.abaddon83.vertx.eventStore.adapters.controllerAdapter.EventStoreServiceBusVerticle
import com.abaddon83.vertx.eventStore.adapters.controllerAdapter.EventStoreServiceImpl
import io.vertx.core.*
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceDiscoveryOptions
import io.vertx.servicediscovery.ServiceReference
import io.vertx.servicediscovery.types.EventBusService
import io.vertx.serviceproxy.ServiceBinder

class MainVerticle : AbstractVerticle() {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)
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
      deploy(EventStoreServiceBusVerticle::class.qualifiedName!!, serverOpts).future()
    )

    CompositeFuture.all(allFutures).setHandler {
      if (it.succeeded()) {
        //When all vertx are deployed to something
        startPromise?.complete()
      } else {
        startPromise?.fail(it.cause())
      }
      discovery.close()
    }
  }

  override fun stop(stopFuture: Future<Void>?) {
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
