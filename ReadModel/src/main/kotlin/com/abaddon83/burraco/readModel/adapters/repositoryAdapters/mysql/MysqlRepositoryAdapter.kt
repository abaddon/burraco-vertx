package com.abaddon83.burraco.readModel.adapters.repositoryAdapters.mysql

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.utils.ddd.UUIDIdentity
import io.vertx.core.logging.LoggerFactory
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect

class MysqlRepositoryAdapter: RepositoryPort {
    companion object {
        private const val username = "root"
        private const val password = "root"
        private const val host = "event.repository"
        private const val port = "3306"
        private const val database = "game"

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
            log.info("record persisted")
        }else{
            log.error("record not persisted")
        }
    }

    override fun findGame(key: UUIDIdentity): BurracoGame {
        return Queries.selectGame(getConnection(), GameIdentity(key.convertTo()))
    }

    override fun getAllBurracoGame(): List<BurracoGame> {
        return Queries.selectAllGame(getConnection())
    }

    //TODO
    override fun persist(entity: GamePlayer) {

    }

    //TODO
    override fun findGamePlayer(key: UUIDIdentity): GamePlayer {
        return GamePlayer()
    }
}