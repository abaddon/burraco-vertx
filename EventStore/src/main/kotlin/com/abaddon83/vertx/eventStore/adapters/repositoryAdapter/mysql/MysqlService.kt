package com.abaddon83.vertx.eventStore.adapters.repositoryAdapter.mysql

import io.vertx.core.logging.LoggerFactory
import org.ktorm.database.Database
import java.lang.reflect.Array.newInstance
import java.lang.reflect.Constructor
import java.sql.*
import java.util.*

//object MysqlService {
//    private var conn: Connection? = null
//    private val username = "root"
//    private val password = "root"
//    private val host = "repository"//""127.0.0.1"
//    private val port = "3306"
//    private val database = "eventstore"
//    private val log = LoggerFactory.getLogger(this::class.qualifiedName)
//
//    fun getConnection(): Database {
//        return Database.connect("jdbc:mysql://$host:$port/$database?user=$username&password=$password")
//
//    }
//}