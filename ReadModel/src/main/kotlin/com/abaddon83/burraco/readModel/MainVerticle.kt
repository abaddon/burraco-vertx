package com.abaddon83.burraco.readModel

import com.abaddon83.burraco.readModel.adapters.eventStreamAdapter.KafkaEventStreamVerticle
import io.vertx.core.*
import io.vertx.core.logging.LoggerFactory
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceDiscoveryOptions

class MainVerticle : AbstractVerticle() {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)
  }

  private val serviceDiscoveryOptions: ServiceDiscoveryOptions = ServiceDiscoveryOptions()
    .setAnnounceAddress("vertx.discovery.announce")
    .setName("my-name")

  override fun start(startPromise: Promise<Void>?) {
    val discovery: ServiceDiscovery = ServiceDiscovery.create(vertx, serviceDiscoveryOptions)
    val serverOpts = DeploymentOptions().setConfig(config())


    //list of verticle to deploy
    val allFutures: List<Future<Any>> = listOf(
      deploy(KafkaEventStreamVerticle::class.qualifiedName!!, serverOpts).future()
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
