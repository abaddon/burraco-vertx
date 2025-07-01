package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.event.PlayerCreated
import com.abaddon83.burraco.player.event.PlayerDeleted
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log

private const val AGGREGATE_APPLY_EVENT_MSG = "The aggregate is applying the event {} with id {} to the aggregate {}"
private const val VALIDATION_MSG_PLAYER_EXIST = "Current player with id %s is already created"
private const val VALIDATION_MSG_PLAYER_ALREADY_ADDED = "The player %s is already a player of game %s"
private const val VALIDATION_MSG_PLAYER_NOT_EXIST = "Player with id %s does not exist"

class PlayerDraft(
    override val id: IIdentity,
    override val version: Long,
    override val gameIdentity: GameIdentity,
    override val user: String
) : Player() {

    companion object Factory {
        fun empty(): PlayerDraft = PlayerDraft(PlayerIdentity.empty(), 0, GameIdentity.empty(), "")
    }

    fun createPlayer(playerIdentity: PlayerIdentity, user: String, gameIdentity: GameIdentity): PlayerDraft {
        check(this.id == PlayerIdentity.empty()) { String.format(VALIDATION_MSG_PLAYER_EXIST, this.id) }
        return raiseEvent(PlayerCreated.create(playerIdentity, gameIdentity, user)) as PlayerDraft
    }

    fun deletePlayer(): DeletedPlayer {
        check(this.id != PlayerIdentity.empty()) { String.format(VALIDATION_MSG_PLAYER_NOT_EXIST, this.id) }
        return raiseEvent(PlayerDeleted.create(this.id as PlayerIdentity)) as DeletedPlayer
    }

    private fun apply(event: PlayerCreated): PlayerDraft {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.java.simpleName, event.messageId, this.id)
        return PlayerDraft(
            id = event.aggregateId,
            version = event.version,
            gameIdentity = event.gameIdentity,
            user = event.user
        )
    }

    private fun apply(event: PlayerDeleted): DeletedPlayer {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.java.simpleName, event.messageId, this.id)
        return DeletedPlayer(
            id = event.aggregateId,
            version = event.version,
            gameIdentity = this.gameIdentity,
            user = this.user
        )
    }

}