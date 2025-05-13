package com.abaddon83.burraco.readModel.ports

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.burraco.readModel.commands.CmdResult
import com.abaddon83.burraco.readModel.commands.CommandImpl
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GamePlayer
import io.vertx.core.Promise

interface GamePort {

    val repository: RepositoryPort

    fun applyEvent(cmd: CommandImpl): Promise<CmdResult>

}