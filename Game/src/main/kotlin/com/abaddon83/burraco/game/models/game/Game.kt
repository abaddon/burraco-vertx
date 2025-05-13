package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.models.player.Player
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

abstract class Game() : AggregateRoot() {
    abstract val players: List<Player>
    override val uncommittedEvents: MutableCollection<IDomainEvent> = mutableListOf()
}