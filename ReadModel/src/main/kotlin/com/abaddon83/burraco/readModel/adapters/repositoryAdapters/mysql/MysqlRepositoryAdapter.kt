package com.abaddon83.burraco.readModel.adapters.repositoryAdapters.mysql

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GamePlayer
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect
import org.slf4j.LoggerFactory

class MysqlRepositoryAdapter: RepositoryPort {
    companion object {
        private const val username = "root"
        private const val password = "root"
        private const val host = "localhost"
        private const val port = "3306"
        private const val database = "readmodel_game"

        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

    private var mysql: Database? = null

    private fun getConnection(): Database {
        if(mysql == null){
            mysql = Database.connect(
                url = "jdbc:mysql://$host:$port/$database",
                driver = "com.mysql.cj.jdbc.Driver",
                user= username,
                password = password,
                dialect = MySqlDialect()
            )
        }
        return mysql as Database
    }

    override fun persist(entity: BurracoGame) {
        if(Queries.insertOrUpdate(getConnection(),entity)){
            log.info("${entity.javaClass.simpleName} persisted")
        }else{
            log.warn("${entity.javaClass.simpleName} not persisted")
        }
    }

    override fun findGame(gameIdentity: GameIdentity): BurracoGame {
        return Queries.selectGame(getConnection(), gameIdentity)
    }

    override fun getAllBurracoGame(): List<BurracoGame> {
        return Queries.selectAllGame(getConnection())
    }

    override fun persist(entity: GamePlayer) {
        if(Queries.insertOrUpdate(getConnection(),entity)){
            log.info("${entity.javaClass.simpleName} persisted")
        }else{
            log.warn("${entity.javaClass.simpleName} not persisted")
        }
    }

    override fun findGamePlayer(playerIdentity: PlayerIdentity, gameIdentity: GameIdentity): GamePlayer {
        return Queries.selectGamePlayer(getConnection(), playerIdentity,gameIdentity)
    }
}