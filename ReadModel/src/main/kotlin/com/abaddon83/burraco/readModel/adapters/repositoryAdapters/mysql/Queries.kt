package com.abaddon83.burraco.readModel.adapters.repositoryAdapters.mysql

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GameStatus
import io.vertx.core.logging.LoggerFactory
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.support.mysql.insertOrUpdate

object Queries {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)

    fun insertOrUpdate(mysql: Database, entity: BurracoGame): Boolean {
        try {
            val effectedRow = mysql.insertOrUpdate(GameTable) {
                set(it.identity, entity.identity.toString())
                set(it.status, entity.status.toString())
                set(it.deck, Json.encodeToString(entity.deck))
                set(it.playerTurn, entity.playerTurn?.toString())
                set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                set(it.discardPile, Json.encodeToString(entity.discardPile))
                onDuplicateKey {
                    set(it.status, entity.status.toString())
                    set(it.deck, Json.encodeToString(entity.deck))
                    set(it.playerTurn, entity.playerTurn?.toString())
                    set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                    set(it.discardPile, Json.encodeToString(entity.discardPile))
                }
            }
            return when (effectedRow) {
                1 -> true
                else -> false
            }
        } catch (ex: Exception) {
            log.error("Insert Or Update query failed")
            ex.printStackTrace()
            return false
        }
    }

    fun insert(mysql: Database, entity: BurracoGame): Boolean {
        try {
            val effectedRow = mysql.insert(GameTable) {
                set(it.identity, entity.identity.toString())
                set(it.status, entity.status.toString())
                set(it.deck, Json.encodeToString(entity.deck))
                set(it.playerTurn, entity.playerTurn?.toString())
                set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                set(it.discardPile, Json.encodeToString(entity.discardPile))
            }
            return when (effectedRow) {
                1 -> true
                else -> false
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }
    }

    fun update(mysql: Database, entity: BurracoGame): Boolean {
        try {
            val effectedRow = mysql.update(GameTable) {
                set(it.status, entity.status.toString())
                set(it.deck, Json.encodeToString(entity.deck))
                set(it.playerTurn, entity.playerTurn?.toString())
                set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                set(it.discardPile, Json.encodeToString(entity.discardPile))
                where {
                    it.identity eq entity.identity.toString()
                }
            }
            return when (effectedRow) {
                1 -> true
                else -> false
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }
    }

    fun selectGame(mysql: Database, identity: GameIdentity): BurracoGame {
        val query = mysql
            .from(GameTable)
            .select()
            .where { GameTable.identity eq identity.toString() }
            .limit(0, 1)
        return query.map { row ->
            BurracoGame(
                GameIdentity.create(row[GameTable.identity]!!)!!,
                status = GameStatus.valueOf(row[GameTable.status]!!),
                deck = listOf()/*Json.decodeFromString(row[GameTable.deck]!!)*/,
                playerTurn = if (row[GameTable.playerTurn].isNullOrEmpty()) null else PlayerIdentity.create(row[GameTable.playerTurn]!!),
                numMazzettoAvailable = row[GameTable.numMazzettoAvailable]!!,
                discardPile = listOf()/*Json.decodeFromString(row[GameTable.discardPile]!!*/
            )
        }.getOrElse(0) { _ -> BurracoGame() }
    }

    fun selectAllGame(mysql: Database): List<BurracoGame> {
        val query = mysql
            .from(GameTable)
            .select()
            .limit(0, 1)
        return query.map { row ->
            BurracoGame(
                GameIdentity.create(row[GameTable.identity]!!)!!,
                status = GameStatus.valueOf(row[GameTable.status]!!),
                deck = listOf()/*Json.decodeFromString(row[GameTable.deck]!!)*/,
                playerTurn = if (row[GameTable.playerTurn].isNullOrEmpty()) null else PlayerIdentity.create(row[GameTable.playerTurn]!!),
                numMazzettoAvailable = row[GameTable.numMazzettoAvailable]!!,
                discardPile = listOf()/*Json.decodeFromString(row[GameTable.discardPile]!!*/
            )
        }
    }
}