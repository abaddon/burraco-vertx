package com.abaddon83.burraco.readModel

import com.abaddon83.burraco.readModel.adapters.KafkaConsumerGameAdapter.KafkaConsumerGameAdapter
import com.abaddon83.burraco.readModel.adapters.KafkaConsumerGameAdapter.config.KafkaConsumerConfig
import com.abaddon83.burraco.readModel.adapters.readModelRestAdapter.RestApiVerticle
import com.abaddon83.burraco.readModel.adapters.readModelRestAdapter.config.HttpConfig
import com.abaddon83.burraco.readModel.adapters.repositoryAdapters.inMemory.InMemoryRepositoryAdapter
import com.abaddon83.burraco.readModel.adapters.repositoryAdapters.mysql.MysqlRepositoryAdapter
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import io.vertx.core.*
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceDiscoveryOptions

class MainVerticle : AbstractVerticle() {

  companion object {
    @JvmStatic
    fun main(args:Array<String>) {
      Launcher.executeCommand("run", MainVerticle::class.java.name)
    }
  }
  private val log = org.slf4j.LoggerFactory.getLogger(this::class.qualifiedName)

  private val serviceDiscoveryOptions: ServiceDiscoveryOptions = ServiceDiscoveryOptions()
    .setAnnounceAddress("vertx.discovery.announce")
    .setName("my-name")

  override fun start(startPromise: Promise<Void>?) {
    val discovery: ServiceDiscovery = ServiceDiscovery.create(vertx, serviceDiscoveryOptions)
    val serverOpts = DeploymentOptions().setConfig(config())
    val kafkaConsumerConfig = KafkaConsumerConfig()
    val repository: RepositoryPort = MysqlRepositoryAdapter()

    //list of verticle to deploy
    val allFutures: List<Future<Any>> = listOf(
      deploy(KafkaConsumerGameAdapter(kafkaConsumerConfig, repository), serverOpts).future(),
      //deploy(RestApiVerticle(HttpConfig()), serverOpts).future()

    )

    CompositeFuture.all(allFutures).onComplete{
      if (it.succeeded()) {
        //When all vertx are deployed to something
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
