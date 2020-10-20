package com.abaddon83.vertx.eventStore.adapters.repositoryAdapter.mysql

import org.ktorm.schema.*

//interface EventEntity : Entity<EventEntity> {
//    companion object : Entity.Factory<EventEntity>()
//    val name: String
//    val entityKey: UUID
//    val entityName: String
//    val instant: Instant
//    val jsonPayload: String
//}

object EventTable : Table<Nothing>("event") {
    val name = varchar("name")
    val entityKey = varchar("entity_key").primaryKey()
    val entityName = varchar("entity_name").primaryKey()
    val instant = timestamp("instant")
    val jsonPayload= text("json_payload")
}