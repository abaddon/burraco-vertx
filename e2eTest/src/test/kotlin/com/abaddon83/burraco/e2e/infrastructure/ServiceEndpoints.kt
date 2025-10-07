package com.abaddon83.burraco.e2e.infrastructure

/**
 * Holds the service endpoints after containers are started.
 * These endpoints use dynamic ports assigned by Testcontainers.
 */
data class ServiceEndpoints(
    val gameServiceUrl: String,
    val playerServiceUrl: String,
    val kafkaBroker: String,
    val eventStoreUrl: String
) {
    companion object {
        @Volatile
        private var instance: ServiceEndpoints? = null

        fun set(endpoints: ServiceEndpoints) {
            instance = endpoints
        }

        fun get(): ServiceEndpoints {
            return instance ?: throw IllegalStateException("ServiceEndpoints not initialized. Ensure containers are started.")
        }
    }
}
