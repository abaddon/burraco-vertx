package com.abaddon83.eventStore.adapters.repositoryAdapter.mysql.config

data class MysqlRepositoryConfig(
    val username: String,
    val password: String,
    val host: String,
    val port: Int,
    val database: String,
    val driver: String
){
    fun connectionUrl(): String = "jdbc:mysql://$host:$port/$database"

}
