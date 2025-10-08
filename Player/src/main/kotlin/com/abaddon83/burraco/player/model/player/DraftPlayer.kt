package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.event.player.PlayerCollectedCard
import com.abaddon83.burraco.common.models.event.player.PlayerCreated
import com.abaddon83.burraco.common.models.event.player.PlayerDeleted
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log

private const val AGGREGATE_APPLY_EVENT_MSG = "The aggregate is applying the event {} with id {} to the aggregate {}"
private const val VALIDATION_MSG_PLAYER_EXIST = "Current player with id %s is already created"
private const val VALIDATION_MSG_PLAYER_ALREADY_ADDED = "The player %s is already a player of game %s"
private const val VALIDATION_MSG_PLAYER_NOT_EXIST = "Player with id %s does not exist"
private const val VALIDATION_MSG_PLAYER_ID_MISMATCH = "Card dealt to player %s but current player is %s"
private const val VALIDATION_MSG_GAME_ID_MISMATCH = "Card dealt for game %s but current game is %s"

class PlayerDraft(
    override val id: IIdentity,
    override val version: Long,
    override val gameIdentity: GameIdentity,
    override val user: String,
    val cards: List<Card> = emptyList()
) : Player() {

    companion object Factory {
        fun empty(): PlayerDraft = PlayerDraft(PlayerIdentity.empty(), 0, GameIdentity.empty(), "", emptyList())
    }

    fun createPlayer(playerIdentity: PlayerIdentity, user: String, gameIdentity: GameIdentity): PlayerDraft {
        check(this.id == PlayerIdentity.empty()) { String.format(VALIDATION_MSG_PLAYER_EXIST, this.id) }
        return raiseEvent(PlayerCreated.create(playerIdentity, gameIdentity, user)) as PlayerDraft
    }

    fun deletePlayer(): DeletedPlayer {
        check(this.id != PlayerIdentity.empty()) { String.format(VALIDATION_MSG_PLAYER_NOT_EXIST, this.id) }
        return raiseEvent(PlayerDeleted.create(this.id as PlayerIdentity)) as DeletedPlayer
    }

    fun addCard(playerId: PlayerIdentity, gameId: GameIdentity, card: Card): PlayerDraft {
        check(this.id == playerId) { String.format(VALIDATION_MSG_PLAYER_ID_MISMATCH, playerId, this.id) }
        check(this.gameIdentity == gameId) { String.format(VALIDATION_MSG_GAME_ID_MISMATCH, gameId, this.gameIdentity) }
        // Directly return updated state without raising event (event comes from Dealer service)
        return raiseEvent(PlayerCollectedCard.create(playerId, gameId, card)) as PlayerDraft
    }

    private fun apply(event: PlayerCreated): PlayerDraft {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.java.simpleName, event.messageId, this.id)
        return PlayerDraft(
            id = event.aggregateId,
            version = 0,
            gameIdentity = event.gameIdentity,
            user = event.user,
            cards = emptyList()
        )
    }

    private fun apply(event: PlayerCollectedCard): PlayerDraft {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.java.simpleName, event.messageId, this.id)
        return PlayerDraft(
            id = this.id,
            version = version + 1,
            gameIdentity = this.gameIdentity,
            user = this.user,
            cards = this.cards + event.card
        )
    }

    private fun apply(event: PlayerDeleted): DeletedPlayer {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.java.simpleName, event.messageId, this.id)
        return DeletedPlayer(
            id = event.aggregateId,
            version = version + 1,
            gameIdentity = this.gameIdentity,
            user = this.user
        )
    }

}