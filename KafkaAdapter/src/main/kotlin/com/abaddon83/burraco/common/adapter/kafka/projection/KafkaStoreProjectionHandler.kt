package com.abaddon83.burraco.common.adapter.kafka.projection

import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository
import io.github.abaddon.kcqrs.core.projections.IProjection
import io.github.abaddon.kcqrs.core.projections.ProjectionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Base class for projection handlers that consume events from Kafka.
 *
 * @param TProjection The type of projection this handler manages
 * @param repository The repository for persisting projection state
 * @param scope Optional coroutine scope (defaults to IO dispatcher with SupervisorJob)
 */
abstract class KafkaStoreProjectionHandler<TProjection : IProjection>(
    repository: IProjectionRepository<TProjection>,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : ProjectionHandler<TProjection>(scope) {

    override val repository: IProjectionRepository<TProjection> = repository
}