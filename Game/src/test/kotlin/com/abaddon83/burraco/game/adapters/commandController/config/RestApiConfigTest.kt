//package com.abaddon83.burraco.game.adapters.commandController.config
//
//import org.junit.jupiter.api.Test
//import kotlin.test.assertEquals
//
//internal class RestApiConfigTest{
//
//    @Test
//    fun `Given a RestApiConfig element when I execute the method to Json then I receive a JSonElement`(){
//        val restApiConfig: RestApiConfig = RestApiConfig("testServiceName", RestApiHttpConfig(123,"localhost","/"))
//        val expected = "{\"serviceName\":testServiceName,\"http\":{\"port\":123,\"address\":\"localhost\",\"root\":\"/\"}}"
//
//        assertEquals(expected,restApiConfig.toJson().toString())
//
//    }
//}