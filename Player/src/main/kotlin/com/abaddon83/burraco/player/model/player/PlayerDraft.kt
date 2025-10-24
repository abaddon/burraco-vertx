package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.event.player.PlayerCollectedCard
import com.abaddon83.burraco.common.models.event.player.PlayerCreated
import com.abaddon83.burraco.common.models.event.player.PlayerDeleted
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log

private const val AGGREGATE_APPLY_EVENT_MSG = "The aggregate is applying the event {} with id {} to the aggregate {}"
private const val VALIDATION_MSG_PLAYER_EXIST = "Current player with id %s is already created"
private const val VALIDATION_MSG_PLAYER_ALREADY_ADDED = "The player %s is already a player of game %s"
private const val VALIDATION_MSG_PLAYER_NOT_EXIST = "Player with id %s does not exist"
private const val VALIDATION_MSG_PLAYER_ID_MISMATCH = "Card dealt to player %s but current player is %s"
private const val VALIDATION_MSG_GAME_ID_MISMATCH = "Card dealt for game %s but current game is %s"

data class PlayerDraft(
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

    fun deletePlayer(): PlayerNotInGame {
        check(this.id != PlayerIdentity.empty()) { String.format(VALIDATION_MSG_PLAYER_NOT_EXIST, this.id) }
        return raiseEvent(PlayerDeleted.create(this.id as PlayerIdentity)) as PlayerNotInGame
    }

    fun addCard(playerId: PlayerIdentity, gameId: GameIdentity, card: Card): PlayerDraft {
        check(this.id == playerId) { String.format(VALIDATION_MSG_PLAYER_ID_MISMATCH, playerId, this.id) }
        check(this.gameIdentity == gameId) { String.format(VALIDATION_MSG_GAME_ID_MISMATCH, gameId, this.gameIdentity) }
        return raiseEvent(PlayerCollectedCard.create(playerId, gameId, card)) as PlayerDraft
    }

    private fun apply(event: PlayerCreated): PlayerDraft {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.java.simpleName, event.messageId, this.id)
        return copy(id = event.aggregateId, gameIdentity = event.gameIdentity, user = event.user, version = 0)
    }

    private fun apply(event: PlayerCollectedCard): PlayerDraft {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.java.simpleName, event.messageId, this.id)
        return copy(cards = this.cards + event.card, version = version + 1)
    }

    private fun apply(event: PlayerDeleted): PlayerNotInGame {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.java.simpleName, event.messageId, this.id)
        return PlayerNotInGame.from(this)
    }

}