package com.abaddon83.burraco.readModel.adapters.repositoryAdapters.mysql

import org.ktorm.schema.*


object GameTable : Table<Nothing>("game") {
    val identity = varchar("id").primaryKey()
    val status = varchar("status")
    val deck = text("deck")
    val playerTurn = varchar("player_turn")
    val numMazzettoAvailable = int("num_mazzetto_available")
    val discardPile= text("discard_pile")
    val players=text("players")
}

object GamePlayerTable : Table<Nothing>("game_player") {
    val identity = varchar("id").primaryKey()
    val gameIdentity = varchar("game_id").primaryKey()
    val handCards = text("hand_cards")
    val tris = text("tris")
    val scale = text("scale")
}

