package com.abaddon83.burraco.game

import com.abaddon83.burraco.game.models.game.Game
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

data class DomainResult(val events: Iterable<IDomainEvent>, val game: Game)

