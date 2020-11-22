package com.abaddon83.burraco.readModel.projections

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Ranks
import com.abaddon83.burraco.common.models.valueObjects.Suits
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class BurracoGameTest {

    @Test
    fun serialiseBurracoGame(){
         val burracoGame = BurracoGame(
             identity = GameIdentity.create(),
             status = GameStatus.Waiting,
             deck = listOf(Card(suit = Suits.Heart,rank = Ranks.Ace),Card(suit = Suits.Clover,rank = Ranks.Two)),
             players = listOf(PlayerIdentity.create(), PlayerIdentity.create()),
             playerTurn = PlayerIdentity.create(),
             numMazzettoAvailable = 0,
             discardPile = listOf()
         )

            val jsonPlayers = Json.encodeToString(burracoGame.players)

            val players = Json.decodeFromString<List<PlayerIdentity>>(jsonPlayers)
            assertEquals(burracoGame.players,players)


    }
}