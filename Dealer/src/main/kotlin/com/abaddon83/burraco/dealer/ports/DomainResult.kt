package com.abaddon83.burraco.dealer.ports


import com.abaddon83.burraco.dealer.models.Dealer
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

data class DomainResult(val events: Iterable<IDomainEvent>, val dealer: Dealer)

