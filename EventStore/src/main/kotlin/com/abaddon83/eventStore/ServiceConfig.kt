package com.abaddon83.eventStore

import com.abaddon83.eventStore.adapters.controllerAdapter.tcp.config.EventStoreServiceTCPConfig
import com.abaddon83.eventStore.adapters.repositoryAdapter.mysql.config.MysqlRepositoryConfig
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract


data class ServiceConfig(
    val eventStoreServiceTCP: EventStoreServiceTCPConfig,
    val mysqlRepository: MysqlRepositoryConfig

    ) {
    companion object {
        fun load(): ServiceConfig {
            val config = ConfigFactory.load()
            return config.extract<ServiceConfig>()
        }
    }
}




