package com.abaddon83.burraco.game.adapters.eventStoreAdapter.tcp.config

data class EventStoreTcpBusConfig(
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
