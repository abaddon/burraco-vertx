package com.abaddon83.utils.ddd


abstract class Identity<T>(private val id: T){
    fun convertTo(): T {
        return id
    }
    override fun toString(): String = "$${id.toString()}"
}