package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.helpers.hasAtLeastABurraco
import com.abaddon83.burraco.game.models.Team
import com.abaddon83.burraco.game.models.decks.Deck
import com.abaddon83.burraco.game.models.decks.DiscardPile
import com.abaddon83.burraco.game.models.decks.PlayerDeck
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.models.player.PlayerInGame

abstract class GameExecution(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<PlayerInGame>,
    private val playerTurn: PlayerIdentity,
    private val deck: Deck,
    private val playerDeck1: PlayerDeck?,
    private val playerDeck2: PlayerDeck?,
    private val discardPile: DiscardPile,
    private val teams: List<Team>
) : Game() {

    fun teamsHasAtLeastBurraco(team: Team): Boolean =
        players.filter { team.members.contains(it.id) }.hasAtLeastABurraco()

}