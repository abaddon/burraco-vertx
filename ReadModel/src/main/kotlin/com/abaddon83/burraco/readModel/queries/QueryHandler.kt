package com.abaddon83.burraco.readModel.queries

//import com.abaddon83.burracoGame.readModel.models.ReadEntity
//import com.abaddon83.burracoGame.readModel.ports.ReadModelRepositoryPort
//
//data class QueryMsg(val query: Query, val response: List<ReadEntity>)
//
//
//
//class QueryHandler(readModelRepository: ReadModelRepositoryPort) {
//
//    companion object {
//        private val log = LoggerFactory.getLogger(this::class.java.simpleName)
//    }
//
//    val repository = readModelRepository
//
//    fun handle(q: Query):List<ReadEntity> {
//
//        val msg = QueryMsg(q, processQuery(q))
//
//        return msg.response
//
//    }
//
//    private fun processQuery(q: Query): List<ReadEntity> {
//        println("Processing $q")
//
//        return when(q){
//            GetAllBurracoGames -> repository.getAllBurracoGame()
//            is GetBurracoGame -> repository.get(q.identity)?.run { listOf(this)}?: emptyList()
//        }
//    }
//}

