package com.abaddon83.burraco.player.adapter.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.externalEvents.game.GameStartedExternalEvent
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.port.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

const val GAME_STARTED = "GameStarted"

/**
 * Kafka handler that processes GameStarted events from the Game service.
 *
 * When a game starts, this handler:
 * 1. Activates the player whose turn it is (PlayerDraft → PlayerActive)
 * 2. Sets all other players to waiting state (PlayerDraft → PlayerWaiting)
 * 3. Extracts and assigns teammates based on team structure
 *
 * Failure Strategy: Fail fast (.getOrThrow()) - if any player activation fails,
 * the error propagates and the event processing fails. This ensures data consistency.
 */
class GameStartedHandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(GAME_STARTED) {

    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val gameStartedEvent = Json.decodeValue(event.eventPayload, GameStartedExternalEvent::class.java)

        // Extract all players from teams (flatten team structure)
        val allPlayers = gameStartedEvent.teams.flatten()

        // Process each player
        allPlayers.forEach { playerId ->
            // Find teammate in the same team
            val teamMateId = findTeamMate(gameStartedEvent.teams, playerId)

            val result = if (playerId == gameStartedEvent.playerTurn) {
                // It's this player's turn - activate them
                log.info("Activating player $playerId for game ${gameStartedEvent.aggregateIdentity}")
                commandController.activatePlayer(playerId, gameStartedEvent.aggregateIdentity, teamMateId)
            } else {
                // Not this player's turn - set them to waiting
                log.info("Setting player $playerId to waiting for game ${gameStartedEvent.aggregateIdentity}")
                commandController.setPlayerWaiting(playerId, gameStartedEvent.aggregateIdentity, teamMateId)
            }

            // Fail fast on error (per design decision #4)
            when (result) {
                is Validated.Invalid -> {
                    log.error("Failed to process player $playerId: ${result.err}")
                    throw IllegalStateException("Failed to process player activation/waiting for player $playerId",
                        (result.err as? com.abaddon83.burraco.player.DomainError.ExceptionError)?.exception)
                }
                is Validated.Valid -> log.debug("Successfully processed player $playerId")
            }
        }

        // Return successful validation
        return Validated.Valid(Unit)
    }

    /**
     * Finds the teammate of a given player within the team structure.
     *
     * @param teams List of teams, where each team is a list of player identities
     * @param playerId The player to find a teammate for
     * @return The teammate's PlayerIdentity, or null if no teammate found
     */
    private fun findTeamMate(teams: List<List<PlayerIdentity>>, playerId: PlayerIdentity): PlayerIdentity? {
        // Find the team containing this player
        val team = teams.find { it.contains(playerId) } ?: return null

        // Return the first teammate that is not the current player
        return team.firstOrNull { it != playerId }
    }
}
