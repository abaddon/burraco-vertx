package com.abaddon83.burraco.readModel.ports
//
//import com.abaddon83.burraco.readModel.projections.BurracoGame
//import com.abaddon83.burraco.readModel.queries.QueryHandler
//import java.util.*
//
//
//interface ReadModelControllerPort {
//    val readModelRepository: RepositoryPort
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
// suspend fun findBurracoGame(gameIdentity: UUID): BurracoGame?
////    suspend fun showPlayerCards(gameIdentity: UUID, playerIdentity: UUID): List<Card>
////}