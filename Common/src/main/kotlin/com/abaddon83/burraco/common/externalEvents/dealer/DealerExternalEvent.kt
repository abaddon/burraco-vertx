package com.abaddon83.burraco.common.externalEvents.dealer

import com.abaddon83.burraco.common.externalEvents.EventOwner
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.models.DealerIdentity
import io.vertx.core.json.Json

abstract class DealerExternalEvent(
    override val aggregateIdentity: DealerIdentity,
    name: DealerEventName
) : ExternalEvent {
    override val eventOwner: String = EventOwner.DEALER.name
    override val eventName: String = name.name

    override fun toKafkaEvent(): KafkaEvent {
        return KafkaEvent(eventName, Json.encode(this))
    }
}
