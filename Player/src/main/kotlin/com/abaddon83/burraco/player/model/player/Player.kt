package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

abstract class Player() : AggregateRoot(){
    abstract val gameIdentity: GameIdentity
    abstract val user: String
    override val uncommittedEvents: MutableCollection<IDomainEvent> = mutableListOf()
    
    companion object {
        fun empty(): Player = PlayerDraft.empty()
    }
}