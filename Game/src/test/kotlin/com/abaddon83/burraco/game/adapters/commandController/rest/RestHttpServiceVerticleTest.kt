package com.abaddon83.burraco.game.adapters.commandController.rest

import com.abaddon83.burraco.game.DomainResult
import com.abaddon83.burraco.game.adapters.commandController.rest.config.RestHttpServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.rest.config.RestApiHttpConfig
import com.abaddon83.burraco.game.helpers.Validated
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import com.abaddon83.burraco.game.models.player.Player
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.models.player.WaitingPlayer
import com.abaddon83.burraco.game.ports.CommandControllerPort
import com.abaddon83.burraco.game.ports.Outcome
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.SimpleAggregateCommandHandler
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class RestHttpServiceVerticleTest {

    @Test
    fun `given I want to check the health status when call api health then status positive`(testContext: VertxTestContext) {
        val expectedResponse = """{"status":"UP","checks":[{"id":"eventstore-connection","status":"UP"}],"outcome":"UP"}"""
        val client = WebClient.create(vertx)
        client
            .get(PORT, ADDRESS, "$ROOT/health")
            .send()
            .onSuccess {  response ->
                val actualResponse = response.bodyAsString()
                assertEquals(expectedResponse, actualResponse)
                testContext.completeNow()
            }
            .onFailure {
                assert(false)
                testContext.completeNow()
            }
    }
    @Test
    fun `given I want to create a  new game when call api create new game then new game is created`(testContext: VertxTestContext) {
        val expectedResponse = """{"gameId":"${GAME_IDENTITY.valueAsString()}","status":"DRAFT","players":[]}"""
        val client = WebClient.create(vertx)
        client
            .get(PORT, ADDRESS, "$ROOT/game/burraco")
            .send()
            .onSuccess {  response ->
                val actualResponse = response.bodyAsString()
                val regex = Regex("""^\{\"gameId\"\:\".{8}-.{4}-.{4}-.{4}-.{12}\",\"status\":\"DRAFT\",\"players\":\[\]\}""")
                assertEquals(true,actualResponse.matches(regex))
                testContext.completeNow()
            }
            .onFailure {
                assert(false)
                testContext.completeNow()
            }
    }

    @Test
    fun `given a game when calling api to add a player then player joined`(testContext: VertxTestContext) {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val bodyRequestMap = mapOf("playerId" to playerIdentity.valueAsString())

        val expectedResponse = """{"gameId":"${gameIdentity.valueAsString()}","status":"DRAFT","players":["${playerIdentity.valueAsString()}"]}"""

        val client = WebClient.create(vertx)
        client
            .post(PORT, ADDRESS, "$ROOT/games/${gameIdentity.valueAsString()}/player/add")
            .sendJson(bodyRequestMap)
            .onSuccess { response ->
                val actualResponse = response.bodyAsString()
                assertEquals(expectedResponse, actualResponse)
                testContext.completeNow()
            }.onFailure { error ->
                assert(false)
                testContext.completeNow()
            }
    }

    @Test
    fun `given a game when calling a malformed api to add a player then error received`(testContext: VertxTestContext) {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val bodyRequestMap = mapOf("player" to playerIdentity.valueAsString())

        val expectedResponse = """{"code":400,"message":"[Bad Request] Validation error for body application/json: provided object should contain property playerId"}"""

        val client = WebClient.create(vertx)
        client
            .post(PORT, ADDRESS, "$ROOT/games/${gameIdentity.valueAsString()}/player/add")
            .sendJson(bodyRequestMap)
            .onSuccess { response ->
                val actualResponse = response.bodyAsString()
                assertEquals(expectedResponse, actualResponse)
                testContext.completeNow()
            }.onFailure { error ->
                assert(false)
                testContext.completeNow()
            }
    }

    @Test
    fun `given a gameDraft when calling api requestDealCards then status is WAITING`(testContext: VertxTestContext) {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val bodyRequestMap = mapOf("playerId" to playerIdentity.valueAsString())

        val expectedStatus = "STARTED"
        val expectedPlayersSize = 4
        val expectedGameId = gameIdentity.valueAsString()

        val client = WebClient.create(vertx)
        client
            .post(PORT, ADDRESS, "$ROOT/games/${gameIdentity.valueAsString()}/requestDealCards")
            .sendJson(bodyRequestMap)
            .onSuccess { response ->
                val actualResponse = response.bodyAsJsonObject()
                assertEquals(expectedStatus, actualResponse.getString("status"))
                assertEquals(expectedPlayersSize, actualResponse.getJsonArray("players").size())
                assertEquals(expectedGameId, actualResponse.getString("gameId"))
                testContext.completeNow()
            }.onFailure { error ->
                assert(false)
                testContext.completeNow()
            }
    }

    companion object {
        private val vertx: Vertx = Vertx.vertx();
        private const val PORT = 12345
        private const val ADDRESS = "localhost"
        private const val ROOT = ""
        private const val STREAM_NAME = "RestApiVerticleTest"
        private val GAME_IDENTITY = GameIdentity.create()
        private val PLAYERS = listOf<Player>()

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext) {
            val restApiConfig = RestHttpServiceConfig("test", RestApiHttpConfig(PORT, ADDRESS, ROOT))
            val verticle =
                RestHttpServiceVerticle(
                    restApiConfig, controllerAdapter = DummyCommandControllerAdapter(
                        STREAM_NAME
                    )
                )

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
        streamName: String
    ) : CommandControllerPort {
        fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { GameDraft.empty() }
        override val gameCommandHandler =
            SimpleAggregateCommandHandler<Game>(InMemoryEventStoreRepository<Game>(streamName, emptyAggregate()))

        override suspend fun createGame(): Outcome =
            Validated.Valid(DomainResult(listOf(), GameDraft(GameIdentity.create(),0L, listOf())))

        override suspend fun addPlayer(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
            val player1 = WaitingPlayer(playerIdentity, listOf())
            val game = GameDraft(gameIdentity,0L, listOf(player1))
            return Validated.Valid(DomainResult(listOf(), game))
        }

        override suspend fun requestDealCards(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
            val player1 = WaitingPlayer(playerIdentity, listOf())
            val player2 = WaitingPlayer(PlayerIdentity.create(), listOf())
            val player3 = WaitingPlayer(PlayerIdentity.create(), listOf())
            val player4 = WaitingPlayer(PlayerIdentity.create(), listOf())
            val gameDraft = GameDraft(gameIdentity,0L, listOf(player1,player2,player3,player4))
            val gameWaitingDealer = GameWaitingDealer.from(gameDraft)
            return Validated.Valid(DomainResult(listOf(), gameWaitingDealer))
        }

        override suspend fun addCardPlayer(
            gameIdentity: GameIdentity,
            playerIdentity: PlayerIdentity,
            card: Card
        ): Outcome {
            TODO("Not yet implemented")
        }

        override suspend fun addCardFirstPlayerDeck(gameIdentity: GameIdentity, card: Card): Outcome {
            TODO("Not yet implemented")
        }

        override suspend fun addCardSecondPlayerDeck(gameIdentity: GameIdentity, card: Card): Outcome {
            TODO("Not yet implemented")
        }

        override suspend fun addCardDiscardDeck(gameIdentity: GameIdentity, card: Card): Outcome {
            TODO("Not yet implemented")
        }

        override suspend fun addCardDeck(gameIdentity: GameIdentity, card: Card): Outcome {
            TODO("Not yet implemented")
        }

        override suspend fun startPlayerTurn(gameIdentity: GameIdentity): Outcome {
            TODO("Not yet implemented")
        }
    }
}

