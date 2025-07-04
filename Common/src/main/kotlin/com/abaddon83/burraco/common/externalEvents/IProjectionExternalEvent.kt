package com.abaddon83.burraco.common.externalEvents

import io.github.abaddon.kcqrs.core.projections.IProjection

interface IProjectionExternalEvent : IProjection {

    fun applyEvent(event: ExternalEvent): IProjection

    fun withPosition(event: ExternalEvent): IProjection

}