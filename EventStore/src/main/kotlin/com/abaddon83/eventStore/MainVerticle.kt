package com.abaddon83.eventStore

import com.abaddon83.eventStore.adapters.controllerAdapter.tcp.ControllerAdapter
import com.abaddon83.eventStore.adapters.controllerAdapter.tcp.EventStoreServiceTCPVerticle
import com.abaddon83.eventStore.adapters.repositoryAdapter.mysql.MysqlRepositoryAdapter
import io.vertx.core.*
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceDiscoveryOptions

class MainVerticle : AbstractVerticle() {
  companion object {
    @JvmStatic
    fun main(args:Array<String>) {
      Launcher.executeCommand("run", com.abaddon83.eventStore.MainVerticle::class.java.name)
    }
  }
  private val log = org.slf4j.LoggerFactory.getLogger(this::class.qualifiedName)
  private val records: MutableList<Record> = mutableListOf()
  private val serviceConfig = ServiceConfig.load()
  private val serviceDiscoveryOptions: ServiceDiscoveryOptions = ServiceDiscoveryOptions()
    .setAnnounceAddress("vertx.discovery.announce")
    .setName("my-name")

  override fun start(startPromise: Promise<Void>?) {
    val discovery: ServiceDiscovery = ServiceDiscovery.create(vertx, serviceDiscoveryOptions)
    val serverOpts = DeploymentOptions().setConfig(config())
    val repository = MysqlRepositoryAdapter(serviceConfig.mysqlRepository)

    val controller = ControllerAdapter(repository = repository)

    //list of verticle to deploy
    val allFutures: List<Future<Any>> = listOf(
      deploy(EventStoreServiceTCPVerticle(controller,serviceConfig.eventStoreServiceTCP), serverOpts).future()
    )

    CompositeFuture.all(allFutures).onComplete{
      if (it.succeeded()) {
        //When all vertx are deployed to something
//        val restApiRecord = HttpEndpoint.createRecord("command-api-service", "127.0.0.1", 7070, "/")
//        discovery.publish(restApiRecord) { ar ->
//          if (ar.succeeded()) {
//            records.add(ar.result())
//            log.info("command-api-service published: ${ar.result()}")
//          } else {
//            log.error("command-api-service not published: ${ar.cause()}")
//          }
//        }
        startPromise?.complete()
      } else {
        startPromise?.fail(it.cause())
      }
      discovery.close()
    }
  }

  override fun stop(stopPromise: Promise<Void?>) {
    val ids = vertx.deploymentIDs()
    log.info("Undeployed ok: {}",ids)
    for (id in ids) {
      vertx.undeploy(id) { res: AsyncResult<Void?> ->
        if (res.succeeded()) {
          log.info("Undeployed ok")
          stopPromise.complete()
        } else {
          log.error("Undeploy failed!",res.cause())
          stopPromise.fail(res.cause())
        }
      }
    }
    //stop()
    //stopPromise.complete()
  }

  private fun deploy(verticle: Verticle, opts: DeploymentOptions): Promise<Any> {
    val done = Promise.promise<Any>()

    vertx.deployVerticle(verticle, opts) {
      if (it.failed()) {
        log.error("Failed to deploy verticle ${verticle::class.qualifiedName}")
        done.fail(it.cause())
      } else {
        log.info("Verticle deployed ${verticle::class.qualifiedName}")
        done.complete()
      }
    }
    return done
  }
}
