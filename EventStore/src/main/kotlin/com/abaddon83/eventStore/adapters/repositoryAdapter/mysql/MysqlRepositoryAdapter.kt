package com.abaddon83.eventStore.adapters.repositoryAdapter.mysql

import com.abaddon83.eventStore.adapters.repositoryAdapter.mysql.config.MysqlRepositoryConfig
import com.abaddon83.eventStore.models.Event
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.eventStore.commands.EventError
import com.abaddon83.eventStore.ports.OutcomeDetail
import com.abaddon83.eventStore.ports.RepositoryPort
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.uuid
import org.slf4j.LoggerFactory
import java.util.*

class MysqlRepositoryAdapter(repositoryConfig: MysqlRepositoryConfig): RepositoryPort {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

    private val mysql: Database = Database.connect(
        url = repositoryConfig.connectionUrl(),
        driver = repositoryConfig.driver,
        user= repositoryConfig.username,
        password = repositoryConfig.password);


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

    override fun findEvents(entityName: String, entityKey: String): List<Event> {
        val query = mysql
            .from(EventTable)
            .select()
            .where {(EventTable.entityName eq entityName) and (EventTable.entityKey eq entityKey)  }
            .orderBy(EventTable.instant.asc())

        return query.map { row ->
            Event(
                name = row[EventTable.name]!!,
                entityKey = UUID.fromString(row[EventTable.entityKey]!!),
                entityName = row[EventTable.entityName]!!,
                instant = row[EventTable.instant]!!,
                jsonPayload = row[EventTable.jsonPayload]!!
            )
        }
    }
}