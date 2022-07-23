//package com.abaddon83.burraco.dealer.models
//
//import com.abaddon83.burraco.dealer.helpers.CardsHelper
//
//class BurracoDealer(val players: List<PlayerIdentity>) {
//    private val numCardsForPlayer = 11
//    private val numCardsForDiscardPile = 1
//    private val numCardsMazzetto: Array<Int> = arrayOf(11, 18)
//    private val cards: MutableList<Card> = listOf(
//        CardsHelper.allRanksWithJollyCards(),
//        CardsHelper.allRanksWithJollyCards()
//    ).flatten().shuffled().toMutableList()
//
//    private val cardPlayers: Map<PlayerIdentity,List<Card>>;
//    private val playerDecks: Map<Int,List<Card>>;
//    private val discardCard: Card;
//
//    init {
//        val totalCards = cards.size
//        //player cards
//        cardPlayers = dealPlayersCards();
//        //player decks
//        playerDecks = dealPlayerDecks(if (players.size == 3) 18 else 11, 11);
//        //discard card
//        discardCard = dealDiscardCard();
//
//        val assignedCards = getDealPlayersCardsTotalCards()+
//                getPlayerDecksTotalCards()+
//                getDiscardDeckTotalCards()+
//                getDeckTotalCards()
//        assert(assignedCards == totalCards){"Total cards assigned doesn't match with the initial deck size: $assignedCards != $totalCards"}
//    }
//
//    private fun dealDiscardCard(): Card{
//        return cards.removeAt(0);
//    }
//
//    private fun dealPlayerDecks(playerDeck1NumCards: Int,playerDeck2NumCards: Int): Map<Int,List<Card>>{
//        val cardsToAssign = playerDeck1NumCards + playerDeck2NumCards;
//        val playerDecks: MutableMap<Int,List<Card>> = mapOf(0 to listOf<Card>(),1 to listOf<Card>(),).toMutableMap()
//        (0 until cardsToAssign).forEach { index ->
//            val card = cards.removeAt(0)
//            val playerDeckId = index % 2
//            playerDecks[playerDeckId] = playerDecks[playerDeckId]!!.plus(card)
//        }
//        return playerDecks;
//    }
//
//    private fun dealPlayersCards(): Map<PlayerIdentity,List<Card>>{
//        val cardsToAssign = players.size * numCardsForPlayer;
//        val cardPlayers: MutableMap<PlayerIdentity,List<Card>> = players.map { it to listOf<Card>() }.toMap().toMutableMap()
//        (0 until cardsToAssign).forEach { index ->
//            val card = cards.removeAt(0)
//            val idPlayer = index % players.size
//            cardPlayers[players[idPlayer]] = cardPlayers[players[idPlayer]]!!.plus(card)
//        }
//        return cardPlayers;
//    }
//
//    fun getDealPlayersCards(playerIdentity: PlayerIdentity): List<Card>{
//        return cardPlayers.get(playerIdentity).orEmpty();
//    }
//
//    private fun getDealPlayersCardsTotalCards(): Int{
//        //return cardPlayers.mapValues { cards -> cards.value.size }.count()
//        return cardPlayers.map{ player -> player.value.size }.fold(0){sum, num -> sum + num}
//    }
//
//    fun getPlayerDeck1(): List<Card>{
//        return playerDecks[0].orEmpty();
//    }
//
//    fun getPlayerDeck2(): List<Card>{
//        return playerDecks[1].orEmpty();
//    }
//
//    private fun getPlayerDecksTotalCards(): Int{
//        return playerDecks.mapValues { cards -> cards.value.size }.count()
//    }
//
//    fun getDiscardDeck(): Card{
//        return discardCard;
//    }
//
//    private fun getDiscardDeckTotalCards(): Int{
//        return 1;
//    }
//
//    fun getDeck(): List<Card>{
//        return cards
//    }
//
//    private fun getDeckTotalCards(): Int{
//        return cards.size
//    }
//
//
//
////    companion object Factory {
////        fun create(burracoDeck: BurracoDeck, burracoPlayers: List<BurracoPlayer>): BurracoDealer {
////            val deckShuffled = deckShuffle(BurracoDeck.create())
////            return BurracoDealer(deckShuffled, burracoPlayers)
////        }
////        private fun deckShuffle(deck: BurracoDeck): BurracoDeck = deck.shuffle()
////    }
//
//
//
////    val dealCardsToPlayers: Map<PlayerIdentity, List<Card>> = burracoPlayers.map { player ->
////        dealCardsToPlayer(player.identity())
////    }.reduce { acc, next -> acc + next }
////
////    val dealCardsToFirstMazzetto: PlayerDeck = PlayerDeck.create(grabCards(numCardsMazzetto[burracoPlayers.size % 2]))
////    val dealCardsToSecondMazzetto: PlayerDeck = PlayerDeck.create(grabCards(numCardsMazzetto[0]))
////    val dealCardToDiscardPile: DiscardPile = DiscardPile.create(grabCards(numCardsForDiscardPile))
////
////    private fun grabCards(numCards: Int): List<Card> = (1..numCards).map { _ -> burracoDeck.grabFirstCard() }.toList()
////
////    private fun dealCardsToPlayer(playerIdentity: PlayerIdentity): Map<PlayerIdentity, List<Card>> = mapOf(Pair(playerIdentity, grabCards(numCardsForPlayer)))
//
//}