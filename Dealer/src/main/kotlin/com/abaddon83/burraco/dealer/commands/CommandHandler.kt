//package com.abaddon83.burraco.dealer.commands
//
//import com.abaddon83.burraco.common.events.*
//import com.abaddon83.burraco.common.models.identities.GameIdentity
//import com.abaddon83.utils.ddd.Event
//import com.abaddon83.utils.functionals.*
//import com.abaddon83.utils.logs.WithLog
//import com.abaddon83.burraco.dealer.models.BurracoDealer
//import com.abaddon83.burraco.dealer.ports.EventBrokerProducerPort
//import io.vertx.core.json.JsonObject
//import kotlinx.serialization.json.jsonObject
//
//typealias CmdResult = Validated<DomainError, DomainResult>
//
//data class DomainResult(val gameIdentity: GameIdentity, val events: Iterable<Event>)
//
//data class CommandMsg(val command: Command, val response: CmdResult) // a command with a result
//
////io.vertx.kafka.client.serialization.JsonObjectSerializer
//class CommandHandler(val eventBrokerProducer: EventBrokerProducerPort<String, String>) : WithLog("CommandHandler") {
//
//    private val TOPIC: String = "dealer"
//
//    fun handle(cmd: Command): CmdResult =
//        CommandMsg(cmd, Valid(DomainResult(GameIdentity.create(),listOf()))).let {
//            executeCommand(it).response
//        }
//
//    private fun executeCommand(msg: CommandMsg): CommandMsg {
//        val res = processPoly(msg.command)
//        if (res is Valid) {
//            publishEvent(res.value)
//        }
//        return msg.copy(response = res)
//    }
//
//    private fun processPoly(c: Command): CmdResult {
//
//        log.debug("Processing $c")
//
//        val cmdResult = when (c) {
//            is DealCards -> execute(c)
//            else -> TODO()
//        }
//        return cmdResult
//    }
//
//    private fun execute(c: DealCards): CmdResult {
//        val burracoDealer = BurracoDealer(c.players)
//        val events = mutableListOf<Event>()
//        //PlayersCards
//        c.players.forEach { playerIdentity ->
//            burracoDealer.getDealPlayersCards(playerIdentity).forEach{ card ->
//                events.add(CardAssignedToPlayer(identity=c.gameIdentity, player = playerIdentity,card = card))
//            }
//        }
//        //Player Deck1
//        burracoDealer.getPlayerDeck1().forEach{ card ->
//            events.add(CardAssignedToPlayerDeck(identity=c.gameIdentity, playerDeckId = 0, card = card))
//        }
//        //Player Deck2
//        burracoDealer.getPlayerDeck2().forEach{ card ->
//            events.add(CardAssignedToPlayerDeck(identity=c.gameIdentity, playerDeckId = 1, card = card))
//        }
//        //Discard Deck
//        events.add(CardAssignedToDiscardDeck(identity = c.gameIdentity, burracoDealer.getDiscardDeck()))
//        //Deck
//        burracoDealer.getDeck().forEach{ card ->
//            events.add(CardAssignedToDeck(identity=c.gameIdentity, card = card))
//        }
//
//        return Valid(DomainResult(c.gameIdentity,events))
//    }
//
//    private fun publishEvent( domainResult : DomainResult){
//        val key = domainResult.gameIdentity.toString()
//        domainResult.events.forEach{event ->
//            eventBrokerProducer.publish(TOPIC,event)
////            val value =when(event){
////                is CardAssignedToPlayer -> event.toJson()
////                is CardAssignedToPlayerDeck -> event.toJson()
////                is CardAssignedToDiscardDeck -> event.toJson()
////                is CardAssignedToDeck -> event.toJson()
////                else -> null
////            }
////            if(value != null){
////                eventBrokerProducer.publish(TOPIC,key,JsonObject.mapFrom(value.jsonObject))
////            }
//        }
//
//
//
//    }
//}