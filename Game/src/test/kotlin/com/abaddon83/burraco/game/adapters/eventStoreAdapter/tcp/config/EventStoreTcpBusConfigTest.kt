package com.abaddon83.burraco.game.adapters.eventStoreAdapter.tcp.config

import org.junit.jupiter.api.Test

internal class EventStoreTcpBusConfigTest{
    @Test
    fun `Given a EventStoreTcpBusConfig element when I execute the method to Json then I receive a JSonObject`(){
        val config = EventStoreTcpBusConfig(ServiceConfig("localhost",1234), ChannelsConfig("publish","query"))
        val expected = "{\"service\":{\"address\":\"localhost\",\"port\":1234},\"channels\":{\"publish\":\"publish\",\"query\":\"query\"}}"

        kotlin.test.assertEquals(expected, config.toJson().toString())

    }
}