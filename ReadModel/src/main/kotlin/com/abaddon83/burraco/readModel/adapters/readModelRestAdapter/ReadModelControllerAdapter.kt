package com.abaddon83.burraco.readModel.adapters.readModelRestAdapter

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.readModel.ports.ReadModelControllerPort
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import io.vertx.core.Promise


class ReadModelControllerAdapter(override val readModelRepository: RepositoryPort) : ReadModelControllerPort {

    override fun findBurracoGame(gameIdentity: GameIdentity): Promise<BurracoGame> {
        TODO("Not yet implemented")
    }

//    override fun findBurracoGame(gameIdentity: GameIdentity): BurracoGame? {
//        val query= GetBurracoGame(gameIdentity.toString())
//        return when(val entity = queryHandle.handle(query).firstOrNull()){
//            is ReadBurracoGame -> entity
//            else -> null
//        }
//    }

    override fun showPlayerCards(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<List<Card>> {
        TODO("Not yet implemented")
    }
}