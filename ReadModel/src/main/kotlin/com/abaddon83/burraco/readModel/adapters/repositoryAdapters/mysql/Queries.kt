package com.abaddon83.burraco.readModel.adapters.repositoryAdapters.mysql

import com.abaddon83.burraco.common.models.identities.*
import com.abaddon83.burraco.common.serializations.toJson
import com.abaddon83.burraco.readModel.projections.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.support.mysql.insertOrUpdate
import org.slf4j.LoggerFactory

object Queries {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)

    //BurracoGame
    fun insertOrUpdate(mysql: Database, entity: BurracoGame): Boolean {
        return try {
            val effectedRow = mysql.insertOrUpdate(GameTable) {
                set(it.identity, entity.identity.convertTo().toString())
                set(it.status, entity.status.toJson())
                set(it.deck, Json.encodeToString(entity.deck))
                set(it.players, Json.encodeToString(entity.players))
                set(it.playerTurn, entity.playerTurn?.toJson())
                set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                set(it.discardPile, Json.encodeToString(entity.discardPile))
                onDuplicateKey {
                    set(it.status, entity.status.toJson())
                    set(it.deck, Json.encodeToString(entity.deck))
                    set(it.players, Json.encodeToString(entity.players))
                    set(it.playerTurn, entity.playerTurn?.toJson())
                    set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                    set(it.discardPile, Json.encodeToString(entity.discardPile))
                }
            }
            when (effectedRow) {
                1 -> true
                else -> false
            }
        } catch (ex: Exception) {
            log.error("Insert Or Update query failed")
            ex.printStackTrace()
            false
        }
    }

    fun insert(mysql: Database, entity: BurracoGame): Boolean {
        return try {
            val effectedRow = mysql.insert(GameTable) {
                set(it.identity, entity.identity.convertTo().toString())
                set(it.status, entity.status.toJson())
                set(it.deck, Json.encodeToString(entity.deck))
                set(it.players, Json.encodeToString(entity.players))
                set(it.playerTurn, entity.playerTurn?.toJson())
                set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                set(it.discardPile, Json.encodeToString(entity.discardPile))
            }
            when (effectedRow) {
                1 -> true
                else -> false
            }
        } catch (ex: Exception) {
            log.error("Insert Or Update query failed")
            ex.printStackTrace()
            false
        }
    }

    fun update(mysql: Database, entity: BurracoGame): Boolean {
        try {
            val effectedRow = mysql.update(GameTable) {
                set(it.status, entity.status.toJson())
                set(it.deck, Json.encodeToString(entity.deck))
                set(it.players, Json.encodeToString(entity.players))
                set(it.playerTurn, entity.playerTurn?.toJson())
                set(it.numMazzettoAvailable, entity.numMazzettoAvailable)
                set(it.discardPile, Json.encodeToString(entity.discardPile))
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
            val gameIdentity = GameIdentity.create(row[GameTable.identity]!!)!!
            BurracoGame(
                key = BurracoGameKey(gameIdentity),
                identity = gameIdentity,
                status = GameStatus.valueOf(row[GameTable.status]!!),
                deck = Json.decodeFromString(row[GameTable.deck]!!),
                players = Json.decodeFromString(row[GameTable.players]!!),
                playerTurn = if (row[GameTable.playerTurn].isNullOrEmpty()) null else PlayerIdentity.create(row[GameTable.playerTurn]!!),
                numMazzettoAvailable = row[GameTable.numMazzettoAvailable]!!,
                discardPile = Json.decodeFromString(
                    row[GameTable.discardPile]!!
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
            val gameIdentity = GameIdentity.create(row[GameTable.identity]!!)!!
            BurracoGame(
                key = BurracoGameKey(gameIdentity),
                identity = gameIdentity,
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
                set(it.handCards, Json.encodeToString(entity.handCards))
                set(it.tris, Json.encodeToString(entity.tris))
                set(it.scale, Json.encodeToString(entity.scale))
                onDuplicateKey {
                    set(it.handCards, Json.encodeToString(entity.handCards))
                    set(it.tris, Json.encodeToString(entity.tris))
                    set(it.scale, Json.encodeToString(entity.scale))
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

    fun selectGamePlayer(mysql: Database, playerIdentity: PlayerIdentity, gameIdentity: GameIdentity): GamePlayer {
        val query = mysql
            .from(GamePlayerTable)
            .select()
            .where {
                (GamePlayerTable.identity eq playerIdentity.convertTo().toString()) and
                        (GamePlayerTable.gameIdentity eq gameIdentity.convertTo().toString())
            }
            .limit(0, 1)
        return query.map { row ->
            val playerIdentity = PlayerIdentity.create(row[GamePlayerTable.identity]!!)!!
            val gameIdentity = GameIdentity.create(row[GamePlayerTable.gameIdentity]!!)!!
            GamePlayer(
                key = GamePlayerKey(playerIdentity,gameIdentity),
                identity = playerIdentity,
                gameIdentity = gameIdentity,
                handCards = Json.decodeFromString(row[GamePlayerTable.handCards]!!),
                tris = Json.decodeFromString(row[GamePlayerTable.tris]!!),
                scale = Json.decodeFromString(row[GamePlayerTable.scale]!!)
            )
        }.getOrElse(0) { _ -> GamePlayer() }
    }
}