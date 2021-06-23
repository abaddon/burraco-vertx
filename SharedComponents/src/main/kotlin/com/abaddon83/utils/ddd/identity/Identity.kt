package com.abaddon83.utils.ddd.identity

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


abstract class Identity<T>(){
    abstract val id: T
    fun convertTo(): T {
        return id
    }
    override fun toString(): String = "${id.toString()}"

    abstract fun isEmpty(): Boolean

    fun toJson(): String {
        return Json.encodeToString(this)
    }
}