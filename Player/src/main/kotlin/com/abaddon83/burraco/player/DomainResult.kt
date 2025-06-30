package com.abaddon83.burraco.player

import com.abaddon83.burraco.player.model.player.Player
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

data class DomainResult(val events: Iterable<IDomainEvent>, val player: Player)