package com.abaddon83.eventStore.adapters.controllerAdapter.tcp.config

data class EventStoreServiceTCPConfig(
    val service: ServiceConfig,
    val channels: ChannelsConfig
)

data class ServiceConfig(
    val address: String,
    val port: Int
)

data class ChannelsConfig(
    val publish: String,
    val query: String
)
