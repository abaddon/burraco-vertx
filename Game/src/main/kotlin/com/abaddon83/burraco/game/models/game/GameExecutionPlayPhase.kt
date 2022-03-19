package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.events.game.CardAddedToTris
import com.abaddon83.burraco.game.events.game.TrisDropped
import com.abaddon83.burraco.game.helpers.*
import com.abaddon83.burraco.game.helpers.TrisHelper.validTris
import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.game.models.TrisIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.decks.Deck
import com.abaddon83.burraco.game.models.decks.DiscardPile
import com.abaddon83.burraco.game.models.decks.PlayerDeck
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import com.abaddon83.burraco.game.models.player.PlayerInGame
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class GameExecutionPlayPhase private constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<PlayerInGame>,
    val playerTurn: PlayerIdentity,
    val deck: Deck,
    val playerDeck1: PlayerDeck,
    val playerDeck2: PlayerDeck,
    val discardPile: DiscardPile
) : GameExecution(id, version, players, playerTurn, deck, playerDeck1, playerDeck2, discardPile) {
    override val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    companion object Factory {
        fun from(game: GameExecutionPickUpPhase): GameExecutionPlayPhase = GameExecutionPlayPhase(
            id = game.id,
            version = game.version,
            players = game.players,
            playerTurn = game.players.first().id,
            deck = game.deck,
            playerDeck1 = game.playerDeck1,
            playerDeck2 = game.playerDeck2,
            discardPile = game.discardPile
        )
    }

    fun dropTris(playerIdentity: PlayerIdentity, cards: List<Card>): GameExecutionPlayPhase {
        require(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        require(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        require(
            players.cardsBelongPlayer(
                playerIdentity,
                cards
            )
        ) { "Tris's cards don't belong to player ${playerIdentity.valueAsString()}" }
        require(validTris(cards)) { "It's not a valid tris" }

        return raiseEvent(TrisDropped.create(id, playerIdentity, cards)) as GameExecutionPlayPhase
    }

    fun appendCardsOnATris(
        playerIdentity: PlayerIdentity,
        cards: List<Card>,
        trisIdentity: TrisIdentity
    ): GameExecutionPlayPhase {
        require(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        require(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        require(
            players.cardsBelongPlayer(
                playerIdentity,
                cards
            )
        ) { "Cards to append to the tris ${trisIdentity.valueAsString()} don't belong to player ${playerIdentity.valueAsString()}" }
        require(
            players.trisBelongPlayer(
                playerIdentity,
                trisIdentity
            )
        ) { "Tris ${trisIdentity.valueAsString()} don't belong to player ${playerIdentity.valueAsString()}" }
        require(
            validTris(
                players.tris(playerIdentity, trisIdentity)?.cards
                    ?.plus(cards)
                    .orEmpty()
            )
        ) { "Cards can't be added to Tris ${trisIdentity.valueAsString()}" }
        return raiseEvent(CardAddedToTris.create(id, playerIdentity, trisIdentity, cards)) as GameExecutionPlayPhase
    }

    //
//    fun dropOnTableAScale(playerIdentity: PlayerIdentity, scale: BurracoScale): GameExecutionPlayPhase {
//        val player = validatePlayerId(playerIdentity)
//        validatePlayerTurn(playerIdentity)
//        player.dropAScale(scale)
//        return applyAndQueueEvent(
//            ScaleDropped(identity = identity(), playerIdentity = player.identity(), scale = scale)
//        )
//    }
//
//    fun appendCardsOnABurracoDropped(
//        playerIdentity: PlayerIdentity,
//        cardsToAppend: List<Card>,
//        burracoIdentity: BurracoIdentity
//    ): GameExecutionPlayPhase {
//        val player = validatePlayerId(playerIdentity)
//        validatePlayerTurn(playerIdentity)
//        player.appendACardOnBurracoDropped(burracoIdentity, cardsToAppend)
//        return applyAndQueueEvent(
//            CardAddedOnBurraco(
//                identity = identity(),
//                playerIdentity = player.identity(),
//                cardsToAppend = cardsToAppend,
//                burracoIdentity = burracoIdentity
//            )
//        )
//    }
//
//    fun pickupMazzetto(playerIdentity: PlayerIdentity): GameExecutionPlayPhase {
//        val player = validatePlayerId(playerIdentity)
//        validatePlayerTurn(playerIdentity)
//        log.debug("show cards: ${player.showMyCards()}")
//        check(
//            player.showMyCards().isEmpty()
//        ) { warnMsg("The player cannot pick up a Mazzetto if he still has cards ( num cards: ${player.showMyCards().size})") }
//        check(!player.isMazzettoTaken()) { warnMsg("The player cannot pick up a Mazzetto he already taken") }
//
//        val mazzetto = firstMazzettoAvailable()
//
//        return applyAndQueueEvent(
//            MazzettoPickedUp(
//                identity = identity(),
//                playerIdentity = player.identity(),
//                mazzettoDeck = mazzetto.getCardList()
//            )
//        )
//    }
//
//    fun dropCardOnDiscardPile(playerIdentity: PlayerIdentity, card: Card): BurracoGameExecutionTurnEnd {
//        val player = validatePlayerId(playerIdentity)
//        validatePlayerTurn(playerIdentity)
//        val eventCardDroppedIntoDiscardPile = CardDroppedIntoDiscardPile(identity, player.identity(), card = card)
//        return applyAndQueueEvent(eventCardDroppedIntoDiscardPile)
//    }
//
//    override fun applyEvent(event: Event): Game {
//        log.info("apply event: ${event::class.simpleName.toString()}")
//        return when (event) {
//            is CardDroppedIntoDiscardPile -> apply(event)
//            is TrisDropped -> apply(event)
//            is ScaleDropped -> apply(event)
//            is CardAddedOnBurraco -> apply(event)
//            is MazzettoPickedUp -> apply(event)
//            else -> throw UnsupportedEventException(event::class.java)
//        }
//    }
//
//    private fun apply(event: CardDroppedIntoDiscardPile): BurracoGameExecutionTurnEnd {
//        val player = players.find { p -> p.identity() == event.playerIdentity }!!
//        return BurracoGameExecutionTurnEnd.create(
//            identity = identity(),
//            players = UpdatePlayers(player.dropACard(event.card)),
//            playerTurn = playerTurn,
//            burracoDeck = burracoDeck,
//            playerDeck1 = playerDeck1,
//            playerDeck2 = playerDeck2,
//            discardPile = discardPile.addCard(event.card)
//        )
//    }
//
    private fun apply(event: TrisDropped): GameExecutionPlayPhase {
        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player ->
            Tris
            player.copy(
                cards = player.cards.minus(event.cards),
                listOfTris = player.listOfTris.plus(Tris.create(event.cards))
            )
        }
        return copy(players = updatedPlayers)
    }

    private fun apply(event: CardAddedToTris): GameExecutionPlayPhase {
        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player ->
            player.copy(
                cards = player.cards.minus(event.cards),
                listOfTris = player.listOfTris.updateTris(event.trisIdentity) { tris ->
                    tris.copy(cards = tris.cards.plus(event.cards))
                }
            )
        }
        return copy(players = updatedPlayers)
    }

//
//    private fun apply(event: ScaleDropped): GameExecutionPlayPhase {
//        val player = players.find { p -> p.identity() == event.playerIdentity }!!
//        return copy(
//            players = UpdatePlayers(player.dropAScale(event.scale))
//        )
//    }
//
//    private fun apply(event: CardAddedOnBurraco): GameExecutionPlayPhase {
//        val player = players.find { p -> p.identity() == event.playerIdentity }!!
//        return copy(
//            players = UpdatePlayers(player.appendACardOnBurracoDropped(event.burracoIdentity, event.cardsToAppend))
//        )
//    }
//
//    private fun apply(event: MazzettoPickedUp): GameExecutionPlayPhase {
//        val player = players.find { p -> p.identity() == event.playerIdentity }!!
//        val mazzettoDeck = PlayerDeck.create(event.mazzettoDeck)
//        return copy(
//            players = UpdatePlayers(player.pickUpMazzetto(mazzettoDeck)),
//            //mazzettoDecks = mazzettoDecks.mazzettoTaken(mazzettoDeck)
//        )
//    }


}