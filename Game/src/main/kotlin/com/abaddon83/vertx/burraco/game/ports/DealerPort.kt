package com.abaddon83.vertx.burraco.game.ports

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.vertx.burraco.game.commands.*
import io.vertx.core.Promise

interface DealerPort {
    val commandHandler: CommandHandler

    fun applyCardToPlayer(identity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): Promise<CmdResult>

    fun applyCardToPlayerDeck(identity: GameIdentity, playerDeckId: Int, card: Card): Promise<CmdResult>

    fun applyCardToDiscardDeck(identity: GameIdentity, card: Card): Promise<CmdResult>

    fun applyCardToDeck(identity: GameIdentity, card: Card): Promise<CmdResult>
}