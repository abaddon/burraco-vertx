package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.models.player.Player
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import org.slf4j.Logger

abstract class Game() : AggregateRoot() {
    protected abstract val log: Logger
    abstract val players: List<Player>
    override val uncommittedEvents: MutableCollection<IDomainEvent> = mutableListOf()
}