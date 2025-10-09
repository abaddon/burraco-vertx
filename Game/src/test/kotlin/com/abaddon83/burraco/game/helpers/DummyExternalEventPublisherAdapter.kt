package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.common.models.event.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log

class DummyExternalEventPublisherAdapter : ExternalEventPublisherPort {
    override suspend fun publish(aggregate: Game, event: GameEvent): Result<Unit> {
        log.debug("event published: $event")
        return Result.success(Unit)
    }
}