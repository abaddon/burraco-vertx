package com.abaddon83.burraco.game.adapters.commandController

import com.abaddon83.burraco.game.DomainResult
import com.abaddon83.burraco.game.GameError
import com.abaddon83.burraco.game.adapters.commandController.config.RestApiConfig
import com.abaddon83.burraco.game.adapters.commandController.config.RestApiHttpConfig
import com.abaddon83.burraco.game.commands.gameDraft.CreateGame
import com.abaddon83.burraco.game.helpers.Validated
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.ports.CommandControllerPort
import com.abaddon83.burraco.game.ports.Outcome
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.Result
import io.github.abaddon.kcqrs.core.domain.SimpleAggregateCommandHandler
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class RestApiVerticleTest {

    @Test
    fun `given I want to create a  new game when call api create new game then new game is created`(testContext: VertxTestContext) {
        val client: HttpClient = vertx.createHttpClient()
        client.request(HttpMethod.GET, PORT, ADDRESS, "/game/burraco")
            .compose { it.send().compose(HttpClientResponse::body) }
            .onComplete(testContext.succeeding { buffer ->
                testContext.verify {
                    val response = buffer.toString()
                    val regex = Regex("""^\{\"gameId\"\:\".{8}-.{4}-.{4}-.{4}-.{12}\",\"status\":\"DRAFT\",\"players\":\[\]\}""")
                    assertEquals(true,response.matches(regex))
                    testContext.completeNow()
                }
            })

    }

    companion object {
        private val vertx: Vertx = Vertx.vertx();
        private const val PORT = 12345
        private const val ADDRESS = "localhost"
        private const val ROOT = "/"
        private const val STREAM_NAME = "RestApiVerticleTest"

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext) {
            val restApiConfig = RestApiConfig("test", RestApiHttpConfig(PORT, ADDRESS, ROOT))
            val verticle =
                RestApiVerticle(restApiConfig, controllerAdapter = DummyCommandControllerAdapter(STREAM_NAME))

            vertx.deployVerticle(verticle).onComplete {
                if (it.succeeded()) {
                    testContext.completeNow()
                } else {
                    testContext.failNow(it.cause())
                }
            }
        }
    }

    class DummyCommandControllerAdapter(
        streamName: String,
    ) : CommandControllerPort {
        fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { GameDraft.empty() }
        override val gameCommandHandler =
            SimpleAggregateCommandHandler<Game>(InMemoryEventStoreRepository<Game>(streamName, emptyAggregate()))

        override suspend fun createGame(): Outcome {
            val cmd = CreateGame(GameIdentity.create())
            return when (val commandResult = gameCommandHandler.handle(cmd)) {
                is Result.Valid -> Validated.Valid(DomainResult(listOf(),commandResult.value))
                is Result.Invalid -> Validated.Invalid(GameError(commandResult.err.message.orEmpty()))
            }
        }
    }

}

