//package com.abaddon83.burraco.dealer.ports
//
//import com.abaddon83.burraco.common.models.identities.GameIdentity
//import com.abaddon83.burraco.common.models.identities.PlayerIdentity
//import com.abaddon83.burraco.dealer.commands.CmdResult
//import com.abaddon83.burraco.dealer.commands.CommandHandler
//
//interface GamePort {
//
//    val commandHandler : CommandHandler
//
//    fun dealCards(identity: GameIdentity, players: List<PlayerIdentity>) : CmdResult
//}