package com.abaddon83.burraco.common.externalEvents

import io.github.abaddon.kcqrs.core.IIdentity

interface ExternalEvent {
    val eventName: String
    val eventOwner: String
    val aggregateIdentity: IIdentity

}
