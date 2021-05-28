package com.abaddon83.burraco.readModel.ports

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.readModel.projections.BurracoGame
import io.vertx.core.Promise

//
//import com.abaddon83.burraco.readModel.projections.BurracoGame
//import com.abaddon83.burraco.readModel.queries.QueryHandler
//import java.util.*
//
//
interface ReadModelControllerPort {
    val readModelRepository: RepositoryPort
//
//    val queryHandle: QueryHandler
//        get() = QueryHandler(readModelRepository)
//
//    fun findBurracoGame(gameIdentity: UUID): BurracoGame?
//
//}
////
////    val service: ReadModelService
////        get() = ReadModelService
////
    fun findBurracoGame(gameIdentity: GameIdentity): Promise<BurracoGame>
    fun showPlayerCards(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<List<Card>>
}