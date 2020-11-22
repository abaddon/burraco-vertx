package com.abaddon83.burraco.readModel.adapters.repositoryAdapters.mysql

import com.abaddon83.burraco.common.models.identities.*
import com.abaddon83.burraco.common.serializations.toJson
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.burraco.readModel.projections.GameStatus
import com.abaddon83.burraco.readModel.projections.toJson
import io.vertx.core.logging.LoggerFactory
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.support.mysql.insertOrUpdate

object Queries {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)

    //BurracoGame
    fun insertOrUpdate(mysql: Database, entity: BurracoGame): Boolean {
        try {

            val effectedRow = mysql.insertOrUpdate(GameTable) {
                set(it.identity, entity.identity.convertTo().toString())
                set(it.status, entity.status.toJson())
                set(it.deck, entity.deck.toJson())
                set(it.players, entity.players.toJson())
                set(it.playerTurn, entity.playerTurn?.toJson())
                set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                set(it.discardPile, entity.discardPile.toJson())
                onDuplicateKey {
                    set(it.status, entity.status.toJson())
                    set(it.deck, entity.deck.toJson())
                    set(it.players, entity.players.toJson())
                    set(it.playerTurn, entity.playerTurn?.toJson())
                    set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                    set(it.discardPile, entity.discardPile.toJson())
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
                set(it.identity, entity.identity.convertTo().toString())
                set(it.status, entity.status.toJson())
                set(it.deck, entity.deck.toJson())
                set(it.players, entity.players.toJson())
                set(it.playerTurn, entity.playerTurn?.toJson())
                set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                set(it.discardPile, entity.discardPile.toJson())
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
                set(it.status, entity.status.toJson())
                set(it.deck, entity.deck.toJson())
                set(it.players, entity.players.toJson())
                set(it.playerTurn, entity.playerTurn?.toJson())
                set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                set(it.discardPile, entity.discardPile.toJson())
                where {
                    it.identity eq entity.identity.convertTo().toString()
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
            .where { GameTable.identity eq identity.convertTo().toString() }
            .limit(0, 1)
        return query.map { row ->
            BurracoGame(
                identity = GameIdentity.create(row[GameTable.identity]!!)!!,
                status = GameStatus.valueOf(row[GameTable.status]!!),
                deck = Json.decodeFromString(row[GameTable.deck]!!),
                players = Json.decodeFromString(row[GameTable.players]!!),
                playerTurn = if (row[GameTable.playerTurn].isNullOrEmpty()) null else PlayerIdentity.create(row[GameTable.playerTurn]!!),
                numMazzettoAvailable = row[GameTable.numMazzettoAvailable]!!,
                discardPile = Json.decodeFromString(row[GameTable.discardPile]!!
                )
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
                deck = Json.decodeFromString(row[GameTable.deck]!!),
                playerTurn = if (row[GameTable.playerTurn].isNullOrEmpty()) null else PlayerIdentity.create(row[GameTable.playerTurn]!!),
                numMazzettoAvailable = row[GameTable.numMazzettoAvailable]!!,
                discardPile = Json.decodeFromString(row[GameTable.discardPile]!!)
            )
        }
    }

    //GamePlayer
    fun insertOrUpdate(mysql: Database, entity: GamePlayer): Boolean {
        try {
            val effectedRow = mysql.insertOrUpdate(GamePlayerTable) {
                set(it.identity, entity.identity.convertTo().toString())
                set(it.gameIdentity, entity.gameIdentity.convertTo().toString())
                set(it.handCards, entity.handCards.toJson())
                set(it.tris, entity.tris.toJson())
                set(it.scale, entity.scale.toJson())
                onDuplicateKey {
                    set(it.handCards, entity.handCards.toJson())
                    set(it.tris, entity.tris.toJson())
                    set(it.scale, entity.scale.toJson())
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

    fun selectGamePlayer(mysql: Database, identity: PlayerIdentity): GamePlayer {
        val query = mysql
            .from(GamePlayerTable)
            .select()
            .where { GamePlayerTable.identity eq identity.convertTo().toString() }
            .limit(0, 1)
        return query.map { row ->
            GamePlayer(
                PlayerIdentity.create(row[GamePlayerTable.identity]!!)!!,
                GameIdentity.create(row[GamePlayerTable.gameIdentity]!!)!!,
                handCards = Json.decodeFromString(row[GamePlayerTable.handCards]!!),
                tris = Json.decodeFromString(row[GamePlayerTable.tris]!!),
                scale = Json.decodeFromString(row[GamePlayerTable.scale]!!)
            )
        }.getOrElse(0) { _ -> GamePlayer() }
    }
}