package com.abaddon83.burraco.readModel.events

import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.readModel.adapters.repositoryAdapters.inMemory.InMemoryRepositoryAdapter
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

class GameEventHandlerTest {

    private val repository: RepositoryPort = InMemoryRepositoryAdapter()


//    val testDispatcherProvider = object : DispatcherProvider {
//        override fun io(): CoroutineDispatcher = coroutinesTestRule.testDispatcher
//    }

    //@Test
//    fun `given x y z`() {
//        val event = BurracoGameCreated(GameIdentity.create(), listOf())
//
//        val handler = GameEventHandler(repository,event)
//        runBlockingTest{
//            handler.join()
//        }
//
//    }


}