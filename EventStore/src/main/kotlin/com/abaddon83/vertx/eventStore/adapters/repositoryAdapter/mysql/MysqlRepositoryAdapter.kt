package com.abaddon83.vertx.eventStore.adapters.repositoryAdapter.mysql

import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.vertx.eventStore.commands.EventError
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.OutcomeDetail
import com.abaddon83.vertx.eventStore.ports.RepositoryPort
import io.vertx.core.logging.LoggerFactory
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.uuid
import java.util.*

class MysqlRepositoryAdapter: RepositoryPort {
    companion object {
        private const val username = "root"
        private const val password = "root"
        private const val host = "repository"
        private const val port = "3306"
        private const val database = "eventstore"

        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
        private val mysql = Database.connect(
            url = "jdbc:mysql://$host:$port/$database",
            driver = "com.mysql.cj.jdbc.Driver",
            user= username,
            password = password)
    }

    override fun save(event: Event): Validated<EventError, OutcomeDetail> {
        try{

            val effectedRow=mysql.insert(EventTable) {
                set(it.name, event.name)
                set(it.entityKey, event.entityKey.toString())
                set(it.entityName, event.entityName)
                set(it.instant, event.instant)
                set(it.jsonPayload, event.jsonPayload)
            }
            return when(effectedRow){
                1 -> Valid(mapOf("record" to "created"))
                else -> Invalid(EventError("Record not saved"))
            }
        }catch (ex: Exception){
            ex.printStackTrace()
            return Invalid(EventError(ex.cause.toString()))
        }

    }

    override fun findEvents(entityName: String, entityKey: String): Set<Event> {
        val query = mysql
            .from(EventTable)
            .select()
            .where {(EventTable.entityName eq entityName) and (EventTable.entityKey eq entityKey)  }
            .orderBy(EventTable.instant.desc())

        return query.map { row ->
            Event(
                name = row[EventTable.name]!!,
                entityKey = UUID.fromString(row[EventTable.entityKey]!!),
                entityName = row[EventTable.entityName]!!,
                instant = row[EventTable.instant]!!,
                jsonPayload = row[EventTable.jsonPayload]!!
            )
        }.toSet()
    }
}